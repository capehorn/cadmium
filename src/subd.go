package cadmium

type SubDMesh struct {
	vertices    []Vector
	faces       []int
	faceOffsets []int
	commonEdges []int // length is multiple of 4, repetition of: face offset idx, face offset idx, vertex idx, vertex idx
}

func NewSubDMesh(vertices []Vector, faces [][]int) *SubDMesh {
	m := &SubDMesh{vertices: vertices, faces: []int{}, faceOffsets: []int{}, commonEdges: []int{}}

	findShared := make(map[int]bool)

	m.faceOffsets = append(m.faceOffsets, 0)
	// compare each two faces
	for i := 0; i < len(faces)-1; i++ {
		face := faces[i]
		m.faces = append(m.faces, face...)
		m.faceOffsets = append(m.faceOffsets, len(m.faces))

		for j := i + 1; j < len(faces); j++ {
			commonVertices := []int{}
			for k := 0; k < len(face); k++ {
				findShared[face[k]] = true
			}
			oFace := faces[j]
			for l := 0; l < len(oFace); l++ {
				if _, ok := findShared[oFace[l]]; ok {
					commonVertices = append(commonVertices, oFace[l])
				}
			}
			if len(commonVertices) == 2 {
				m.commonEdges = append(m.commonEdges, i, j, commonVertices[0], commonVertices[1])
			} else if 2 < len(commonVertices) {
				panic("More than two common vertices between two faces")
			}
			clear(findShared)
		}
	}

	// add last face
	m.faces = append(m.faces, faces[len(faces)-1]...)

	return m
}

func (m *SubDMesh) GetFace(fOffsetIdx int) []int {
	if 0 <= fOffsetIdx {
		if fOffsetIdx == len(m.faceOffsets)-1 {
			return m.faces[m.faceOffsets[fOffsetIdx]:]
		} else if fOffsetIdx < len(m.faceOffsets)-1 {
			return m.faces[m.faceOffsets[fOffsetIdx]:m.faceOffsets[fOffsetIdx+1]]
		}
	}
	return nil
}

func (m *SubDMesh) GetFaces() [][]int {
	faces := [][]int{}
	for fOffsetIdx := 0; fOffsetIdx < len(m.faceOffsets); fOffsetIdx++ {
		faces = append(faces, m.GetFace(fOffsetIdx))
	}
	return faces
}

func (m *SubDMesh) GetCommonEdgesIndices(fOffsetIdx int, vIdx int) []int {
	commonEdgesIndices := []int{}
	for i := 0; i < len(m.commonEdges)-4; i += 4 {
		if (m.commonEdges[i] == fOffsetIdx || m.commonEdges[i+1] == fOffsetIdx) && (m.commonEdges[i+2] == vIdx || m.commonEdges[i+3] == vIdx) {
			commonEdgesIndices = append(commonEdgesIndices, i/4)
		}
	}
	return commonEdgesIndices
}

type edgeKey = [2]int

func createEdgeKey(p int, q int) edgeKey {
	if p <= q {
		return [2]int{p, q}
	}
	return [2]int{q, p}
}

type edgeMidAndNewVertex struct {
	edgeMidPoint Vector
	newVertex    Vector
	vIdx         int // vertex index in the new subd mesh
}

func (m *SubDMesh) SubD() *SubDMesh {
	// create subd mesh, original verticies are also verticies in this new mesh
	subD := &SubDMesh{vertices: make([]Vector, len(m.vertices)), faces: []int{}, faceOffsets: []int{}, commonEdges: []int{}}
	copy(subD.vertices, m.vertices)

	// tracking where the face avg vertices starts in subd
	faceAvgOffset := len(subD.vertices)
	// for every original face stores the avg point
	faceAvgs := []Vector{}
	faces := m.GetFaces()
	// compute each face's average point
	for _, face := range faces {
		faceAvgs = append(faceAvgs, m.faceAveragePoint(face))
	}
	// add face avg points to the subd vertices
	subD.vertices = append(subD.vertices, faceAvgs...)

	// for every original edge stores its midpoint and the new edge point
	edgeMidAndNewVertices := make(map[edgeKey]edgeMidAndNewVertex)

	// compute each edge midpoint and the new edge point
	for _, face := range faces {
		for _, pair := range SlidingWindow2(face, true) {
			m.computeMidEdgeAndNewEdgeVertex(pair[0], pair[1], faceAvgs, subD, edgeMidAndNewVertices)
		}
	}

	// track computing the new vertex positions
	computedVertices := make(map[int]Vector)

	// build new faces and compute new vertices position
	for fOffsetIdx := 0; fOffsetIdx < len(m.faceOffsets); fOffsetIdx++ {
		for _, t := range SlidingWindow3(m.GetFace(fOffsetIdx), true) {
			edgeKey01 := createEdgeKey(t[0], t[1])
			edgeKey12 := createEdgeKey(t[1], t[2])
			// add face offset and face
			subD.faceOffsets = append(subD.faceOffsets, len(subD.faces))
			subD.faces = append(subD.faces, t[1], edgeMidAndNewVertices[edgeKey01].vIdx, faceAvgOffset+fOffsetIdx, edgeMidAndNewVertices[edgeKey12].vIdx)

			// only compute new vertices position if not computed yet
			if _, ok := computedVertices[t[1]]; !ok {
				computedVertices[t[1]] = m.computeVertex(t[1], edgeKey01, edgeKey12, fOffsetIdx, edgeMidAndNewVertices, faceAvgs)
			}
		}
	}

	for k := range computedVertices {
		subD.vertices[k] = computedVertices[k]
	}

	return subD
}

func (m *SubDMesh) Triangulate() []*Triangle {
	triangles := []*Triangle{}
	for _, face := range m.GetFaces() {
		switch len(face) {

		case 3:
			triangles = append(triangles, NewTriangleForPoints(m.vertices[face[0]], m.vertices[face[1]], m.vertices[face[2]]))
		case 4:
			t0, t1 := QuadToTriangles(m.vertices[face[0]], m.vertices[face[1]], m.vertices[face[2]], m.vertices[face[3]])
			triangles = append(triangles, t0, t1)
		default:
			avgPoint := m.faceAveragePoint(face)
			for _, pair := range SlidingWindow2(face, true) {
				triangles = append(triangles, NewTriangleForPoints(avgPoint, m.vertices[pair[0]], m.vertices[pair[1]]))
			}
		}
	}
	return triangles
}

func (m *SubDMesh) computeVertex(vIdx int, edgeKey01, edgeKey02 edgeKey, fOffsetIdx int, edgeMidAndNewVertices map[edgeKey]edgeMidAndNewVertex, faceAvgs []Vector) Vector {
	faceOffsetIndices := make(map[int]bool)
	faceOffsetIndices[fOffsetIdx] = true
	edgeKeys := make(map[edgeKey]bool)
	edgeKeys[edgeKey01] = true
	edgeKeys[edgeKey02] = true

	// find all other edges from the vertex
	for fIdx, face := range m.GetFaces() {
	FaceVertices:
		for _, pair := range SlidingWindow2(face, true) {
			if pair[0] == vIdx || pair[1] == vIdx {
				ek := createEdgeKey(pair[0], pair[1])
				if _, ok := edgeKeys[ek]; !ok {
					edgeKeys[ek] = true
					if _, ok := faceOffsetIndices[fIdx]; !ok {
						faceOffsetIndices[fIdx] = true
					}
					break FaceVertices
				}
			}
		}
	}

	newVertex := V(0, 0, 0)

	for k := range faceOffsetIndices {
		newVertex = newVertex.Add(faceAvgs[k])
	}

	newVertex = newVertex.DivScalar(float64(len(faceOffsetIndices)))

	midEdgeSum := V(0, 0, 0)

	for ek := range edgeKeys {
		midEdgeSum = midEdgeSum.Add(edgeMidAndNewVertices[ek].edgeMidPoint)
	}

	midEdgeSum = midEdgeSum.DivScalar(float64(len(edgeKeys)))
	newVertex = newVertex.Add(midEdgeSum.MulScalar(2))
	newVertex = newVertex.Add(m.vertices[vIdx].MulScalar(float64(len(edgeKeys) - 3)))
	return newVertex.DivScalar(float64(len(edgeKeys)))
}

func (m *SubDMesh) findCommonEdgeIdx(vIdxA int, vIdxB int) int {
	for i := 0; i < len(m.commonEdges)/4; i++ {
		if (m.commonEdges[4*i+2] == vIdxA && m.commonEdges[4*i+3] == vIdxB) || (m.commonEdges[4*i+3] == vIdxA && m.commonEdges[4*i+2] == vIdxB) {
			return i
		}
	}
	return -1
}

func (m *SubDMesh) computeMidEdgeAndNewEdgeVertex(vIdxA int, vIdxB int, faceAvgs []Vector, subD *SubDMesh, edgeMidAndNewPoints map[edgeKey]edgeMidAndNewVertex) {
	edgeKey := createEdgeKey(vIdxA, vIdxB)

	// only add if absent
	if _, ok := edgeMidAndNewPoints[edgeKey]; !ok {
		edgeMidPoint := m.vertices[vIdxA].Lerp(m.vertices[vIdxB], 0.5)
		var newEdgePoint Vector
		commonEdgeIdx := m.findCommonEdgeIdx(vIdxA, vIdxB)
		if commonEdgeIdx == -1 {
			newEdgePoint = edgeMidPoint
		} else {
			f := faceAvgs[m.commonEdges[commonEdgeIdx*4]]
			g := faceAvgs[m.commonEdges[commonEdgeIdx*4+1]]
			faceMidPoint := f.Lerp(g, 0.5)
			newEdgePoint = edgeMidPoint.Lerp(faceMidPoint, 0.5)
		}
		subD.vertices = append(subD.vertices, newEdgePoint)
		edgeMidAndNewPoints[edgeKey] = edgeMidAndNewVertex{edgeMidPoint: edgeMidPoint, newVertex: newEdgePoint, vIdx: len(subD.vertices) - 1}
	}
}

func (m *SubDMesh) faceAveragePoint(face []int) Vector {
	x := 0.0
	y := 0.0
	z := 0.0

	l := len(face)
	for i := 0; i < l; i++ {
		vertex := m.vertices[face[i]]
		x += vertex.X
		y += vertex.Y
		z += vertex.Z
	}
	return V(x/float64(l), y/float64(l), z/float64(l))
}
