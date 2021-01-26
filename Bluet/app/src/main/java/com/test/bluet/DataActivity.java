/*
package com.test.bluet;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//MPAndroidChart import
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {
    private LineChart lineChart;
    private LineData chartData;
    private LineDataSet Set_1;
    private LineDataSet Set_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        ArrayList<Entry> entry_1 = ((MainActivity)MainActivity.context_main).entry_1;
        ArrayList<Entry> entry_2 = ((MainActivity)MainActivity.context_main).entry_2;

        lineChart = (LineChart) findViewById(R.id.chart_temp);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.getDescription().setText("Temperature and Humidity Chart");


        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMaximum(50f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextSize(15f);


        lineChart.getAxisRight().setEnabled(true);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setAxisMaximum(100f);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawZeroLine(true);
        rightAxis.setDrawGridLines(true);
        rightAxis.setTextSize(15f);


        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setTextSize(12f);

        chartData = new LineData();

        Set_1 = new LineDataSet(entry_1, "Temperature");
        Set_1.setColor(Color.RED);
        Set_1.setCircleColor(Color.BLACK);
        Set_1.setLineWidth(3f);
        Set_1.setValueTextSize(12f);
        Set_1.setAxisDependency(leftAxis.getAxisDependency());


        Set_2 = new LineDataSet(entry_2, "Humidity");
        Set_2.setColor(Color.BLUE);
        Set_2.setCircleColor(Color.BLACK);
        Set_2.setLineWidth(3f);
        Set_2.setValueTextSize(12f);
        Set_2.setAxisDependency(rightAxis.getAxisDependency());
//        Set_2.setValueFormatter(new PercentFormatter()); //시발


        chartData.addDataSet(Set_1);
        chartData.addDataSet(Set_2);
        lineChart.setData(chartData);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.btn_returntomain:
                Toast.makeText(getApplicationContext(), "return to main", Toast.LENGTH_LONG).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}*/
