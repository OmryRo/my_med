package il.ac.huji.cs.postpc.mymeds.activities.medicine;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;

import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.utils.TimeAdapter;
import il.ac.huji.cs.postpc.mymeds.utils.TimeItem;

public class MedicineEditorActivity extends AppCompatActivity implements OnItemSelectedListener {

//    private enum Days {Sunday,Monday, Tuesday, Wednesday, Thursday, Friday, Saturday}
    private static String TAG = MedicineEditorActivity.class.getSimpleName();
    private DatePickerDialog pickerDialog;
    private Button[] days;
    private boolean[] daysChoice = {false, false, false, false, false, false, false};
    private EditText medicineName;
    private EditText medicineNote;
    private EditText timesOfRepeate;
    private EditText numberOccurrence;
    private RadioGroup radioGroup;

    private TextView dateToEnd;
    private TextView dateToStart;

    private View repeatView;

    private ImageView addTime;
    private ImageView medicineTypeIcone;

    private boolean end = false;

    private Spinner spinner;

    private RecyclerView recyclerView;
    private TimeAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<TimeItem> contant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_editor);

        radioGroup = findViewById(R.id.radio_group);
        repeatView = findViewById(R.id.linearLayout4);
        contant = new ArrayList<>();
        days = new Button[]{
                findViewById(R.id.bt_sunday),
                findViewById(R.id.bt_monday),
                findViewById(R.id.bt_thursday),
                findViewById(R.id.bt_wednesday),
                findViewById(R.id.bt_tuesday),
                findViewById(R.id.bt_friday),
                findViewById(R.id.bt_saturday)
        };

        numberOccurrence = findViewById(R.id.et_number_occurrence);
        medicineName = findViewById(R.id.et_medicine_name);
        medicineNote = findViewById(R.id.et_medicine_notes);
        timesOfRepeate = findViewById(R.id.et_times_of_repeat);

        dateToEnd = findViewById(R.id.tv_date_picker);
        dateToEnd.setOnClickListener(getDate(dateToEnd));
        dateToStart = findViewById(R.id.tv_start_day);
        dateToStart.setOnClickListener(getDate(dateToStart));

        initSpinner();

        recyclerView = findViewById(R.id.rv_list_of_times);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TimeAdapter(this);
        recyclerView.setAdapter(adapter);
        addTime = findViewById(R.id.ib_add_time_button);

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeItem item = new TimeItem();
                contant.add(item);
                adapter.setContext(contant);
                adapter.notifyDataSetChanged();
            }
        });


    }

    private void initSpinner() {
        spinner = findViewById(R.id.repeats_unit_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeats_unit_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


    }

    public void onDayButtonClicked(View view) {
        int choiceDay = -1;
        switch (view.getId()) {
            case R.id.bt_sunday:
                choiceDay = 0;
                break;
            case R.id.bt_monday:
                choiceDay = 1;
                break;
            case R.id.bt_thursday:
                choiceDay = 2;
                break;
            case R.id.bt_wednesday:
                choiceDay = 3;
                break;
            case R.id.bt_tuesday:
                choiceDay = 4;
                break;
            case R.id.bt_friday:
                choiceDay = 5;
                break;
            case R.id.bt_saturday:
                choiceDay = 6;
                break;
        }

        if (choiceDay == -1) {
            return;
        }

        daysChoice[choiceDay] = !daysChoice[choiceDay];
        days[choiceDay].setBackgroundResource(daysChoice[choiceDay] ? R.drawable.button_pressed : R.drawable.button_default);

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radio_never:
                end = checked;
                radioGroup.check(R.id.radio_never);
                break;
            case R.id.radio_custom:
                if (checked) {
                    radioGroup.check(R.id.radio_custom);
                }
                break;
            case R.id.radio_occurrence:
                if (checked) {
                    radioGroup.check(R.id.radio_occurrence);
//                    numberOccurrence.setFocusable(true);
//                    String num = numberOccurrence.getText().toString();
//                    System.out.println(num);
                }
                break;
        }
    }

    private View.OnClickListener getDate(final TextView tv) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                pickerDialog = new DatePickerDialog(MedicineEditorActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tv.setText(MessageFormat.format("{0}/{1}/{2}", dayOfMonth, monthOfYear + 1, year));
                            }
                        }, year, month, day);
                pickerDialog.show();
            }
        };
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
//        Log.d(TAG, "Selected d"+position);

        repeatView.setVisibility(position == 1 ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
