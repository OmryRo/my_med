package il.ac.huji.cs.postpc.mymeds;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements
        CalenderFragment.CalenderFragmentListener,
        AppointmentsFragment.AppointmentsFragmentListener,
        MedicinesFragment.MedicinesFragmentListener {

    private ViewPager pager;
    private CalenderFragment calenderFragment;
    private AppointmentsFragment appointmentsFragment;
    private MedicinesFragment medicinesFragment;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setFragments();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    private void setFragments() {

        boolean isRTL = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
        final int calenderIndex = isRTL ? 2 : 0;
        final int medicinesIndex = 1;
        final int appointmentIndex = isRTL ? 0 : 2;

        calenderFragment = new CalenderFragment();
        medicinesFragment = new MedicinesFragment();
        appointmentsFragment = new AppointmentsFragment();

        navView = findViewById(R.id.nav_view);
        pager = findViewById(R.id.pager);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int currentItem = medicinesIndex;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_calender:
                        currentItem = calenderIndex;
                        setTitle(R.string.calender);
                        break;
                    case R.id.navigation_medicines:
                        currentItem = medicinesIndex;
                        setTitle(R.string.medicines);
                        break;
                    case R.id.navigation_appointments:
                        currentItem = appointmentIndex;
                        setTitle(R.string.appointments);
                        break;
                }
                pager.setCurrentItem(currentItem);
                return true;
            }
        });

        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == calenderIndex) return calenderFragment;
                if (position == medicinesIndex) return medicinesFragment;
                if (position == appointmentIndex) return appointmentsFragment;
                return null;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == calenderIndex) navView.setSelectedItemId(R.id.navigation_calender);
                else if (position == medicinesIndex) navView.setSelectedItemId(R.id.navigation_medicines);
                else if (position == appointmentIndex) navView.setSelectedItemId(R.id.navigation_appointments);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        navView.setSelectedItemId(R.id.navigation_medicines);
    }

    @Override
    public void onBackPressed() {
        if (navView.getSelectedItemId() == R.id.navigation_medicines) {
            super.onBackPressed();
        } else {
            navView.setSelectedItemId(R.id.navigation_medicines);
        }

    }
}
