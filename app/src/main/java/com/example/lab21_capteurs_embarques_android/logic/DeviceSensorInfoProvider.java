package com.example.lab21_capteurs_embarques_android.logic;

import android.hardware.Sensor;

public class DeviceSensorInfoProvider {

    public static String getSensorDescription(Sensor s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Identifier: ").append(s.getId()).append("\n");
        sb.append("Label: ").append(s.getName()).append("\n");
        sb.append("Manufacturer: ").append(s.getVendor()).append("\n");
        sb.append("Revision: ").append(s.getVersion()).append("\n");
        sb.append("Category: ").append(s.getStringType()).append("\n");
        sb.append("Numerical Type: ").append(s.getType()).append("\n");
        sb.append("Precision: ").append(s.getResolution()).append("\n");
        sb.append("Current Consumption: ").append(s.getPower()).append(" mA\n");
        sb.append("Limit: ").append(s.getMaximumRange()).append("\n");
        sb.append("Interval: ").append(s.getMinDelay()).append(" µs\n");
        return sb.toString();
    }
}
