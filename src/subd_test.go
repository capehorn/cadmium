package cadmium

import (
	"log"
	"testing"

	"github.com/stretchr/testify/assert"
)

func Test_NewSubDMesh_twoQuad(t *testing.T) {
	assert := assert.New(t)
	vertices := []Vector{V(0, 0, 0), V(10, 0, 0), V(10, 10, 0), V(0, 10, 0), V(10, 0, 10), V(0, 10, 10)}
	faces := [][]int{{0, 1, 2, 3}, {0, 1, 4, 5}}
	m := NewSubDMesh(vertices, faces)

	assert.Equal(vertices, m.vertices)
	assert.Equal([]int{0, 1, 2, 3, 0, 1, 4, 5}, m.faces)
	assert.Equal([]int{0, 4}, m.faceOffsets)
	assert.Equal([]int{0, 1, 0, 1}, m.commonEdges)

	assert.Equal([]int{0, 1, 2, 3}, m.GetFace(0))
	assert.Equal([]int{0, 1, 4, 5}, m.GetFace(1))
	assert.Equal([]int(nil), m.GetFace(2))
}

func Test_NewSubDMesh_Cube(t *testing.T) {
	assert := assert.New(t)
	vertices := []Vector{V(0, 0, 0), V(10, 0, 0), V(10, 10, 0), V(0, 10, 0), V(10, 0, 10), V(0, 0, 10), V(0, 10, 10), V(10, 10, 10)}
	faces := [][]int{{0, 1, 2, 3}, {0, 1, 4, 5}, {2, 3, 6, 7}, {6, 7, 4, 5}, {1, 2, 7, 4}, {0, 3, 6, 5}}
	m := NewSubDMesh(vertices, faces)

	assert.Equal(vertices, m.vertices)

	assert.Equal([]int{0, 1, 2, 3}, m.GetFace(0))
	assert.Equal([]int{0, 1, 4, 5}, m.GetFace(1))
	assert.Equal([]int{2, 3, 6, 7}, m.GetFace(2))
	assert.Equal([]int{6, 7, 4, 5}, m.GetFace(3))
	assert.Equal([]int{1, 2, 7, 4}, m.GetFace(4))
	assert.Equal([]int{0, 3, 6, 5}, m.GetFace(5))
	assert.Equal([]int(nil), m.GetFace(7))

	assert.Equal(6, len(m.GetFaces()))

	assert.Equal([]int{0, 4, 8, 12, 16, 20}, m.faceOffsets)

	assert.Equal([]int{
		0, 1, 0, 1,
		0, 2, 2, 3,
		0, 4, 1, 2,
		0, 5, 0, 3,
		1, 3, 4, 5,
		1, 4, 1, 4,
		1, 5, 0, 5,
		2, 3, 6, 7,
		2, 4, 2, 7,
		2, 5, 3, 6,
		3, 4, 7, 4,
		3, 5, 6, 5}, m.commonEdges)

	assert.Equal(3, m.findCommonEdgeIdx(3, 0))
	assert.Equal(7, m.findCommonEdgeIdx(6, 7))
	assert.Equal(-1, m.findCommonEdgeIdx(1, 7))

	assert.Equal(V(5, 5, 0), m.faceAveragePoint([]int{0, 1, 2, 3}))

	faceAvgs := []Vector{}
	for _, face := range faces {
		faceAvgs = append(faceAvgs, m.faceAveragePoint(face))
	}

	edgeMidAndNewVertices := make(map[edgeKey]edgeMidAndNewVertex)

	m.computeMidEdgeAndNewEdgeVertex(0, 1, faceAvgs, m, edgeMidAndNewVertices)
	m.computeMidEdgeAndNewEdgeVertex(1, 2, faceAvgs, m, edgeMidAndNewVertices)

	assert.Equal(edgeMidAndNewVertex{edgeMidPoint: V(5, 0, 0), newVertex: V(5, 1.25, 1.25), vIdx: 8}, edgeMidAndNewVertices[createEdgeKey(0, 1)])
	assert.Equal(edgeMidAndNewVertex{edgeMidPoint: V(10, 5, 0), newVertex: V(8.75, 5, 1.25), vIdx: 9}, edgeMidAndNewVertices[createEdgeKey(1, 2)])

	assert.Equal([]int{0, 3}, m.GetCommonEdgesIndices(0, 0))
	assert.Equal([]int{0, 2}, m.GetCommonEdgesIndices(0, 1))
}

func Test_SubD_aQuad(t *testing.T) {
	assert := assert.New(t)
	vertices := []Vector{V(0, 0, 0), V(10, 0, 0), V(10, 10, 0), V(0, 10, 0)}
	faces := [][]int{{0, 1, 2, 3}}
	m := NewSubDMesh(vertices, faces)

	s := m.SubD()

	assert.Equal(9, len(s.vertices))
	assert.Equal(4*4, len(s.faces))
	subDFaces := s.GetFaces()
	assert.Contains(subDFaces, []int{1, 5, 4, 6})
}

func Test_SubD_twoQuads(t *testing.T) {
	assert := assert.New(t)
	vertices := []Vector{V(0, 0, 0), V(10, 0, 0), V(10, 10, 0), V(0, 10, 0), V(10, 0, 10), V(0, 0, 10)}
	faces := [][]int{{0, 1, 2, 3}, {0, 1, 4, 5}}
	m := NewSubDMesh(vertices, faces)

	s := m.SubD()

	assert.Equal(15, len(s.vertices))
	assert.Equal(8*4, len(s.faces))
}

func Test_SubD_Triangulate_cube(t *testing.T) {
	vertices := []Vector{V(0, 0, 0), V(10, 0, 0), V(10, 10, 0), V(0, 10, 0), V(10, 0, 10), V(0, 0, 10), V(0, 10, 10), V(10, 10, 10)}
	faces := [][]int{{0, 1, 2, 3}, {0, 1, 4, 5}, {2, 3, 6, 7}, {6, 7, 4, 5}, {1, 2, 7, 4}, {0, 3, 6, 5}}
	s := NewSubDMesh(vertices, faces)
	s = s.SubD()
	s = s.SubD()
	s = s.SubD()

	triangles := s.Triangulate()

	mesh := NewTriangleMesh(triangles)

	// fit mesh in a bi-unit cube centered at the origin
	mesh.BiUnitCube()

	// smooth the normals
	mesh.SmoothNormalsThreshold(Radians(30))

	err := SaveSTL("../test/subd-cube.stl", mesh)

	if err != nil {
		log.Fatal(err)
	}
}

func Test_SubD_Triangulate_twoQuads(t *testing.T) {
	vertices := []Vector{V(0, 0, 0), V(10, 0, 0), V(10, 10, 0), V(0, 10, 0), V(10, 0, 10), V(0, 0, 10)}
	faces := [][]int{{0, 1, 2, 3}, {0, 1, 4, 5}}
	s := NewSubDMesh(vertices, faces)
	s = s.SubD()
	s = s.SubD()
	s = s.SubD()

	triangles := s.Triangulate()

	mesh := NewTriangleMesh(triangles)

	// fit mesh in a bi-unit cube centered at the origin
	mesh.BiUnitCube()

	// smooth the normals
	mesh.SmoothNormalsThreshold(Radians(30))

	err := SaveSTL("../test/subd-two-quads.stl", mesh)

	if err != nil {
		log.Fatal(err)
	}
}
