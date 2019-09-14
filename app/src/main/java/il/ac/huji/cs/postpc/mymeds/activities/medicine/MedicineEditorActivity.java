package il.ac.huji.cs.postpc.mymeds.activities.medicine;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.Calendar;

import il.ac.huji.cs.postpc.mymeds.R;

public class MedicineEditorActivity extends AppCompatActivity implements OnItemSelectedListener {

    //    private enum Days {Sunday,Monday, Tuesday, Wednesday, Thursday, Friday, Saturday}
    private DatePickerDialog pickerDialog;
    private Button sunday;
    private Button monday;
    private Button thursday;
    private Button wednesday;
    private Button tuesday;
    private Button friday;
    private Button saturday;
    private Button[] days = {};
    private boolean[] daysChoice = {false, false, false, false, false, false, false};
    private EditText medicineName;
    private EditText medicineNote;
    private EditText timesOfRepeate;
    private EditText numberOccurrence;

    private RadioButton radioButtonNever;
    private RadioButton radioButtonCustom;
    private RadioButton radioButtonOccurrence;

    private TextView dateToEnd;

    private ImageButton addTime;
    private ImageView medicineTypeIcone;

    private boolean end = false;

    private Spinner spinner;

    private View divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_editor);

        sunday = findViewById(R.id.bt_sunday);
        monday = findViewById(R.id.bt_monday);
        thursday = findViewById(R.id.bt_thursday);
        wednesday = findViewById(R.id.bt_wednesday);
        tuesday = findViewById(R.id.bt_tuesday);
        friday = findViewById(R.id.bt_friday);
        saturday = findViewById(R.id.bt_saturday);

        radioButtonCustom = findViewById(R.id.radio_custom_day);
        radioButtonNever = findViewById(R.id.radio_never);
        radioButtonOccurrence = findViewById(R.id.radio_after_number_occurrence);

        days = new Button[]{sunday, monday, thursday, wednesday, tuesday, friday, saturday};

        numberOccurrence = findViewById(R.id.et_number_occurrence);
        medicineName = findViewById(R.id.et_medicine_name);
        medicineNote = findViewById(R.id.et_medicine_notes);
        timesOfRepeate = findViewById(R.id.et_times_of_repeat);

        dateToEnd = findViewById(R.id.tv_date_picker);
        initSpinner();


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
                radioButtonCustom.setChecked(false);
                radioButtonOccurrence.setChecked(false);
                break;
            case R.id.radio_custom_day:
                if (checked) {
                    radioButtonNever.setChecked(false);
                    radioButtonOccurrence.setChecked(false);
                    getDatePicker();
                }
                break;
            case R.id.radio_after_number_occurrence:
                if (checked) {
                    radioButtonNever.setChecked(false);
                    radioButtonCustom.setChecked(false);
//                    numberOccurrence.setFocusable(true);
//                    String num = numberOccurrence.getText().toString();
//                    System.out.println(num);
                }
                break;
        }
    }

    private void getDatePicker() {
        dateToEnd.setOnClickListener(new View.OnClickListener() {
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
                                dateToEnd.setText(MessageFormat.format("{0}/{1}/{2}", dayOfMonth, monthOfYear, year));
                            }
                        }, year, month, day);
                pickerDialog.show();
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
