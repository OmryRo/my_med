package il.ac.huji.cs.postpc.mymeds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

/*
 * api to use: https://open.fda.gov/apis/
 */
public class SearchMedicineActivity extends AppCompatActivity {

    public static final int SEACH_MEDICINE_REQUEST = 0x2010;
    public static final int SEARCH_MEDICINE_CHANGES = 0x2011;
    public static final int SEARCH_MEDICINE_NO_CHANGES = 0x2012;

    private EditText searchText;
    private ImageView searchIcon;
    private ImageButton micButton;
    private ImageButton clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_medicine);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.find_medicine);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationContentDescription(R.string.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(SEARCH_MEDICINE_NO_CHANGES);
                finish();
            }
        });

        setSearchBar();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_top_menu, menu);
        return true;
    }
}
