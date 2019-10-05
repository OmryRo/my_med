package il.ac.huji.cs.postpc.mymeds.drip_counter;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Utils {

    public static final Scalar COLOR_CANDIDATE = new Scalar(255, 255, 0, 255);
    public static final Scalar COLOR_GOOD = new Scalar(0, 255, 0, 255);
    public static final Scalar COLOR_BAD = new Scalar(255, 0, 0, 255);
    public static final Scalar COLOR_BAD_2 = new Scalar(255, 0, 255, 255);
    public static final Scalar COLOR_NATURAL = new Scalar(255, 255, 255, 255);
    public static final Scalar COLOR_SEARCH_AREA = new Scalar(0, 200, 255, 255);
    public static final Scalar COLOR_SEARCH_AREA_LAST = new Scalar(0, 100, 200, 255);

    public static double distance2(Point pt1, Point pt2) {
        double dx = pt1.x - pt2.x;
        double dy = pt1.y - pt2.y;
        return dx * dx + dy * dy;
    }

    public static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.x - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    public static void setLabel(Mat im, String label, MatOfPoint contour, double scale, Scalar color) {
        int fontface = Imgproc.FONT_HERSHEY_SIMPLEX;
        int thickness = 3;
        int[] baseline = new int[1];
        Size text = Imgproc.getTextSize(label, fontface, scale, thickness, baseline);
        Rect r = Imgproc.boundingRect(contour);
        Point pt = new Point(r.x + ((r.width - text.width) / 2), r.y + ((r.height + text.height) / 2));
        Imgproc.putText(im, label, pt, fontface, scale, color, thickness);
    }

    public static Point average(Point[] points) {
        if (points == null || points.length == 0) {
            return null;
        }

        double x = 0;
        double y = 0;

        for (Point point : points) {
            x += point.x;
            y += point.y;
        }

        return new Point(x / points.length, y / points.length);
    }

    public static Point[] findBlockingBox(MatOfPoint2f curve) {
        Point[] points = curve.toArray();

        double top = Double.MAX_VALUE;
        double bottom = Double.MIN_VALUE;

        double left = Double.MAX_VALUE;
        double right = Double.MIN_VALUE;

        for (Point point : points) {
            if (point.x < left) {
                left = point.x;
            } else if (point.x > right) {
                right = point.x;
            }

            if (point.y < top) {
                top = point.y;
            } else if (point.y > bottom) {
                bottom = point.y;
            }
        }

        return new Point[] {new Point(left, top), new Point(right, bottom)};
    }

    public static Point filter(Point p1, Point p2) {
        return filter(p1, p2, 0.5);
    }

    public static Point filter(Point p1, Point p2, double alpha) {
        double invert = 1 - alpha;
        return new Point(p1.x * alpha + p2.x * invert, p1.y * alpha + p2.y * invert);
    }

}
