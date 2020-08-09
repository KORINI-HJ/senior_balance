package org.tensorflow.lite.examples.classification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment;
    private ReportFragment reportFragment;
    private ProfileFragment profileFragment;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        reportFragment = new ReportFragment();
        profileFragment = new ProfileFragment();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, homeFragment).commitAllowingStateLoss();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId())
                {
                    case R.id.select_home:{
                        fragmentTransaction.replace(R.id.frameLayout, homeFragment).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.select_report:{
                        fragmentTransaction.replace(R.id.frameLayout, reportFragment).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.select_profile:{
                        fragmentTransaction.replace(R.id.frameLayout, profileFragment).commitAllowingStateLoss();
                        return true;
                    }
                    default: return false;
                }
            }
        });
    }
}
