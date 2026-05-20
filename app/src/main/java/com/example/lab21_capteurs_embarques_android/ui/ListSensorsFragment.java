package com.example.lab21_capteurs_embarques_android.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab21_capteurs_embarques_android.logic.DeviceSensorInfoProvider;

import java.util.List;

public class ListSensorsFragment extends Fragment {

    private SensorManager deviceSensorManager;
    private LinearLayout listLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView rootScroll = new ScrollView(requireContext());
        listLayout = new LinearLayout(requireContext());
        listLayout.setOrientation(LinearLayout.VERTICAL);
        listLayout.setPadding(32, 32, 32, 32);
        rootScroll.addView(listLayout);

        deviceSensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        populateSensorList();

        return rootScroll;
    }

    private void populateSensorList() {
        List<Sensor> allSensors = deviceSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor s : allSensors) {
            TextView infoText = new TextView(requireContext());
            infoText.setText(DeviceSensorInfoProvider.getSensorDescription(s));
            infoText.setTextSize(15);
            infoText.setPadding(10, 20, 10, 20);
            listLayout.addView(infoText);

            View line = new View(requireContext());
            line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
            line.setBackgroundColor(0xFFCCCCCC);
            listLayout.addView(line);
        }
    }
}
