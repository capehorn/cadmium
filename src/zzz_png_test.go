package cadmium

import (
	"testing"

	"github.com/fogleman/gg"
)

func Test_cubicBezierSubdivision(test *testing.T) {
	cb := NewCubicBezier(V(0, 0, 0), V(300, 100, 0), V(250, 380, 0), V(350, 220, 0))

	w := 400
	h := 400
	dc := gg.NewContext(w, h)

	dc.DrawRectangle(0, 0, float64(w), float64(h))
	dc.SetRGB(1, 1, 1)
	dc.Fill()

	//drawCubicBezier(dc, cb, 16, 4, [3]float64{1, 0, 0})
	drawControlFrameLines(dc, [4]Vector{cb.P0, cb.P1, cb.P2, cb.P3}, 2, [3]float64{1, 0, 0})
	drawPoints(dc, getCubicBezierCurvePoints(cb, 8), 2, [3]float64{0, 1, 0})

	cb0, cb1 := cb.SplitAt(0.5)
	for _, cb := range []CubicBezier{cb0, cb1} {
		drawControlFrameLines(dc, [4]Vector{cb.P0, cb.P1, cb.P2, cb.P3}, 1, [3]float64{1, 0, 0})
		drawPoints(dc, getCubicBezierCurvePoints(cb, 16), 2, [3]float64{0, 0, 0})
	}

	dc.SavePNG("../test/out-subdiv.png")
}

func Test_cubicBezierApproximate(test *testing.T) {
	cb := NewCubicBezier(V(0, 0, 0), V(300, 100, 0), V(250, 380, 0), V(350, 220, 0))

	w := 400
	h := 400
	dc := gg.NewContext(w, h)

	dc.DrawRectangle(0, 0, float64(w), float64(h))
	dc.SetRGB(1, 1, 1)
	dc.Fill()

	points := cb.Approximate(2, make([]Vector, 0))
	drawPoints(dc, points, 2, [3]float64{0, 0, 0})

	dc.SavePNG("../test/out-approx.png")
}

func drawControlFrameLines(dc *gg.Context, ps [4]Vector, radius float64, rgb [3]float64) {
	// draw control frame
	dc.SetRGB(rgb[0], rgb[1], rgb[2])
	dc.DrawLine(ps[0].X, ps[0].Y, ps[1].X, ps[1].Y)
	dc.DrawLine(ps[1].X, ps[1].Y, ps[2].X, ps[2].Y)
	dc.DrawLine(ps[2].X, ps[2].Y, ps[3].X, ps[3].Y)

	dc.DrawCircle(ps[0].X, ps[0].Y, radius)
	dc.DrawCircle(ps[1].X, ps[1].Y, radius)
	dc.DrawCircle(ps[2].X, ps[2].Y, radius)
	dc.DrawCircle(ps[3].X, ps[3].Y, radius)

	dc.Stroke()
}

func drawPoints(dc *gg.Context, points []Vector, radius float64, rgb [3]float64) {
	for _, p := range points {
		dc.SetRGB(rgb[0], rgb[1], rgb[2])
		dc.DrawCircle(p.X, p.Y, radius)
		dc.Fill()
	}
}

func getCubicBezierCurvePoints(cb CubicBezier, numOfInterval int) []Vector {
	points := make([]Vector, 0)
	t := 0.0
	dt := float64(1) / float64(numOfInterval)
	for i := 0; i <= numOfInterval; i++ {
		if i == numOfInterval {
			t = 1.0
		}
		points = append(points, cb.PointAt(t))
		t = t + dt
	}
	return points
}
