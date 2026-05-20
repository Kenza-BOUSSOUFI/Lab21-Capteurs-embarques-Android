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

import java.util.LinkedList;
import java.util.Queue;

public class ActivityInferenceFragment extends Fragment implements SensorEventListener {

    private SensorManager mSensManager;
    private Sensor mAccel;

    private TextView mStatusView;

    private final float[] mGravityEstimate = new float[3];
    private final Queue<Float> mMagnitudeBuffer = new LinkedList<>();

    private static final int BUFFER_CAPACITY = 30;
    private static final float FILTER_COEFF = 0.85f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mStatusView = new TextView(requireContext());
        mStatusView.setTextSize(20);
        mStatusView.setPadding(30, 30, 30, 30);

        mSensManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        return mStatusView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAccel != null) {
            mSensManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_GAME);
        } else {
            mStatusView.setText("Accelerometer not available.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Basic Low-pass filter to isolate gravity
        mGravityEstimate[0] = FILTER_COEFF * mGravityEstimate[0] + (1 - FILTER_COEFF) * x;
        mGravityEstimate[1] = FILTER_COEFF * mGravityEstimate[1] + (1 - FILTER_COEFF) * y;
        mGravityEstimate[2] = FILTER_COEFF * mGravityEstimate[2] + (1 - FILTER_COEFF) * z;

        // Linear acceleration (removing gravity)
        float linX = x - mGravityEstimate[0];
        float linY = y - mGravityEstimate[1];
        float linZ = z - mGravityEstimate[2];

        float totalMotion = (float) Math.sqrt(linX * linX + linY * linY + linZ * linZ);

        if (mMagnitudeBuffer.size() >= BUFFER_CAPACITY) {
            mMagnitudeBuffer.poll();
        }
        mMagnitudeBuffer.add(totalMotion);

        String result = analyzeState(x, y, z);
        mStatusView.setText(String.format("Raw: [%.2f, %.2f, %.2f]\n\nMotion Level: %.2f\n\nDetected State:\n%s", x, y, z, totalMotion, result));
    }

    private String analyzeState(float x, float y, float z) {
        if (mMagnitudeBuffer.size() < BUFFER_CAPACITY) return "Calibrating...";

        float sum = 0, maxVal = 0;
        for (float f : mMagnitudeBuffer) {
            sum += f;
            if (f > maxVal) maxVal = f;
        }
        float average = sum / mMagnitudeBuffer.size();

        float varSum = 0;
        for (float f : mMagnitudeBuffer) {
            varSum += Math.pow(f - average, 2);
        }
        float stdDev = (float) Math.sqrt(varSum / mMagnitudeBuffer.size());

        if (maxVal > 12.0f) return "Jumping / High Impact";
        if (stdDev > 1.3f) return "Walking / Moving";
        if (Math.abs(z) > 8.5f) return "Stationary (Flat Surface)";
        if (Math.abs(y) > 7.5f || Math.abs(x) > 7.5f) return "Sitting / Standing (Upright)";

        return "Idle / Stable";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
