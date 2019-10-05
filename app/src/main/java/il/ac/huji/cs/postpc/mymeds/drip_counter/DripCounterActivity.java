package il.ac.huji.cs.postpc.mymeds.drip_counter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import il.ac.huji.cs.postpc.mymeds.R;


public class DripCounterActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "DRIP_COUNTER_ACTIVITY";
    private static final int CAMERA_PERMISSIONS_REQ = 100;

    private BetterCameraView cameraView;

    ChamberDetector chamberDetector;
    DropDetection dropDetection;
    TextView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drip_counter);
        results = findViewById(R.id.results);

        chamberDetector = new ChamberDetector();
        dropDetection = new DropDetection(this);

        cameraView = findViewById(R.id.cameraView);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(this);
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.setFocus();
            }
        });

        results.setText("Direct the camera to the Drip Chamber.");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQ);
        } else {
            initCVLoader();
        }
    }

    private void initCVLoader() {
        if (OpenCVLoader.initDebug()) {
            cameraView.setVisibility(View.VISIBLE);
            cameraView.setEnabled(true);
            cameraView.enableView();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Can't load")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSIONS_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCVLoader();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null) {
            cameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraView != null) {
            cameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        chamberDetector.onCameraViewStarted(width, height);
        dropDetection.onCameraViewStarted(width, height);
    }

    @Override
    public void onCameraViewStopped() {
        chamberDetector.onCameraViewStopped();
        dropDetection.onCameraViewStopped();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat gray = inputFrame.gray();
        Mat dst = inputFrame.rgba();

        try {

            Point[] searchArea = chamberDetector.detectDropChamber(dst, gray);
            final Pair<Integer, Float> result = dropDetection.detectDrops(dst, gray, searchArea);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (Float.isNaN(result.second)) {
                        results.setText("Direct the camera to the Drip Chamber.");
                    } else {
                        results.setText(
                                String.format("Counter: %s        Speed: %.2f drop/min",
                                        result.first,
                                        result.second
                                )
                        );
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "onCameraFrame: ", e);
        }

        return dst;
    }

}
