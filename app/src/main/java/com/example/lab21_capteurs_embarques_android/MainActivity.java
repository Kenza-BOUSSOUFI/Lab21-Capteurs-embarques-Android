package com.example.lab21_capteurs_embarques_android;

import android.hardware.Sensor;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.lab21_capteurs_embarques_android.ui.ActivityInferenceFragment;
import com.example.lab21_capteurs_embarques_android.ui.KineticTrackerFragment;
import com.example.lab21_capteurs_embarques_android.ui.ListSensorsFragment;
import com.example.lab21_capteurs_embarques_android.ui.OrientationCompassFragment;
import com.example.lab21_capteurs_embarques_android.ui.PedometerFragment;
import com.example.lab21_capteurs_embarques_android.ui.SensorMonitorFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_root);
        NavigationView navView = findViewById(R.id.nav_panel);
        navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            displayFragment(new ListSensorsFragment());
            navView.setCheckedItem(R.id.nav_all_sensors);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment selectedFrag = null;

        if (id == R.id.nav_all_sensors) {
            selectedFrag = new ListSensorsFragment();
        } else if (id == R.id.nav_temp) {
            selectedFrag = SensorMonitorFragment.create(Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature", "VAL");
        } else if (id == R.id.nav_humid) {
            selectedFrag = SensorMonitorFragment.create(Sensor.TYPE_RELATIVE_HUMIDITY, "Relative Humidity", "VAL");
        } else if (id == R.id.nav_prox) {
            selectedFrag = SensorMonitorFragment.create(Sensor.TYPE_PROXIMITY, "Proximity Sensor", "VAL");
        } else if (id == R.id.nav_mag) {
            selectedFrag = SensorMonitorFragment.create(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic Magnitude", "NORM");
        } else if (id == R.id.nav_accel) {
            selectedFrag = KineticTrackerFragment.build(Sensor.TYPE_ACCELEROMETER, "Accelerometer Data");
        } else if (id == R.id.nav_grav) {
            selectedFrag = KineticTrackerFragment.build(Sensor.TYPE_GRAVITY, "Gravity Force");
        } else if (id == R.id.nav_gyro) {
            selectedFrag = KineticTrackerFragment.build(Sensor.TYPE_GYROSCOPE, "Rotation Rate (rad/s)");
        } else if (id == R.id.nav_steps) {
            selectedFrag = new PedometerFragment();
        } else if (id == R.id.nav_comp) {
            selectedFrag = new OrientationCompassFragment();
        } else if (id == R.id.nav_act_rec) {
            selectedFrag = new ActivityInferenceFragment();
        }

        if (selectedFrag != null) {
            displayFragment(selectedFrag);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
