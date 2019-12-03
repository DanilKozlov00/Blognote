package com.example.blognote;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

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

import static com.example.blognote.calculator.APP_PREFERENCES_COUNTER2;
import static com.example.blognote.calculator.APP_PREFERENCES_COUNTER3;
import static com.example.blognote.calculator.APP_PREFERENCES_COUNTER4;
import static com.example.blognote.calendarnote.APP_NALOGI;
import static com.example.blognote.calendarnote.APP_OTHERS;
import static com.example.blognote.calendarnote.APP_OTHERS_MINUS;
import static com.example.blognote.calendarnote.APP_PEKLAMA;
import static com.example.blognote.calendarnote.APP_PEKLAMA_MINUS;
import static com.example.blognote.calendarnote.APP_PIAR;
import static com.example.blognote.calendarnote.APP_SELL;
import com.example.blognote.calculator;



public class settings extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {


    private SharedPreferences mSettings;
    Button reset;
    private int theme;
    private SharedPreferences settings;
    public static final String THEME_Key = "app_theme";
    public static final String APP_PREFERENCES="notepad_settings";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            theme = settings.getInt(THEME_Key, R.style.AppTheme);
            setTheme(theme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.settings);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setupNavigation(savedInstanceState, toolbar);



            reset=(Button) findViewById(R.id.reset);

            View.OnClickListener oclBtnOk = new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    mSettings= getSharedPreferences(APP_PREFERENCES_COUNTER2, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSettings.edit();
                    int a=22;
                    editor.putInt(APP_PREFERENCES_COUNTER2, a);
                    editor.apply();
                    mSettings= getSharedPreferences(APP_PREFERENCES_COUNTER3, Context.MODE_PRIVATE);
                    editor.putInt(APP_PREFERENCES_COUNTER3, 0);
                    editor.apply();
                    mSettings= getSharedPreferences(APP_PREFERENCES_COUNTER4, Context.MODE_PRIVATE);
                    editor.putInt(APP_PREFERENCES_COUNTER4, 0);
                    editor.apply();
                    mSettings= getSharedPreferences(APP_OTHERS, Context.MODE_PRIVATE);
                    editor.putInt(APP_OTHERS,0);
                    editor.apply();
                    mSettings= getSharedPreferences(APP_OTHERS_MINUS,Context.MODE_PRIVATE);
                    editor.putInt(APP_OTHERS_MINUS,0);
                    editor.apply();
                    mSettings= getSharedPreferences(APP_NALOGI, Context.MODE_PRIVATE);
                    editor.putInt(APP_NALOGI,0);
                    editor.apply();
                    mSettings= getSharedPreferences(APP_PEKLAMA_MINUS, Context.MODE_PRIVATE);
                    editor.putInt(APP_PEKLAMA_MINUS,0);
                    editor.apply();
                    mSettings= getSharedPreferences(APP_PEKLAMA, Context.MODE_PRIVATE);
                    editor.putInt(APP_PEKLAMA,0);
                    editor.apply();
                    mSettings= getSharedPreferences(APP_PIAR, Context.MODE_PRIVATE);
                    editor.putInt(APP_PIAR,0);
                    editor.apply();
                    mSettings= getSharedPreferences(APP_SELL, Context.MODE_PRIVATE);
                    editor.putInt(APP_SELL,0);
                    editor.apply();


                }

            };

            // присвоим обработчик кнопке OK (btnOk)
            reset.setOnClickListener(oclBtnOk);
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
                        TaskStackBuilder.create(settings.this)
                                .addNextIntent(new Intent(settings.this, settings.class))
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
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        if(position==1){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if (position==2){
            Intent intent = new Intent(this,NotepadActivity.class);
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
    public void onBackPressed() {

    }
}


