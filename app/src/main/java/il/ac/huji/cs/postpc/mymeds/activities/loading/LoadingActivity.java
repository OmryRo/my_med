package il.ac.huji.cs.postpc.mymeds.activities.loading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import il.ac.huji.cs.postpc.mymeds.R;
import il.ac.huji.cs.postpc.mymeds.activities.home.HomeActivity;

public class LoadingActivity extends AppCompatActivity {

    boolean aborted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!aborted) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
                finish();
            }
        }, 2000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        aborted = true;
        finish();
    }
}
