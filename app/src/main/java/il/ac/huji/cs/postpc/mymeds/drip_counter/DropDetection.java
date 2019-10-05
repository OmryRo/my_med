package il.ac.huji.cs.postpc.mymeds.drip_counter;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import il.ac.huji.cs.postpc.mymeds.BuildConfig;

public class DropDetection {

    Mat lastFrame;
    Context context;
    int counter = 0;
    float speed = Float.NaN;
    long lastDropPassed = 0;

    DropDetection(Context context) {
        this.context = context;
    }

    public void onCameraViewStarted(int width, int height) {
        lastFrame = null;
        counter = 0;
        speed = Float.NaN;
        lastDropPassed = 0;
    }

    public void onCameraViewStopped() {
        lastFrame = null;
    }

    public Pair<Integer, Float> detectDrops(Mat dst, Mat gray, Point[] searchArea) {

        if (lastFrame != null && searchArea != null) {

            Mat diff = new Mat();
            Core.absdiff(lastFrame, gray, diff);
            Imgproc.GaussianBlur(diff, diff, new Size(45, 45), 0);

            Imgproc.threshold(diff, diff, 125, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

            diff = diff.submat(new Rect(searchArea[0], searchArea[1]));
            int width = diff.width();
            int height = diff.height();

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(diff, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            List<Object[]> candidates = new ArrayList<>();
            for (int i = 0; i < contours.size(); i++) {

                MatOfPoint cnt = contours.get(i);
                MatOfPoint2f curve = new MatOfPoint2f(cnt.toArray());
                Point[] points = curve.toArray();
                boolean isInBorder = false;
                for (Point point : points) {
                    if (point.x <= 0.2 * width || 0.8 * width <= point.x || point.y <= 0.2 * height || height * 0.8 <= point.y) {
                        isInBorder = true;
                        break;
                    }
                }

                if (isInBorder) {
                    if (BuildConfig.DEBUG_OPEN_CV) {
                        Imgproc.drawContours(dst, contours, i, Utils.COLOR_BAD_2, 3, 0, new Mat(), 0, searchArea[0]);
                    }
                    continue;
                }

                int numberVertices = (int) curve.total();

                if (numberVertices < 3) {
                    continue;
                }

                double contourArea = Math.abs(Imgproc.contourArea(cnt));
                double expectedSize = width * width * 0.2;
                boolean inSize = contourArea > expectedSize * 0.5 && contourArea < expectedSize * 2;

                if (!inSize) {
                    if (BuildConfig.DEBUG_OPEN_CV) {
                        Imgproc.drawContours(dst, contours, i, Utils.COLOR_BAD, 3, 0, new Mat(), 0, searchArea[0]);
                    }
                    continue;
                }

                Point center = Utils.average(points);
                candidates.add(new Object[] {cnt, contourArea, center});

                Imgproc.drawContours(dst, contours, i, Utils.COLOR_NATURAL, 3, 0, new Mat(), 0, searchArea[0]);
            }

            boolean isFound = false;
            for (Object[] first : candidates) {
                for (Object[] second : candidates) {

                    if (first == second) {
                        continue;
                    }

                    MatOfPoint firstCnt = (MatOfPoint) first[0];
                    MatOfPoint secondCnt = (MatOfPoint) second[0];

                    double firstContourArea = (double) first[1];
                    double secondContourArea = (double) second[1];

                    Point firstCenter = (Point) first[2];
                    Point secondCenter = (Point) second[2];

                    double weightRatio = firstContourArea / secondContourArea;
                    if (weightRatio > 1) {
                        weightRatio = 1 / weightRatio;
                    }

                    if (
                            Math.abs(firstCenter.x - secondCenter.x) < width * 0.2 &&
                            Math.abs(firstCenter.y - secondCenter.y) < height * 0.3 &&
                            weightRatio > 0.2
                    ) {

                        List<MatOfPoint> toDraw = new ArrayList<>();
                        toDraw.add(firstCnt);
                        toDraw.add(secondCnt);

                        if (BuildConfig.DEBUG_OPEN_CV) {
                            Imgproc.drawContours(dst, toDraw, -1, Utils.COLOR_GOOD, 5, 0, null, 0, searchArea[0]);
                        }

                        isFound = true;
                        break;

                    }
                }

                if (isFound) {
                    break;
                }

            }

            if (isFound) {
                long currentTime = System.currentTimeMillis();

                if (lastDropPassed == 0) {
                    counter++; // first drop

                } else {
                    if (lastDropPassed + 500 >= currentTime) {
                        // same drop event...
                    } else {
                        long timePassedFromLastOneMs = currentTime - lastDropPassed;
                        speed = (1000f / timePassedFromLastOneMs) * 60f;
                        counter++;

                    }
                }
                lastDropPassed = currentTime;
            }

        }

        lastFrame = gray.clone();

        return new Pair<>(counter, speed);
    }

}
