package il.ac.huji.cs.postpc.mymeds.drip_counter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.cs.postpc.mymeds.BuildConfig;

class ChamberDetector {

    public static final int DEFAULT_CANNY_LOW = 15;
    public static final int DEFAULT_CANNY_HIGH = 25;
    public static final boolean DEFAULT_FILTER_AREA = true;

    private static final int MAX_TIMES_TILL_RESET = 10;
    private static final int MAX_DISTANCE_TO_IGNORE = 500;

    private Mat dsIMG, usIMG, bwIMG, cIMG, hovIMG;
    private MatOfPoint2f approxCurve;
    private Point[] dropChamberArea;

    private int timesTillReset;

    ChamberDetector() {}

    public Mat getCanny() {
        return cIMG;
    }

    public void onCameraViewStarted(int width, int height) {
        dsIMG = new Mat();
        usIMG = new Mat();
        bwIMG = new Mat();
        cIMG = new Mat();
        hovIMG = new Mat();
        approxCurve = new MatOfPoint2f();
        dropChamberArea = new Point[] {new Point(0,0), new Point(0,0)};
        timesTillReset = -1;
    }

    public void onCameraViewStopped() {
        dsIMG = null;
        usIMG = null;
        bwIMG = null;
        cIMG = null;
        hovIMG = null;
        approxCurve = null;
    }

    public Point[] detectDropChamber(Mat dst, Mat gray) {

        Imgproc.pyrDown(gray, usIMG, new Size(gray.cols() / 2, gray.rows() / 2));
        Imgproc.pyrUp(usIMG, usIMG, gray.size());

        Imgproc.Canny(usIMG, bwIMG, 10, 80);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 15));
        Imgproc.dilate(bwIMG, bwIMG, kernel, new Point(-1, 1), 1);
        Core.bitwise_not(bwIMG, bwIMG);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(bwIMG, contours, hovIMG, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Object[]> found = new ArrayList<>();

        for (MatOfPoint cnt : contours) {

            MatOfPoint2f curve = new MatOfPoint2f(cnt.toArray());
            double epsilon = 0.03 * Imgproc.arcLength(curve, true);

            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(curve, approxCurve, epsilon, true);
            int numberVertices = (int) approxCurve.total();

            if (numberVertices <= 4 || 30 <= numberVertices) {
                continue;
            }

            double contourArea = Math.abs(Imgproc.contourArea(cnt));
            boolean inSize = contourArea > 15000 && contourArea < 35000;

            if (!inSize) {
                continue;
            }

            Point[] blocking = Utils.findBlockingBox(approxCurve);

            boolean inAspect = Math.abs(blocking[0].y - blocking[1].y) >= 2 * Math.abs(blocking[0].x - blocking[1].x);
            if (!inAspect) {
                if (BuildConfig.DEBUG_OPEN_CV) {
                    Imgproc.rectangle(dst, blocking[0], blocking[1], Utils.COLOR_BAD, 3);
                }
                continue;
            }

            found.add(new Object[] {approxCurve, blocking});
        }

        double bestTopLeftDist = Double.MAX_VALUE;
        double bestBottomRightDist = Double.MAX_VALUE;
        double bestDistTotal = Double.MAX_VALUE;
        Point[] best = null;

        for (Object[] candidate : found) {
            Point[] blocking = (Point[]) candidate[1];

            if (BuildConfig.DEBUG_OPEN_CV) {
                Imgproc.rectangle(dst, blocking[0], blocking[1], Utils.COLOR_CANDIDATE, 3);
            }
            double topLeftDist = Utils.distance2(dropChamberArea[0], blocking[0]);
            double bottomRightDist = Utils.distance2(dropChamberArea[1], blocking[1]);
            double totalDist = topLeftDist * topLeftDist + bottomRightDist * bottomRightDist;

            if (best == null || bestDistTotal > totalDist) {
                bestTopLeftDist = topLeftDist;
                bestBottomRightDist = bottomRightDist;
                bestDistTotal = totalDist;
                best = blocking;
            }
        }

        if (best != null) {

            if (timesTillReset <= 0) {
                dropChamberArea = best;
                timesTillReset = 20;

            } else if (bestDistTotal < 10000) {
                dropChamberArea[0] = Utils.filter(dropChamberArea[0], best[0]);
                dropChamberArea[1] = Utils.filter(dropChamberArea[1], best[1]);
                timesTillReset = 20;

            } else {
                timesTillReset -= 1;

            }

            Imgproc.rectangle(dst, best[0], best[1], Utils.COLOR_GOOD, 3);

        } else {
            timesTillReset -= 1;
        }

        if (timesTillReset >= 0) {
            Scalar color = timesTillReset == 0 ? Utils.COLOR_SEARCH_AREA_LAST : Utils.COLOR_SEARCH_AREA;
            if (BuildConfig.DEBUG_OPEN_CV) {
                Imgproc.rectangle(dst, dropChamberArea[0], dropChamberArea[1], color, 3);
            }
        }

        return dropChamberArea;
    }
}
