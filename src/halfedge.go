package cadmium

type HalfedgeDS struct {
	Halfedge *Halfedge
	Vertices []Vector
}

type Halfedge struct {
	vIdx int
	next *Halfedge
	twin *Halfedge
}

func NewHalfedgeDS(vertices []Vector, faces [][]int) *HalfedgeDS {
	ds := HalfedgeDS{Halfedge: nil, Vertices: vertices}

	halfedgeFaces := []*Halfedge{}

	for i := 0; i < len(faces); i++ {
		faceVertices := faces[i]
		firstHalfedge := Halfedge{vIdx: faceVertices[0], next: nil, twin: nil}
		he := &firstHalfedge
		for j := 1; j < len(faceVertices); j++ {
			he.next = &Halfedge{vIdx: faceVertices[j], next: nil, twin: nil}
			he = he.next
		}
		// the last halfedge must point to the first
		he.next = &firstHalfedge
		halfedgeFaces = append(halfedgeFaces, &firstHalfedge)
		//setTwins(halfedgeFaces)
		setTwins(halfedgeFaces)
	}
	ds.Halfedge = halfedgeFaces[0]
	return &ds
}

func (he Halfedge) copy() Halfedge {
	return Halfedge{vIdx: he.vIdx, next: he.next, twin: he.twin}
}

func (he Halfedge) sameHalfedge(other Halfedge) bool {
	return (he.vIdx == other.vIdx && he.next.vIdx == other.next.vIdx)
}

func (he Halfedge) sameEdge(other Halfedge) bool {
	return (he.vIdx == other.vIdx && he.next.vIdx == other.next.vIdx) || (he.vIdx == other.next.vIdx && he.next.vIdx == other.vIdx)
}

func (he Halfedge) equals(other Halfedge) bool {
	return he.vIdx == other.vIdx && he.next == other.next && he.twin == other.twin
}

func setTwins(halfedgeFaces []*Halfedge) {
	if len(halfedgeFaces) == 1 {
		return
	}
	// take the last face
	lastFaceFirstHalfedge := halfedgeFaces[len(halfedgeFaces)-1]
	currentHalfedge := lastFaceFirstHalfedge
	// walk through the last face halfedges
	for currentHalfedge.next != lastFaceFirstHalfedge {
		// walk through faces, excluding the last face
		for i := 0; i < len(halfedgeFaces)-1; i++ {
			otherFaceFirstHalfedge := halfedgeFaces[i]
			otherCurrentHalfedge := otherFaceFirstHalfedge
			// walk through the other halfedges
			for otherCurrentHalfedge.next != otherFaceFirstHalfedge {
				if otherCurrentHalfedge.twin == nil && currentHalfedge.sameEdge(*otherCurrentHalfedge) {
					currentHalfedge.twin = otherCurrentHalfedge
					otherCurrentHalfedge.twin = currentHalfedge
				}
				otherCurrentHalfedge = otherCurrentHalfedge.next
			}
		}
		currentHalfedge = currentHalfedge.next
	}
}
