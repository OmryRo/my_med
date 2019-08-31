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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.utils.ListItemHolder;
import il.ac.huji.cs.postpc.mymeds.utils.PermissionChecker;

/*
 * api to use: https://open.fda.gov/apis/
 */
public class SearchMedicineActivity extends AppCompatActivity {

    public static final int SEACH_MEDICINE_REQUEST = 0x2010;
    public static final int SEARCH_MEDICINE_CHANGES = 0x2011;
    public static final int SEARCH_MEDICINE_NO_CHANGES = 0x2012;
    private static final int VOICE_SEARCH_REQ_ID = 0x3000;

    private PermissionChecker permissionChecker;
    private View cameraViewPlaceholder;
    private Toolbar toolbar;
    private EditText searchText;
    private ImageView searchIcon;
    private ImageButton micButton;
    private ImageButton clearButton;
    private RecyclerView recyclerView;
    private boolean isInCameraMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionChecker = new PermissionChecker(this);

        setContentView(R.layout.activity_search_medicine);

        cameraViewPlaceholder = findViewById(R.id.camera_view);

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

    private void setCameraMode() {
        setTitle(R.string.search_by_camera);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextSearchMode();
            }
        });

        isInCameraMode = true;
        cameraViewPlaceholder.setVisibility(View.VISIBLE);
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

        cameraViewPlaceholder.setVisibility(View.GONE);
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
}
