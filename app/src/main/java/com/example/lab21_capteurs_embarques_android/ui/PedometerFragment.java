package com.example.lab21_capteurs_embarques_android.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PedometerFragment extends Fragment implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mStepSensor;
    private TextView mStepDisplay;
    private int mStepCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mStepDisplay = new TextView(requireContext());
        mStepDisplay.setTextSize(30);
        mStepDisplay.setPadding(50, 50, 50, 50);
        mStepDisplay.setText("Steps: 0");

        mSensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        return mStepDisplay;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mStepSensor != null) {
            mSensorManager.registerListener(this, mStepSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            mStepDisplay.setText("Step Counter sensor not available on this device.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            mStepCount = (int) event.values[0];
            mStepDisplay.setText("Total Steps: " + mStepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
