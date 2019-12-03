package com.example.blognote;

import android.annotation.SuppressLint;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.data.PieEntry;
import  com.example.blognote.calculator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class calendarnote extends AppCompatActivity implements Drawer.OnDrawerItemClickListener, View.OnClickListener {

    private SharedPreferences settings;
    public static final String THEME_Key = "app_theme";
    public static final String APP_PREFERENCES="notepad_settings";
    private int theme;
    EditText money;

    String stroka;

    Button ok_btn;
    int im;
    String[] data = {"Реклама+-","Продажи+","Пиар-", "Оплата налогов-", "Другое+-"};
    Spinner spinner;
    EditText opisanie;
    EditText who;
    int peklama;
    int sell;
    int others;
    int peklama_minus;
    int nalogi;
    int others_minus;
    int piar;


    public static final String APP_PEKLAMA= "reklama";
    public static final String APP_OTHERS = "others";
    public static final String APP_SELL= "sell";
    public static final String APP_PEKLAMA_MINUS= "reklama_minus";
    public static final String APP_OTHERS_MINUS = "others_minus";
    public static final String APP_PIAR= "piar";
    public static final String APP_NALOGI= "nalogi";
    private SharedPreferences mSettings_peklama;
    private SharedPreferences mSettings_peklama_minus;
    private SharedPreferences mSettings_others_minus;
    private SharedPreferences mSettings_others;
    private SharedPreferences mSettings_sell;
    private SharedPreferences mSettings_piar;
    private SharedPreferences mSettings_nalogi;






    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        theme = settings.getInt(THEME_Key, R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendarnote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupNavigation(savedInstanceState, toolbar);
        money = (EditText) findViewById(R.id.money);
        ok_btn = (Button) findViewById(R.id.btn_ok);
        opisanie=(EditText) findViewById(R.id.opisanie);
        who=(EditText) findViewById(R.id.who);

        ok_btn.setOnClickListener(this);
        mSettings_peklama = getSharedPreferences(APP_PEKLAMA, Context.MODE_PRIVATE);
        mSettings_sell = getSharedPreferences(APP_SELL, Context.MODE_PRIVATE);
        mSettings_others = getSharedPreferences(APP_OTHERS, Context.MODE_PRIVATE);
        mSettings_peklama_minus = getSharedPreferences(APP_PEKLAMA_MINUS, Context.MODE_PRIVATE);
        mSettings_piar = getSharedPreferences(APP_PIAR, Context.MODE_PRIVATE);
        mSettings_others_minus = getSharedPreferences(APP_OTHERS_MINUS, Context.MODE_PRIVATE);
        mSettings_nalogi = getSharedPreferences(APP_NALOGI, Context.MODE_PRIVATE);




        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Title");
        // выделяем элемент
        spinner.setSelection(2);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });




    }



    private void setupNavigation(Bundle savedInstanceState, Toolbar toolbar) {

        // Navigation menu items
        List<IDrawerItem> iDrawerItems = new ArrayList<>();

        iDrawerItems.add(new PrimaryDrawerItem().withName("Notes").withIcon(R.drawable.ic_home_black_24dp));
        iDrawerItems.add(new PrimaryDrawerItem().withName("Calendar").withIcon(R.drawable.ic_note_black_24dp));
        iDrawerItems.add(new PrimaryDrawerItem().withName("Calculator").withIcon(R.drawable.ic_home_black_24dp));

        // sticky DrawItems ; footer menu items

        List<IDrawerItem> stockyItems = new ArrayList<>();

        SwitchDrawerItem switchDrawerItem = new SwitchDrawerItem()
                .withName("Dark Theme")
                .withChecked(theme == R.style.AppTheme_Dark)
                .withIcon(R.drawable.ic_dark_theme)
                .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                        // TODO: 02/10/2018 change to darck theme and save it to settings
                        if (isChecked) {
                            settings.edit().putInt(THEME_Key, R.style.AppTheme_Dark).apply();
                        } else {
                            settings.edit().putInt(THEME_Key, R.style.AppTheme).apply();
                        }

                        // recreate app or the activity // if it's not working follow this steps
                        // MainActivity.this.recreate();

                        // this lines means wi want to close the app and open it again to change theme
                        TaskStackBuilder.create(calendarnote.this)
                                .addNextIntent(new Intent(calendarnote.this, calendarnote.class))
                                .addNextIntent(getIntent()).startActivities();
                    }
                });

        stockyItems.add(new PrimaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_settings_black_24dp));
        stockyItems.add(switchDrawerItem);

        // navigation menu header
        AccountHeader header = new AccountHeaderBuilder().withActivity(this)
                .addProfiles(new ProfileDrawerItem()
                        .withEmail("Blognote")
                        .withName("Beta")
                        .withIcon(R.mipmap.ic_launcher_round))
                .withSavedInstance(savedInstanceState)
                .withHeaderBackground(R.drawable.ic_launcher_background)
                .withSelectionListEnabledForSingleProfile(false) // we need just one profile
                .build();

        // Navigation drawer
        new DrawerBuilder()
                .withActivity(this) // activity main
                .withToolbar(toolbar) // toolbar
                .withSavedInstance(savedInstanceState) // saveInstance of activity
                .withDrawerItems(iDrawerItems) // menu items
                .withTranslucentNavigationBar(true)
                .withStickyDrawerItems(stockyItems) // footer items
                .withAccountHeader(header) // header of navigation
                .withOnDrawerItemClickListener(this) // listener for menu items click
                .build();

    }

    /**
     * when no notes show msg in main_layout
     */

    /**
     * Start EditNoteActivity.class for Create New Note
     */







    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        if(position==1){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if(position==2){
            Intent intent = new Intent(this, NotepadActivity.class);
            startActivity(intent);
        }
        if (position==3){
            Intent intent = new Intent(this,calculator.class);
            startActivity(intent);
        }
        if (position==-1){
            Intent intent = new Intent(this, settings.class);
            startActivity(intent);
        }
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Запоминаем данные
    }

    @Override
    protected void onResume() {
        super.onResume();
            // Получаем число из настроек
            peklama = mSettings_peklama.getInt(APP_PEKLAMA, 0);
            sell = mSettings_sell.getInt(APP_SELL, 0);
            others = mSettings_others.getInt(APP_OTHERS, 0);
            others_minus = mSettings_others_minus.getInt(APP_OTHERS_MINUS, 0);
            peklama_minus = mSettings_peklama_minus.getInt(APP_PEKLAMA_MINUS, 0);
            nalogi = mSettings_nalogi.getInt(APP_NALOGI, 0);
            piar = mSettings_piar.getInt(APP_PIAR, 0);


    }









    @Override
    public void onClick(View view) {

        spinner.getSelectedItemPosition();

        if (money != null) {
            im = Integer.parseInt(String.valueOf(money.getText()));
        }

        if (spinner.getSelectedItemPosition() == 0) {
            if (im>0){
                SharedPreferences.Editor editor = mSettings_peklama.edit();
            peklama += im;
            editor.putInt(APP_PEKLAMA, peklama);
                editor.apply();
            }
            else {
                SharedPreferences.Editor editor = mSettings_peklama_minus.edit();
                peklama += im;
                editor.putInt(APP_PEKLAMA, peklama);
                editor.apply();
            }

        }

        if (spinner.getSelectedItemPosition() == 1) {
            if (im>0){
                SharedPreferences.Editor editor = mSettings_sell.edit();
            sell += im;
            editor.putInt(APP_SELL,sell);
            editor.apply();}
        }

        if (spinner.getSelectedItemPosition() == 2) {
            if (im<0){
                SharedPreferences.Editor editor = mSettings_piar.edit();
            piar += im;
            editor.putInt(APP_PIAR,piar);
            editor.apply();}
        }

        if (spinner.getSelectedItemPosition() == 3) {
            if (im<0){
                SharedPreferences.Editor editor = mSettings_nalogi.edit();
            nalogi += im;
            editor.putInt(APP_NALOGI,nalogi);
            editor.apply();}
        }

        if (spinner.getSelectedItemPosition() == 4) {
            if (im>0) {
                SharedPreferences.Editor editor = mSettings_others.edit();
                others += im;
                editor.putInt(APP_OTHERS, others);
                editor.apply();
            }
            else {
                SharedPreferences.Editor editor = mSettings_others_minus.edit();
                others_minus += im;
                editor.putInt(APP_OTHERS_MINUS,others_minus);
                editor.apply();
            }

        }







        Intent intent = new Intent(this, calculator_temp.class);
        stroka=spinner.getSelectedItem().toString()+"\n"+"Сумма:"+money.getText().toString()+"\n"+"Заказчик:"+who.getText().toString()+"\n"+"Описание:"+opisanie.getText().toString();
        intent.putExtra("stroka", stroka);
        intent.putExtra("money", im);
        startActivity(intent);
    }


}



