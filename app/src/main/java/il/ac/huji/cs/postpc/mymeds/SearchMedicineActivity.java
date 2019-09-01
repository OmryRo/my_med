package il.ac.huji.cs.postpc.mymeds;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.util.ArrayList;
import java.util.List;

/*
 * api to use: https://open.fda.gov/apis/
 */
public class SearchMedicineActivity extends AppCompatActivity implements LifecycleOwner {

    public static final int SEACH_MEDICINE_REQUEST = 0x2010;
    public static final int SEARCH_MEDICINE_CHANGES = 0x2011;
    public static final int SEARCH_MEDICINE_NO_CHANGES = 0x2012;
    private static final int VOICE_SEARCH_REQ_ID = 0x3000;
    private static final String TAG = SearchMedicineActivity.class.getSimpleName();

    private PermissionChecker permissionChecker;
    private LifecycleRegistry lifecycleRegistry;
    private TextureView viewFinder;
    private NestedScrollView nestedScrollView;
    private Toolbar toolbar;
    private EditText searchText;
    private ImageView searchIcon;
    private ImageButton micButton;
    private ImageButton clearButton;
    private RecyclerView recyclerView;
    private boolean isInCameraMode;

    private FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions
            .Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_EAN_13)
            .build();

    private FirebaseVisionBarcodeDetector detector = FirebaseVision
            .getInstance()
            .getVisionBarcodeDetector(options);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionChecker = new PermissionChecker(this);

        setContentView(R.layout.activity_search_medicine);
        nestedScrollView = findViewById(R.id.nsv_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationContentDescription(R.string.back);

        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
        viewFinder = findViewById(R.id.view_finder);

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                updateTransform();
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_camera_search:
                        permissionChecker.enqueueIfHasCameraPermissions(new PermissionChecker.HasPermissionsListener() {
                            @Override
                            public void granted() {
                                setCameraMode();
                            }

                            @Override
                            public void denied() {
                                new AlertDialog.Builder(SearchMedicineActivity.this)
                                        .setTitle("Camera Permission required.")
                                        .setMessage("In order to use the camera to search, you must give permission to use it.")
                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create()
                                        .show();
                            }
                        });
                        return true;
                }
                return false;
            }
        });

        setTextSearchMode();
        setSearchBar();
        setRecycleView();
    }

    private void setRecycleView() {
        recyclerView = findViewById(R.id.results_container);
        recyclerView.setAdapter(new RecyclerView.Adapter<ListItemHolder>() {
            @NonNull
            @Override
            public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return ListItemHolder.createHolder(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {
                holder.setData(
                        R.drawable.ic_tablets_solid,
                        "Some Medicine",
                        "3 times a day.\nNext time: 8 hours."
                );
            }

            @Override
            public int getItemCount() {
                return 10;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void setSearchBar() {
        searchText = findViewById(R.id.search_medicine_bar);
        searchIcon = findViewById(R.id.search_icon);
        micButton = findViewById(R.id.mic_button);
        clearButton = findViewById(R.id.clear_button);
        micButton.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.GONE);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = s.length() == 0;
                micButton.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                clearButton.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            }
        });

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_search));
                startActivityForResult(intent, VOICE_SEARCH_REQ_ID);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
            }
        });

        searchText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
    }


    private void startScanning() {
        viewFinder.post(new Runnable() {
            @Override
            public void run() {
                startCamera();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleRegistry.markState(Lifecycle.State.RESUMED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    private void setCameraMode() {
        setTitle(R.string.search_by_camera);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextSearchMode();
            }
        });

        isInCameraMode = true;
        nestedScrollView.setVisibility(View.GONE);
        viewFinder.setVisibility(View.VISIBLE);
        startScanning();
        searchText.setText("");
        invalidateOptionsMenu();
    }

    private void setTextSearchMode() {
        setTitle(R.string.find_medicine);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(SEARCH_MEDICINE_NO_CHANGES);
                finish();
            }
        });

        viewFinder.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
        isInCameraMode = false;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isInCameraMode) {
            return super.onCreateOptionsMenu(menu);
        }

        getMenuInflater().inflate(R.menu.search_top_menu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_SEARCH_REQ_ID && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String query = matches.get(0);
                searchText.setText(query);
                searchText.setSelection(query.length());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isInCameraMode) {
            setTextSearchMode();
        } else {
            super.onBackPressed();
        }
    }
    private void startCamera() {
        // Create configuration object for the viewfinder use case
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(new Rational(1, 1))
                .setTargetResolution(new Size(640, 640))
                .build();

        // Build the viewfinder use case
        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup parent = (ViewGroup) viewFinder.getParent();
                parent.removeView(viewFinder);
                parent.addView(viewFinder, 0);
                viewFinder.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }
        });

        ImageAnalysisConfig config =
                new ImageAnalysisConfig.Builder()
                        .setTargetResolution(new Size(640, 640))
                        .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                        .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(config);

        imageAnalysis.setAnalyzer(new ImageAnalysis.Analyzer() {
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
                                            searchText.setText(rawValue);
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
        });

        CameraX.bindToLifecycle(this, imageAnalysis, preview);
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();

        // Compute the center of the view finder
        float centerX = viewFinder.getWidth() / 2f;
        float centerY = viewFinder.getHeight() / 2f;

        // Correct preview output to account for display rotation
        int rotationDegrees = 0;
        switch (viewFinder.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotationDegrees = 0;
                break;
            case Surface.ROTATION_90:
                rotationDegrees = 90;
                break;
            case Surface.ROTATION_180:
                rotationDegrees = 180;
                break;
            case Surface.ROTATION_270:
                rotationDegrees = 270;
                break;
        }

        matrix.postRotate(-rotationDegrees, centerX, centerY);

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix);
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
}
