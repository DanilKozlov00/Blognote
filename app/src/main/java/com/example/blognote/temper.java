package com.example.blognote;


import android.content.Intent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class temper extends AppCompatActivity {
    String stroka;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        String key="stroka";
        Intent intent_str = getIntent();
        stroka = intent_str.getStringExtra(key);

        if(stroka!=null){
        Intent intent_3 = new Intent(this, EditActivity.class);
        intent_3.putExtra("stroka", stroka);
        startActivity(intent_3);
        key="null";}

        else{
            Intent intent_3 = new Intent(this, MainActivity.class);
            startActivity(intent_3);

    }
    }
}

