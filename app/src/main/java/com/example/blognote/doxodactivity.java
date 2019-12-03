package com.example.blognote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

import static com.example.blognote.calendarnote.APP_OTHERS;
import static com.example.blognote.calendarnote.APP_PEKLAMA;
import static com.example.blognote.calendarnote.APP_SELL;

public class doxodactivity extends AppCompatActivity {

    Legend l;
    PieChart PieChart;
    private SharedPreferences mSettings_peklama;
    private SharedPreferences mSettings_sell;
    private SharedPreferences mSettings_others;
    int peklama;
    int sell;
    int others;
    ArrayList<PieEntry> yValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doxodactivity);
        mSettings_peklama = getSharedPreferences(APP_PEKLAMA, Context.MODE_PRIVATE);
        mSettings_sell = getSharedPreferences(APP_SELL, Context.MODE_PRIVATE);
        mSettings_others = getSharedPreferences(APP_OTHERS, Context.MODE_PRIVATE);




        PieChart=(PieChart) findViewById(R.id.pieChart);

        PieChart.setUsePercentValues(true);
        PieChart.getDescription().setEnabled(false);
        PieChart.setExtraLeftOffset(5);

        PieChart.setDragDecelerationFrictionCoef(0.9f);

        PieChart.setDrawHoleEnabled(true);
        PieChart.setHoleColor(Color.WHITE);
        PieChart.setTransparentCircleRadius(61f);

        l = PieChart.getLegend();
        l.setEnabled(false);



        PieDataSet dataSet=new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        final int[] MY_COLORS = {Color.rgb(66,134,244), Color.rgb(242,135,21),Color.rgb(42,150,21)};
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c: MY_COLORS) colors.add(c);

        dataSet.setColors(colors);



        PieData data=new  PieData((dataSet));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);
        PieChart.setData(data);
        PieChart.setUsePercentValues(false);



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, calculator.class);
        startActivity(intent);
    }



    @Override
    protected void onResume() {
        super.onResume();


            // Получаем число из настроек
            peklama = mSettings_peklama.getInt(APP_PEKLAMA, 0);
            sell = mSettings_sell.getInt(APP_SELL, 0);
            others = mSettings_others.getInt(APP_OTHERS, 0);

            if (peklama!=0){
            yValues.add(new PieEntry(peklama,"Реклама"));}
            if (sell!=0){
            yValues.add(new PieEntry(sell,"Продажи"));}
            if (others!=0){
            yValues.add(new PieEntry(others,"Другое"));}


    }


}
