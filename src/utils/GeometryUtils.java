package utils;

public class GeometryUtils {

    // --------------------
    // Data Classes
    // --------------------
    public static class Point {
        public double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }

    public static class Rectangle {
        public double xMin, yMin, xMax, yMax;
        public Rectangle(double xMin, double yMin, double xMax, double yMax) {
            this.xMin = Math.min(xMin, xMax);
            this.yMin = Math.min(yMin, yMax);
            this.xMax = Math.max(xMin, xMax);
            this.yMax = Math.max(yMin, yMax);
        }
        public boolean contains(Point p) {
            return p.x >= xMin && p.x <= xMax && p.y >= yMin && p.y <= yMax;
        }
        public Point[] getCorners() {
            return new Point[]{
                    new Point(xMin, yMin),
                    new Point(xMin, yMax),
                    new Point(xMax, yMin),
                    new Point(xMax, yMax)
            };
        }
    }

    public static class Triangle {
        public Point a, b, c;
        public Triangle(Point a, Point b, Point c) { this.a = a; this.b = b; this.c = c; }
        public Point[] getVertices() { return new Point[]{a, b, c}; }
    }

    // --------------------
    // Intersection Methods
    // --------------------

    // Triangle vs Rectangle
    public static boolean intersects(Triangle tri, Rectangle rect) {
        // Case 1: Triangle vertex inside rect
        for (Point p : tri.getVertices()) {
            if (rect.contains(p)) return true;
        }
        // Case 2: Rectangle corner inside triangle
        for (Point corner : rect.getCorners()) {
            if (pointInTriangle(corner, tri.a, tri.b, tri.c)) return true;
        }
        // Case 3: Edge intersections
        Point[] triPts = tri.getVertices();
        Point[] rectPts = rect.getCorners();
        int[][] rectEdges = {{0,1}, {1,3}, {3,2}, {2,0}};
        for (int i = 0; i < 3; i++) {
            Point t1 = triPts[i];
            Point t2 = triPts[(i+1)%3];
            for (int[] edge : rectEdges) {
                Point r1 = rectPts[edge[0]];
                Point r2 = rectPts[edge[1]];
                if (segmentsIntersect(t1, t2, r1, r2)) return true;
            }
        }
        return false;
    }

    // Rectangle vs Rectangle
    public static boolean intersects(Rectangle r1, Rectangle r2) {
        return !(r1.xMax < r2.xMin || r1.xMin > r2.xMax ||
                r1.yMax < r2.yMin || r1.yMin > r2.yMax);
    }

    // --------------------
    // Helpers
    // --------------------

    private static boolean pointInTriangle(Point p, Point a, Point b, Point c) {
        double denominator = ((b.y - c.y)*(a.x - c.x) + (c.x - b.x)*(a.y - c.y));
        double w1 = ((b.y - c.y)*(p.x - c.x) + (c.x - b.x)*(p.y - c.y)) / denominator;
        double w2 = ((c.y - a.y)*(p.x - c.x) + (a.x - c.x)*(p.y - c.y)) / denominator;
        double w3 = 1 - w1 - w2;
        return w1 >= 0 && w2 >= 0 && w3 >= 0;
    }

    private static boolean segmentsIntersect(Point p1, Point p2, Point q1, Point q2) {
        return (orientation(p1, p2, q1) * orientation(p1, p2, q2) <= 0) &&
                (orientation(q1, q2, p1) * orientation(q1, q2, p2) <= 0);
    }

    private static int orientation(Point a, Point b, Point c) {
        double val = (b.y - a.y) * (c.x - b.x) - (b.x - a.x) * (c.y - b.y);
        if (val > 0) return 1;
        if (val < 0) return -1;
        return 0;
    }
}
