package il.ac.huji.cs.postpc.mymeds;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ScrollView;

import il.ac.huji.cs.postpc.mymeds.utils.OnSwipeVisitor;

public class HomeActivity extends AppCompatActivity implements
        CalenderFragment.CalenderFragmentListener,
        AppointmentsFragment.AppointmentsFragmentListener,
        MedicinesFragment.MedicinesFragmentListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        OnSwipeVisitor.OnSwipeListener {

    private static final int SLIDE_RIGHT = -1;
    private static final int SLIDE_LEFT = 1;
    private static final int SLIDE_NONE = 0;

    private ScrollView scrollView;
    private CalenderFragment calenderFragment;
    private AppointmentsFragment appointmentsFragment;
    private MedicinesFragment medicinesFragment;
    private Fragment currentFragment;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navView = findViewById(R.id.nav_view);
        scrollView = findViewById(R.id.home_main);
        scrollView.setOnTouchListener(new OnSwipeVisitor(this, this));
        appointmentsFragment = new AppointmentsFragment();
        medicinesFragment = new MedicinesFragment();
        calenderFragment = new CalenderFragment();

        navView.setOnNavigationItemSelectedListener(this);
        navView.setSelectedItemId(R.id.navigation_medicines);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_calender:
                showCalenderFragment();
                return true;
            case R.id.navigation_medicines:
                showMedicinesFragment();
                return true;
            case R.id.navigation_appointments:
                showAppointmentFragment();
                return true;
        }
        return false;
    }


    private void showCalenderFragment() {
        int slideDirection = SLIDE_NONE;
        if (currentFragment == calenderFragment) {
            return;
        } else if (currentFragment == appointmentsFragment) {
            slideDirection = SLIDE_LEFT;
        } else if (currentFragment == medicinesFragment) {
            slideDirection = SLIDE_RIGHT;
        }

        setFragment(calenderFragment, slideDirection);
    }

    private void showAppointmentFragment() {


        int slideDirection = SLIDE_NONE;
        if (currentFragment == appointmentsFragment) {
            return;
        } else if (currentFragment == medicinesFragment) {
            slideDirection = SLIDE_LEFT;
        } else if (currentFragment == calenderFragment) {
            slideDirection = SLIDE_RIGHT;
        }

        setFragment(appointmentsFragment, slideDirection);
    }

    private void showMedicinesFragment() {

        int slideDirection = SLIDE_NONE;
        if (currentFragment == medicinesFragment) {
            return;
        } else if (currentFragment == calenderFragment) {
            slideDirection = SLIDE_LEFT;
        } else if (currentFragment == appointmentsFragment) {
            slideDirection = SLIDE_RIGHT;
        }

        setFragment(medicinesFragment, slideDirection);
    }

    private void setFragment(Fragment fragment, int slide) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (slide) {
            case SLIDE_LEFT:
                transaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
                break;
            case SLIDE_RIGHT:
                transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
                break;
        }

        currentFragment = fragment;
        transaction.replace(R.id.home_main, fragment).commit();
    }

    @Override
    public void onSwipeRight() {
        int selected = navView.getSelectedItemId();
        switch (selected) {
            case (R.id.navigation_appointments):
                navView.setSelectedItemId(R.id.navigation_calender);
                break;
            case (R.id.navigation_calender):
                navView.setSelectedItemId(R.id.navigation_medicines);
                break;
            case (R.id.navigation_medicines):
                navView.setSelectedItemId(R.id.navigation_appointments);
                break;
        }
    }

    @Override
    public void onSwipeLeft() {

        int selected = navView.getSelectedItemId();
        switch (selected) {
            case (R.id.navigation_appointments):
                navView.setSelectedItemId(R.id.navigation_medicines);
                break;
            case (R.id.navigation_calender):
                navView.setSelectedItemId(R.id.navigation_appointments);
                break;
            case (R.id.navigation_medicines):
                navView.setSelectedItemId(R.id.navigation_calender);
                break;
        }
    }
}
