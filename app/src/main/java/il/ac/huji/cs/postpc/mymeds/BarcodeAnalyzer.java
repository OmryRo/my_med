package il.ac.huji.cs.postpc.mymeds;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.util.List;


public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
    private FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions
            .Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_EAN_13)
            .build();
    private static String TAG = BarcodeAnalyzer.class.getSimpleName();
    private OnFoundBarcode listner = null;
    private FirebaseVisionBarcodeDetector detector = FirebaseVision
            .getInstance()
            .getVisionBarcodeDetector(options);
    private void onFounDBarcode(String barcode) {
        listner.onFoundDBarcode(barcode);
    }

    void setOnFounDBarcode (Context context) {
//        listner = context;
    }

    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException(
                        "Rotation must be 0, 90, 180, or 270.");
        }
    }

    @Override
    public void analyze(ImageProxy imageProxy, int degrees) {
        if (imageProxy == null || imageProxy.getImage() == null) {
            return;
        }
        Image mediaImage = imageProxy.getImage();
        int rotation = degreesToFirebaseRotation(degrees);
        FirebaseVisionImage img =
                FirebaseVisionImage.fromMediaImage(mediaImage, rotation);

        Task<List<FirebaseVisionBarcode>> result =
                detector.detectInImage(img)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                                for (FirebaseVisionBarcode barcode : barcodes) {
                                    Rect bounds = barcode.getBoundingBox();
                                    Point[] corners = barcode.getCornerPoints();
                                    String rawValue = barcode.getRawValue();
                                    onFounDBarcode(rawValue);
                                    Log.d(TAG, rawValue);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });

    }

    interface OnFoundBarcode {
        void onFoundDBarcode(String barcode);
    }
}

