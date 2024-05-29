package cadmium

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestNewHalfedgeDS_twoQuads(t *testing.T) {
	assert := assert.New(t)
	vertices := []Vector{V(0, 0, 0), V(10, 0, 0), V(10, 10, 0), V(0, 10, 0), V(0, 0, 10), V(10, 0, 10)}
	faces := [][]int{{0, 1, 2, 3}, {1, 0, 4, 5}}
	ds := NewHalfedgeDS(vertices, faces)

	he_0_3 := Halfedge{vIdx: 3, next: nil, twin: nil}
	he_0_2 := Halfedge{vIdx: 2, next: &he_0_3, twin: nil}
	he_0_1 := Halfedge{vIdx: 1, next: &he_0_2, twin: nil}
	he_0_0 := Halfedge{vIdx: 0, next: &he_0_1, twin: nil}
	he_0_3.next = &he_0_0

	he_1_3 := Halfedge{vIdx: 5, next: nil, twin: nil}
	he_1_2 := Halfedge{vIdx: 4, next: &he_1_3, twin: nil}
	he_1_1 := Halfedge{vIdx: 0, next: &he_1_2, twin: nil}
	he_1_0 := Halfedge{vIdx: 1, next: &he_1_1, twin: nil}
	he_1_3.next = &he_1_0

	he_0_0.twin = &he_1_0
	he_1_0.twin = &he_0_0

	assert.Equal(vertices, ds.Vertices)
	assert.Equal(he_0_0, *ds.Halfedge)
}
