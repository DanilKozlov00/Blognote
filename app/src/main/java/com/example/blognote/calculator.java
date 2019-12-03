package com.example.blognote;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;


import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.CompoundButton;


import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.blognote.Inst.AppPreferences;

import com.example.blognote.Inst.AuthenticationListener;
import com.github.mikephil.charting.charts.PieChart;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;


import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import com.example.blognote.Inst.AuthenticationDialog;
import java.io.IOException;
import java.util.ArrayList;


import java.util.List;

import eu.marcocattaneo.androidinstagramconnector.connection.Instagram;

public class calculator extends AppCompatActivity implements Drawer.OnDrawerItemClickListener, AuthenticationListener {



    private String token = null;
    private AppPreferences appPreferences = null;
    private AuthenticationDialog authenticationDialog = null;
    private Button button = null;
    private View info = null;


    private SharedPreferences settings;
    public static final String THEME_Key = "app_theme";
    public static final String APP_PREFERENCES="notepad_settings";
    public static final String APP_PREFERENCES2 = "mysettings";
    public static final String APP_PREFERENCES_COUNTER2 = "counter";
    public static final String APP_PREFERENCES_COUNTER3 = "counter2";
    public static final String APP_PREFERENCES_COUNTER4 = "counter3";
    private SharedPreferences mSettings;
    private int theme;

    Legend l;
    TextView doxodsum;
    TextView rasxodsum;
    TextView allbtn;
    String temp;
    String temp2;
    String tempall;
    public Button reset;
    public int t;
    int sum;
    int minus;
    int all;

PieChart PieChart;
    ArrayList<PieEntry> yValues = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        mSettings = getSharedPreferences(APP_PREFERENCES2, Context.MODE_PRIVATE);

        theme = settings.getInt(THEME_Key, R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doxodiandrasxodi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupNavigation(savedInstanceState, toolbar);
        doxodsum = (TextView) findViewById(R.id.textView3);
        rasxodsum = (TextView) findViewById(R.id.textView4);
        allbtn=(TextView) findViewById(R.id.all);
        reset=(Button) findViewById(R.id.reset);

        View.OnClickListener oclBtnOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sum = all = minus = 0;

               ongon(v);

            }
        };

        // присвоим обработчик кнопке OK (btnOk)
        reset.setOnClickListener(oclBtnOk);




        rasxodsum.setText("0");
        doxodsum.setText("0");
        allbtn.setText("0");





PieChart=(PieChart) findViewById(R.id.pieChart);

PieChart.setUsePercentValues(true);
PieChart.getDescription().setEnabled(false);
PieChart.setExtraLeftOffset(5);

PieChart.setDragDecelerationFrictionCoef(0.9f);

PieChart.setDrawHoleEnabled(true);
PieChart.setHoleColor(Color.WHITE);
PieChart.setTransparentCircleRadius(61f);




    PieDataSet dataSet = new PieDataSet(yValues, "");
    dataSet.setSliceSpace(3f);
    dataSet.setSelectionShift(5f);

    final int[] MY_COLORS = {Color.rgb(66, 134, 244), Color.rgb(242, 135, 21)};
    ArrayList<Integer> colors = new ArrayList<Integer>();

    for (int c : MY_COLORS) colors.add(c);

    dataSet.setColors(colors);


    PieData data = new PieData((dataSet));
    data.setValueTextSize(15f);
    data.setValueTextColor(Color.WHITE);

    PieChart.setData(data);
        l = PieChart.getLegend();
        l.setEnabled(false);






    }



    public void ongon(View view) {

        if(token!=null)
        {
            logout();
        }
        else {
            authenticationDialog = new AuthenticationDialog(this, this);
            authenticationDialog.setCancelable(true);
            authenticationDialog.show();
        }
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
                        TaskStackBuilder.create(calculator.this)
                                .addNextIntent(new Intent(calculator.this, calculator.class))
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



    /**
     * when no notes show msg in main_layout
     */

    /**
     * Start EditNoteActivity.class for Create New Note
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Запоминаем данные
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_COUNTER2, sum);
        editor.putInt(APP_PREFERENCES_COUNTER3, minus);
        editor.putInt(APP_PREFERENCES_COUNTER4, all);
        editor.apply();
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (mSettings.contains(APP_PREFERENCES_COUNTER2)) {
            // Получаем число из настроек
            sum = mSettings.getInt(APP_PREFERENCES_COUNTER2, 0);
            int defaultValue = 0;
            Intent intent = getIntent();
            t=intent.getIntExtra("money", defaultValue);
            if (t>0){
                sum=sum+t;
            }

            temp=String.valueOf(sum);
            doxodsum.setText(temp);
            if(sum!=0)
            yValues.add(new PieEntry(sum,"Доходы"));
        }
        if (mSettings.contains(APP_PREFERENCES_COUNTER3)) {
            // Получаем число из настроек
            minus = mSettings.getInt(APP_PREFERENCES_COUNTER3, 0);
            int defaultValue = 0;
            Intent intent = getIntent();
            t=intent.getIntExtra("money", defaultValue);
            if (t<0){
                minus=minus+t;
            }
            temp2=String.valueOf(minus);
            rasxodsum.setText(temp2);
            if (minus!=0)
            yValues.add(new PieEntry(minus*(-1),"Расходы"));

            if (mSettings.contains(APP_PREFERENCES_COUNTER4)) {
                all = mSettings.getInt(APP_PREFERENCES_COUNTER4, 0);
                all=sum+(minus*(-1));

                tempall=String.valueOf(all);
                allbtn.setText(tempall);


            }



        }
    }







    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        if(position==1){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if (position==2){
            Intent intent = new Intent(this,NotepadActivity.class);
            startActivity(intent);
        }
        if (position==-1){
            Intent intent = new Intent(this, settings.class);
            startActivity(intent);
        }
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
        return false;
    }


    public void Doxod(View view)
    {
        Intent intent = new Intent(calculator.this, doxodactivity.class);
        startActivity(intent);
    }

    public void Rasxod(View view)
    {
        Intent intent = new Intent(calculator.this, rasxodactivity.class);
        startActivity(intent);
    }

    @Override
    public void onTokenReceived(String auth_token) {
        if (auth_token == null)
            return;
        appPreferences.putString(AppPreferences.TOKEN, auth_token);
        token = auth_token;
        getUserInfoByAccessToken(token);
    }

    private void getUserInfoByAccessToken(String token) {
        new RequestInstagramAPI().execute();
    }

    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getResources().getString(R.string.get_user_info_url) + token);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    if (jsonData.has("id")) {
                        //сохранение данных пользователя
                        appPreferences.putString(AppPreferences.USER_ID, jsonData.getString("id"));
                        appPreferences.putString(AppPreferences.USER_NAME, jsonData.getString("username"));
                        appPreferences.putString(AppPreferences.PROFILE_PIC, jsonData.getString("profile_picture"));

                        //TODO: сохранить еще данные
                        login();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Ошибка входа!", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    public void login() {

    }

    public void logout() {
        token=null;
    }
}



