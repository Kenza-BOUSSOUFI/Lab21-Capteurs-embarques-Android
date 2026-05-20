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

public class OrientationCompassFragment extends Fragment implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccel;
    private Sensor mMag;

    private TextView mDirectionText;

    private final float[] mGravityData = new float[3];
    private final float[] mMagnetData = new float[3];

    private boolean mHasGravity = false;
    private boolean mHasMagnet = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDirectionText = new TextView(requireContext());
        mDirectionText.setTextSize(26);
        mDirectionText.setPadding(40, 40, 40, 40);

        mSensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMag = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return mDirectionText;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAccel != null) mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
        if (mMag != null) mSensorManager.registerListener(this, mMag, SensorManager.SENSOR_DELAY_UI);

        if (mAccel == null || mMag == null) {
            mDirectionText.setText("Compass unavailable: Sensors missing.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mGravityData, 0, 3);
            mHasGravity = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetData, 0, 3);
            mHasMagnet = true;
        }

        if (mHasGravity && mHasMagnet) {
            float[] rotationMatrix = new float[9];
            float[] orientationAngles = new float[3];

            if (SensorManager.getRotationMatrix(rotationMatrix, null, mGravityData, mMagnetData)) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles);
                float azimuthDeg = (float) Math.toDegrees(orientationAngles[0]);
                if (azimuthDeg < 0) azimuthDeg += 360;

                mDirectionText.setText(String.format("Heading: %.1f°\nPoint: %s", azimuthDeg, getCardinalPoint(azimuthDeg)));
            }
        }
    }

    private String getCardinalPoint(float deg) {
        if (deg >= 337.5 || deg < 22.5) return "NORTH";
        if (deg < 67.5) return "NORTH-EAST";
        if (deg < 112.5) return "EAST";
        if (deg < 157.5) return "SOUTH-EAST";
        if (deg < 202.5) return "SOUTH";
        if (deg < 247.5) return "SOUTH-WEST";
        if (deg < 292.5) return "WEST";
        return "NORTH-WEST";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
