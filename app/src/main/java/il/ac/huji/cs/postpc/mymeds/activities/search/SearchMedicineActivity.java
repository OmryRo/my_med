package il.ac.huji.cs.postpc.mymeds.activities.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.medicine.MedicineInfoActivity;
import il.ac.huji.cs.postpc.mymeds.database.entities.Medicine;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;
import il.ac.huji.cs.postpc.mymeds.utils.PermissionChecker;

/*
 * api to use: https://open.fda.gov/apis/
 */
public class SearchMedicineActivity extends AppCompatActivity implements ScanBarcodeFragment.Listener {

    private static final String TAG = "SEARCH_MEDICINE_ACTIVITY";

    public static final int SEACH_MEDICINE_REQUEST = 0x2010;
    public static final int SEARCH_MEDICINE_CHANGES = 0x2011;
    public static final int SEARCH_MEDICINE_NO_CHANGES = 0x2012;
    private static final int VOICE_SEARCH_REQ_ID = 0x3000;

    private PermissionChecker permissionChecker;
    private FrameLayout cameraViewPlaceholder;
    private Toolbar toolbar;
    private EditText searchText;
    private ImageView searchIcon;
    private ImageButton micButton;
    private ImageButton clearButton;
    private RecyclerView recyclerView;
    private MedicineSearchAdapter adapter;
    private ScanBarcodeFragment searchBarcodeFragment;
    private boolean isInCameraMode;
    private ArrayList<MedicineSearchResult> medicines;
    private boolean startedAnotherActivity;
    private boolean recievedBarcode;

    private final Object LOCK = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionChecker = new PermissionChecker(this);

        setContentView(R.layout.activity_search_medicine);

        cameraViewPlaceholder = findViewById(R.id.camera_container);
        searchBarcodeFragment = new ScanBarcodeFragment();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationContentDescription(R.string.back);

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

        downloadMedicineCollection();
        setTextSearchMode();
        setSearchBar();
        setRecycleView();
    }

    private void setRecycleView() {
        adapter = new MedicineSearchAdapter();
        recyclerView = findViewById(R.id.results_container);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void downloadMedicineCollection() {
        medicines = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("meds")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                try {
                                    MedicineSearchResult item = new MedicineSearchResult();
                                    item.nameEn = documentSnapshot.getString("en");
                                    item.nameHe = documentSnapshot.getString("he");
                                    item.barcode = documentSnapshot.getLong("barcode");
                                    medicines.add(item);
                                } catch (NullPointerException e) {
                                    Log.e(TAG, "failed on object: " + documentSnapshot.getId() + " " + documentSnapshot.toString(), e);
                                }
                            }
                            adapter.filter(searchText.getText().toString());

                        } else {
                            Log.d(TAG, "onComplete: ");
                        }
                    }
                });
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
                adapter.filter(s.toString());
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

    private void setCameraMode() {

        synchronized (LOCK) {
            recievedBarcode = false;
        }

        setTitle(R.string.search_by_camera);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextSearchMode();
            }
        });
        showSearchBarcodeFragment();
        isInCameraMode = true;
        cameraViewPlaceholder.setVisibility(View.VISIBLE);
        searchText.setText("");
        invalidateOptionsMenu();
    }

    private void showSearchBarcodeFragment() {
        cameraViewPlaceholder.setVisibility( View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.camera_container, searchBarcodeFragment)
                .commit();
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
        hideSearchBarcodeFragment();
        cameraViewPlaceholder.setVisibility(View.GONE);
        isInCameraMode = false;
        invalidateOptionsMenu();
    }

    private void hideSearchBarcodeFragment() {
        cameraViewPlaceholder.setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .remove(searchBarcodeFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startedAnotherActivity = false;
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

    @Override
    public void onBarcodeFound(String barcode) {

        synchronized (LOCK) {
            if (recievedBarcode) {
                return;
            }

            recievedBarcode = true;
        }

        long barcodeAsLong;
        try {
            barcodeAsLong = Long.parseLong(barcode);
        } catch (NumberFormatException e) {
            barcodeAsLong = -1;
        }

        if (barcodeAsLong == -1) {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid barcode")
                    .setMessage("Something went wrong. Please try again.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setCameraMode();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
        }

        for (MedicineSearchResult result : medicines) {
            if (result.barcode == barcodeAsLong) {
                startNewMedicineActivity(result);
                return;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Not found")
                .setMessage(String.format("Couldn't find medicine with barcode %s.", barcode))
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setCameraMode();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }



    private void startNewMedicineActivity(MedicineSearchResult result) {
        Intent intent = new Intent(getApplicationContext(), MedicineInfoActivity.class);
        intent.putExtra(MedicineInfoActivity.INTENT_MED_NAME, result.nameEn);
        intent.putExtra(MedicineInfoActivity.INTENT_MED_TYPE, result.type);
        startActivityForResult(intent, MedicineInfoActivity.MEDICINE_INFO_REQ);
    }

    class MedicineSearchResult {
        long barcode;
        int amount;
        String nameEn;
        String nameHe;
        int type;
    }

    class MedicineSearchAdapter extends RecyclerView.Adapter<ListItemHolder> {

        ArrayList<MedicineSearchResult> filtered;

        MedicineSearchAdapter() {
            filtered = new ArrayList<>();
        }

        public void filter(String needle) {
            filtered.clear();

            if (medicines == null) {
                return;
            }

            if (needle == null || needle.length() == 0) {
                filtered.addAll(medicines);
            } else {

                needle = needle.toLowerCase();

                for (MedicineSearchResult result : medicines) {
                    if (result.nameHe.toLowerCase().contains(needle) || result.nameEn.toLowerCase().contains(needle)) {
                        filtered.add(result);
                    }
                }

            }

            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ListItemHolder.createHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {
            final MedicineSearchResult result = filtered.get(position);
            holder.setData(
                    result.type == Medicine.TYPE_PILLS ? R.drawable.ic_tablets_solid : R.drawable.ic_syringe_solid,
                    result.nameEn,
                    ""
            );

            holder.setOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    synchronized (LOCK) {
                        if (startedAnotherActivity) {
                            return;
                        }

                        startedAnotherActivity = true;
                    }

                    startNewMedicineActivity(result);
                }
            });
        }

        @Override
        public int getItemCount() {
            return filtered.size();
        }
    }
}
