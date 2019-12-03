package com.example.blognote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

import static com.example.blognote.calendarnote.APP_NALOGI;
import static com.example.blognote.calendarnote.APP_OTHERS_MINUS;
import static com.example.blognote.calendarnote.APP_PEKLAMA_MINUS;
import static com.example.blognote.calendarnote.APP_PIAR;


public class rasxodactivity extends AppCompatActivity {

    Legend l;
    PieChart PieChart;
    private SharedPreferences mSettings_peklama;
    private SharedPreferences mSettings_nalogi;
    private SharedPreferences mSettings_piar;
    private SharedPreferences mSettings_others;
    int peklama_minus;
    int nalogi;
    int piar;
    int others_minus;
    ArrayList<PieEntry> yValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rasxoddactivity);
        mSettings_peklama = getSharedPreferences(APP_PEKLAMA_MINUS, Context.MODE_PRIVATE);
        mSettings_piar = getSharedPreferences(APP_PIAR, Context.MODE_PRIVATE);
        mSettings_others = getSharedPreferences(APP_OTHERS_MINUS, Context.MODE_PRIVATE);
        mSettings_nalogi = getSharedPreferences(APP_NALOGI, Context.MODE_PRIVATE);





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
        final int[] MY_COLORS = {Color.rgb(66,134,244), Color.rgb(242,135,21),Color.rgb(42,150,21),Color.rgb(202,150,21)};
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
            peklama_minus = mSettings_peklama.getInt(APP_PEKLAMA_MINUS, 0);
            piar = mSettings_piar.getInt(APP_PIAR, 0);
            others_minus = mSettings_others.getInt(APP_OTHERS_MINUS, 0);
            nalogi = mSettings_nalogi.getInt(APP_NALOGI, 0);

            if (peklama_minus!=0){
            yValues.add(new PieEntry(peklama_minus*(-1),"Реклама"));}
            if (piar!=0){
            yValues.add(new PieEntry(piar*(-1),"Пиар"));}
            if (others_minus!=0){
            yValues.add(new PieEntry(others_minus*(-1),"Другое"));}
            if (nalogi!=0){
                yValues.add(new PieEntry(nalogi*(-1),"Налоги"));}

    }


}
