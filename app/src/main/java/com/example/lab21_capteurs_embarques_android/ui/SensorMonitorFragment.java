package com.example.lab21_capteurs_embarques_android.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab21_capteurs_embarques_android.components.RealTimeLineGraph;

public class SensorMonitorFragment extends Fragment implements SensorEventListener {

    private static final String KEY_TYPE = "sensor_type";
    private static final String KEY_NAME = "sensor_name";
    private static final String KEY_ALGO = "processing_algo";

    private SensorManager mSensorManager;
    private Sensor mTargetSensor;

    private TextView mCurrentValueLabel;
    private RealTimeLineGraph mDataChart;

    private int mSensorType;
    private String mDisplayName;
    private String mAlgo;

    private final Handler mSimHandler = new Handler(Looper.getMainLooper());
    private float mSimStep = 0f;

    public static SensorMonitorFragment create(int type, String name, String algo) {
        SensorMonitorFragment frag = new SensorMonitorFragment();
        Bundle data = new Bundle();
        data.putInt(KEY_TYPE, type);
        data.putString(KEY_NAME, name);
        data.putString(KEY_ALGO, algo);
        frag.setArguments(data);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mSensorType = getArguments().getInt(KEY_TYPE);
            mDisplayName = getArguments().getString(KEY_NAME);
            mAlgo = getArguments().getString(KEY_ALGO);
        }

        mSensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        mTargetSensor = mSensorManager.getDefaultSensor(mSensorType);

        LinearLayout mainLayout = new LinearLayout(requireContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(30, 30, 30, 30);

        TextView header = new TextView(requireContext());
        header.setText(mDisplayName);
        header.setTextSize(24);
        header.setPadding(0, 0, 0, 20);

        mCurrentValueLabel = new TextView(requireContext());
        mCurrentValueLabel.setTextSize(18);
        mCurrentValueLabel.setPadding(0, 0, 0, 20);

        mDataChart = new RealTimeLineGraph(requireContext());
        mDataChart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 650));

        mainLayout.addView(header);
        mainLayout.addView(mCurrentValueLabel);
        mainLayout.addView(mDataChart);

        return mainLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTargetSensor != null) {
            mSensorManager.registerListener(this, mTargetSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            mCurrentValueLabel.setText("Hardware sensor missing. Simulating data...");
            initSimulation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mSimHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float processedVal = computeValue(event.values);
        refreshDisplay(processedVal);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private float computeValue(float[] rawData) {
        if ("NORM".equals(mAlgo)) {
            return (float) Math.sqrt(rawData[0] * rawData[0] + rawData[1] * rawData[1] + rawData[2] * rawData[2]);
        }
        return rawData[0];
    }

    private void refreshDisplay(float value) {
        mCurrentValueLabel.setText("Current: " + String.format("%.2f", value));
        mDataChart.pushData(value);
    }

    private void initSimulation() {
        mSimHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSimStep += 0.5f;
                float fakeVal;
                switch (mSensorType) {
                    case Sensor.TYPE_AMBIENT_TEMPERATURE: fakeVal = 22 + (float) Math.sin(mSimStep / 10) * 5; break;
                    case Sensor.TYPE_RELATIVE_HUMIDITY: fakeVal = 50 + (float) Math.cos(mSimStep / 8) * 10; break;
                    case Sensor.TYPE_PROXIMITY: fakeVal = (mSimStep % 10 < 5) ? 0f : 8f; break;
                    case Sensor.TYPE_MAGNETIC_FIELD: fakeVal = 40 + (float) Math.random() * 5; break;
                    default: fakeVal = (float) Math.random() * 100;
                }
                refreshDisplay(fakeVal);
                mSimHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}
