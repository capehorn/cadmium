package cadmium

import (
	"bufio"
	"encoding/binary"
	"io"
	"math"
	"os"
	"runtime"
	"strings"
	"testing"
)

func Test_generateSTL(t *testing.T) {
	bbp := NewBicubicBezierPatch([16]Vector{
		V(0, 0, 0),
		V(10, 0, 10),
		V(20, 0, 10),
		V(30, 0, 0),

		V(0, 10, 0),
		V(10, 10, 20),
		V(20, 10, 20),
		V(30, 10, 0),

		V(0, 20, 0),
		V(10, 20, 40),
		V(20, 20, 40),
		V(30, 20, 0),

		V(0, 30, 0),
		V(10, 30, 10),
		V(20, 30, 10),
		V(30, 30, 0),
	})

	triangles := bbp.Triangulate(64, 64)

	triangles = addSplitted(bbp, triangles)

	mesh := NewTriangleMesh(triangles)

	// fit mesh in a bi-unit cube centered at the origin
	mesh.BiUnitCube()

	// smooth the normals
	mesh.SmoothNormalsThreshold(Radians(30))

	SaveSTL("../test/bicubicbezier.stl", mesh)
}

func addSplitted(bbp BicubicBezierPatch, triangles []*Triangle) []*Triangle {
	m := 8
	n := 8
	matrix := Translate(V(0, 0, 10))
	bbp0, bbp1, bbp2, bbp3 := bbp.SplitAt(0.2, 0.7)
	triangles = append(triangles, bbp0.Transform(matrix).Triangulate(m, n)...)
	triangles = append(triangles, bbp1.Transform(matrix).Triangulate(m, n)...)
	triangles = append(triangles, bbp2.Transform(matrix).Triangulate(m, n)...)
	triangles = append(triangles, bbp3.Transform(matrix).Triangulate(m, n)...)
	return triangles
}

type STLHeader struct {
	_     [80]uint8
	Count uint32
}

type STLTriangle struct {
	N, V1, V2, V3 [3]float32
	_             uint16
}

func LoadSTL(path string) (*Mesh, error) {
	// open file
	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	// get file size
	info, err := file.Stat()
	if err != nil {
		return nil, err
	}
	size := info.Size()

	// read header, get expected binary size
	header := STLHeader{}
	if err := binary.Read(file, binary.LittleEndian, &header); err != nil {
		return nil, err
	}
	expectedSize := int64(header.Count)*50 + 84

	// rewind to start of file
	_, err = file.Seek(0, 0)
	if err != nil {
		return nil, err
	}

	// parse ascii or binary stl
	if size == expectedSize {
		return loadSTLB(file)
	} else {
		return loadSTLA(file)
	}
}

func loadSTLA(file *os.File) (*Mesh, error) {
	var vertexes []Vector
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := scanner.Text()
		fields := strings.Fields(line)
		if len(fields) == 4 && fields[0] == "vertex" {
			f := ParseFloats(fields[1:])
			vertexes = append(vertexes, Vector{X: f[0], Y: f[1], Z: f[2]})
		}
	}
	var triangles []*Triangle
	for i := 0; i < len(vertexes); i += 3 {
		t := Triangle{}
		t.V1.Position = vertexes[i+0]
		t.V2.Position = vertexes[i+1]
		t.V3.Position = vertexes[i+2]
		t.FixNormals()
		triangles = append(triangles, &t)
	}
	return NewTriangleMesh(triangles), scanner.Err()
}

func makeFloat(b []byte) float64 {
	return float64(math.Float32frombits(binary.LittleEndian.Uint32(b)))
}

func loadSTLB(file *os.File) (*Mesh, error) {
	r := bufio.NewReader(file)
	header := STLHeader{}
	if err := binary.Read(r, binary.LittleEndian, &header); err != nil {
		return nil, err
	}
	count := int(header.Count)
	triangles := make([]*Triangle, count)
	_triangles := make([]Triangle, count)
	b := make([]byte, count*50)
	_, err := io.ReadFull(r, b)
	if err != nil {
		return nil, err
	}
	wn := runtime.NumCPU()
	ch := make(chan Box, wn)
	for wi := 0; wi < wn; wi++ {
		go func(wi, wn int) {
			var min, max Vector
			for i := wi; i < count; i += wn {
				j := i * 50
				v1 := Vector{X: makeFloat(b[j+12 : j+16]), Y: makeFloat(b[j+16 : j+20]), Z: makeFloat(b[j+20 : j+24])}
				v2 := Vector{X: makeFloat(b[j+24 : j+28]), Y: makeFloat(b[j+28 : j+32]), Z: makeFloat(b[j+32 : j+36])}
				v3 := Vector{X: makeFloat(b[j+36 : j+40]), Y: makeFloat(b[j+40 : j+44]), Z: makeFloat(b[j+44 : j+48])}
				t := &_triangles[i]
				t.V1.Position = v1
				t.V2.Position = v2
				t.V3.Position = v3
				n := t.Normal()
				t.V1.Normal = n
				t.V2.Normal = n
				t.V3.Normal = n
				if i == wi {
					min = v1
					max = v1
				}
				for _, v := range [3]Vector{v1, v2, v3} {
					if v.X < min.X {
						min.X = v.X
					}
					if v.Y < min.Y {
						min.Y = v.Y
					}
					if v.Z < min.Z {
						min.Z = v.Z
					}
					if v.X > max.X {
						max.X = v.X
					}
					if v.Y > max.Y {
						max.Y = v.Y
					}
					if v.Z > max.Z {
						max.Z = v.Z
					}
				}
				triangles[i] = t
			}
			ch <- Box{Min: min, Max: max}
		}(wi, wn)
	}
	box := EmptyBox
	for wi := 0; wi < wn; wi++ {
		box = box.Extend(<-ch)
	}
	mesh := NewTriangleMeshWithBox(triangles, &box)
	return mesh, nil
}

func SaveSTL(path string, mesh *Mesh) error {
	file, err := os.Create(path)
	if err != nil {
		return err
	}
	defer file.Close()
	w := bufio.NewWriter(file)
	header := STLHeader{}
	header.Count = uint32(len(mesh.Triangles))
	if err := binary.Write(w, binary.LittleEndian, &header); err != nil {
		return err
	}
	for _, triangle := range mesh.Triangles {
		n := triangle.Normal()
		d := STLTriangle{}
		d.N[0] = float32(n.X)
		d.N[1] = float32(n.Y)
		d.N[2] = float32(n.Z)
		d.V1[0] = float32(triangle.V1.Position.X)
		d.V1[1] = float32(triangle.V1.Position.Y)
		d.V1[2] = float32(triangle.V1.Position.Z)
		d.V2[0] = float32(triangle.V2.Position.X)
		d.V2[1] = float32(triangle.V2.Position.Y)
		d.V2[2] = float32(triangle.V2.Position.Z)
		d.V3[0] = float32(triangle.V3.Position.X)
		d.V3[1] = float32(triangle.V3.Position.Y)
		d.V3[2] = float32(triangle.V3.Position.Z)
		if err := binary.Write(w, binary.LittleEndian, &d); err != nil {
			return err
		}
	}
	w.Flush()
	return nil
}
