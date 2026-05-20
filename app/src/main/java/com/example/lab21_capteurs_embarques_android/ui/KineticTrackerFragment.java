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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab21_capteurs_embarques_android.components.RealTimeLineGraph;

public class KineticTrackerFragment extends Fragment implements SensorEventListener {

    private static final String PARAM_SENSOR_TYPE = "phys_type";
    private static final String PARAM_LABEL = "screen_title";

    private SensorManager mSensMan;
    private Sensor mSensor;

    private TextView mAxesReadout;
    private RealTimeLineGraph mSignalPlot;

    private int mPhysType;
    private String mTitleText;

    public static KineticTrackerFragment build(int type, String label) {
        KineticTrackerFragment f = new KineticTrackerFragment();
        Bundle b = new Bundle();
        b.putInt(PARAM_SENSOR_TYPE, type);
        b.putString(PARAM_LABEL, label);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mPhysType = getArguments().getInt(PARAM_SENSOR_TYPE);
            mTitleText = getArguments().getString(PARAM_LABEL);
        }

        mSensMan = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensMan.getDefaultSensor(mPhysType);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(35, 35, 35, 35);

        TextView head = new TextView(requireContext());
        head.setText(mTitleText);
        head.setTextSize(22);

        mAxesReadout = new TextView(requireContext());
        mAxesReadout.setTextSize(17);
        mAxesReadout.setPadding(0, 30, 0, 30);

        mSignalPlot = new RealTimeLineGraph(requireContext());
        mSignalPlot.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 650));

        root.addView(head);
        root.addView(mAxesReadout);
        root.addView(mSignalPlot);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSensor != null) {
            mSensMan.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            mAxesReadout.setText("Required sensor not found on this hardware.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensMan.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float xVal = event.values[0];
        float yVal = event.values[1];
        float zVal = event.values[2];

        float vectorSum = (float) Math.sqrt(xVal * xVal + yVal * yVal + zVal * zVal);

        mAxesReadout.setText(String.format("X: %.3f\nY: %.3f\nZ: %.3f\nMagnitude: %.3f", xVal, yVal, zVal, vectorSum));
        mSignalPlot.pushData(vectorSum);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
