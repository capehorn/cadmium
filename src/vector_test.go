package cadmium

import (
	"math"
	"testing"
)

var Tolerance = 0.00000001

func TestSegmentDistance(t *testing.T) {
	p := V(40, 10, 0)

	segmentStart := V(0, 0, 0)
	segmentEnd := V(100, 0, 0)

	distance := p.SegmentDistance(segmentStart, segmentEnd)
	if math.Abs(distance-10) > Tolerance {
		t.Errorf("Output %f is not equal with expected %f", distance, 10.0)
	}
}
