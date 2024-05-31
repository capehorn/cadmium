package cadmium

func SubdDooSabin(ds HalfedgeDS) HalfedgeDS {
	nds := NewHalfedgeDS([]Vector{}, [][]int{})
	//avgVertex =
	faceVertices := ds.faceVertices(ds.Halfedge)
	center := Average(faceVertices)
	newFaceVertices := make([]Vector, 0, len(faceVertices))
	for i, v := range faceVertices {
		newFaceVertices[i] = center.Add(v.Sub(center).MulScalar(0.5))
	}
	return *nds
}
