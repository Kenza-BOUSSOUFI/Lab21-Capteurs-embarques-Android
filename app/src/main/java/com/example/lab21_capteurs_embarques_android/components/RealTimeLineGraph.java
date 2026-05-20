package com.example.lab21_capteurs_embarques_android.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RealTimeLineGraph extends View {

    private final List<Float> dataPoints = new ArrayList<>();
    private final int capacity = 80;

    private final Paint gridPaint = new Paint();
    private final Paint tracePaint = new Paint();
    private final Paint labelPaint = new Paint();

    public RealTimeLineGraph(Context context) {
        super(context);

        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(3);

        tracePaint.setColor(Color.rgb(0, 150, 136)); // Teal
        tracePaint.setStrokeWidth(5);
        tracePaint.setStyle(Paint.Style.STROKE);
        tracePaint.setAntiAlias(true);

        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(32);
    }

    public void pushData(float val) {
        if (dataPoints.size() >= capacity) {
            dataPoints.remove(0);
        }
        dataPoints.add(val);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        float margin = 50f;

        // Axes
        canvas.drawLine(margin, h - margin, w - margin, h - margin, gridPaint);
        canvas.drawLine(margin, margin, margin, h - margin, gridPaint);

        if (dataPoints.size() < 2) {
            canvas.drawText("Gathering signals...", w / 3f, h / 2f, labelPaint);
            return;
        }

        float minVal = Float.MAX_VALUE;
        float maxVal = -Float.MAX_VALUE;

        for (float f : dataPoints) {
            minVal = Math.min(minVal, f);
            maxVal = Math.max(maxVal, f);
        }

        if (maxVal == minVal) {
            maxVal = minVal + 1;
        }

        Path curve = new Path();
        float plotWidth = w - 2 * margin;
        float plotHeight = h - 2 * margin;

        for (int i = 0; i < dataPoints.size(); i++) {
            float posX = margin + i * (plotWidth / (capacity - 1));
            float normalized = (dataPoints.get(i) - minVal) / (maxVal - minVal);
            float posY = h - margin - (normalized * plotHeight);

            if (i == 0) {
                curve.moveTo(posX, posY);
            } else {
                curve.lineTo(posX, posY);
            }
        }

        canvas.drawPath(curve, tracePaint);
        canvas.drawText("Range: [" + minVal + " to " + maxVal + "]", margin + 20, margin + 20, labelPaint);
    }
}
