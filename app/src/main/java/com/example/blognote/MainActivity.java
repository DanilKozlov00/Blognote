package com.example.blognote;

import android.Manifest;
import android.app.TaskStackBuilder;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.example.blognote.content.CalendarCursor;
import com.example.blognote.content.EventCursor;
import com.example.blognote.content.EventsQueryHandler;
import com.example.blognote.weather.WeatherSyncService;
import com.example.blognote.widget.AgendaAdapter;
import com.example.blognote.widget.AgendaView;
import com.example.blognote.widget.CalendarSelectionView;
import com.example.blognote.widget.EventCalendarView;

import static com.example.blognote.NotepadActivity.APP_PREFERENCES;
import static com.example.blognote.NotepadActivity.THEME_Key;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, Drawer.OnDrawerItemClickListener {

    private static final String STATE_TOOLBAR_TOGGLE = "state:toolbarToggle";
    private static final int REQUEST_CODE_CALENDAR = 0;
    private static final int REQUEST_CODE_LOCATION = 1;
    private static final String SEPARATOR = ",";
    private static final int LOADER_CALENDARS = 0;
    private static final int LOADER_LOCAL_CALENDAR = 1;

    private final SharedPreferences.OnSharedPreferenceChangeListener mWeatherChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
                    if (TextUtils.equals(key, WeatherSyncService.PREF_WEATHER_TODAY) ||
                            TextUtils.equals(key, WeatherSyncService.PREF_WEATHER_TOMORROW)) {
                        loadWeather();
                    }
                }
            };
    private final CalendarSelectionView.OnSelectionChangeListener mCalendarSelectionListener
            = new CalendarSelectionView.OnSelectionChangeListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onSelectionChange(long id, boolean enabled) {
            if (!enabled) {
                mExcludedCalendarIds.add(String.valueOf(id));
            } else {
                mExcludedCalendarIds.remove(String.valueOf(id));
            }
            mCalendarView.invalidateData();
            mAgendaView.invalidateData();
        }
    };
    private final Coordinator mCoordinator = new Coordinator();
    private View mCoordinatorLayout;
    private CheckedTextView mToolbarToggle;
    private EventCalendarView mCalendarView;
    private AgendaView mAgendaView;
    private FloatingActionButton mFabAdd;
    private CalendarSelectionView mCalendarSelectionView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mDrawer;
    private final HashSet<String> mExcludedCalendarIds = new HashSet<>();
    private boolean mWeatherEnabled, mPendingWeatherEnabled;
    private SharedPreferences settings;
    public static final String THEME_Key = "app_theme";
    public static final String APP_PREFERENCES="notepad_settings";
    private int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        theme = settings.getInt(THEME_Key, R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setUpPreferences();
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        setUpContentView();
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar);
        setupNavigation(savedInstanceState, toolbar2);
    }

    private void setupNavigation(Bundle savedInstanceState, Toolbar toolbar2) {

        // Navigation menu items
        List<IDrawerItem> iDrawerItems = new ArrayList<>();
        iDrawerItems.add(new PrimaryDrawerItem().withName("Календарь").withIcon(R.drawable.ic_home_black_24dp));
        iDrawerItems.add(new PrimaryDrawerItem().withName("Заметки").withIcon(R.drawable.ic_note_black_24dp));
        iDrawerItems.add(new PrimaryDrawerItem().withName("Калькулятор").withIcon(R.drawable.ic_note_black_24dp));

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
                        TaskStackBuilder.create(MainActivity.this)
                                .addNextIntent(new Intent(MainActivity.this, MainActivity.class))
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
                .withToolbar(toolbar2) // toolbar
                .withSavedInstance(savedInstanceState) // saveInstance of activity
                .withDrawerItems(iDrawerItems) // menu items
                .withTranslucentNavigationBar(true)
                .withStickyDrawerItems(stockyItems) // footer items
                .withAccountHeader(header) // header of navigation
                .withOnDrawerItemClickListener(this) // listener for menu items click
                .build();

    }




    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCoordinator.restoreState(savedInstanceState);
        if (savedInstanceState.getBoolean(STATE_TOOLBAR_TOGGLE, false)) {
            View toggleButton = findViewById(R.id.toolbar_toggle_frame);
            if (toggleButton != null) { // can be null as disabled in landscape
                toggleButton.performClick();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        mCoordinator.coordinate(mToolbarToggle, mCalendarView, mAgendaView);
        if (checkCalendarPermissions()) {
            loadEvents();
        } else {
            toggleEmptyView(true);
        }
        if (mWeatherEnabled && !checkLocationPermissions()) {
            explainLocationPermissions();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_weather).setChecked(mWeatherEnabled);
        switch (CalendarUtils.sWeekStart) {
            case Calendar.SATURDAY:
                menu.findItem(R.id.action_week_start_saturday).setChecked(true);
                break;
            case Calendar.SUNDAY:
                menu.findItem(R.id.action_week_start_sunday).setChecked(true);
                break;
            case Calendar.MONDAY:
                menu.findItem(R.id.action_week_start_monday).setChecked(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_today) {
            mCoordinator.reset();
            return true;
        }
        if (item.getItemId() == R.id.action_weather) {
            mPendingWeatherEnabled = !mWeatherEnabled;
            if (!mWeatherEnabled && !checkLocationPermissions()) {
                requestLocationPermissions();
            } else {
                toggleWeather();
            }
            return true;
        }
        if (item.getItemId() == R.id.action_week_start_saturday ||
                item.getItemId() == R.id.action_week_start_sunday ||
                item.getItemId() == R.id.action_week_start_monday) {
            if (!item.isChecked()) {
                changeWeekStart(item.getItemId());
            }
            return true;
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCoordinator.saveState(outState);
        outState.putBoolean(STATE_TOOLBAR_TOGGLE, mToolbarToggle.isChecked());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCalendarView.deactivate();
        mAgendaView.setAdapter(null); // force detaching adapter
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(CalendarUtils.PREF_CALENDAR_EXCLUSIONS,
                        TextUtils.join(SEPARATOR, mExcludedCalendarIds))
                .apply();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(mWeatherChangeListener);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawer)) {
            mDrawerLayout.closeDrawer(mDrawer);
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CALENDAR:
                if (checkCalendarPermissions()) {
                    toggleEmptyView(false);
                    loadEvents();
                } else {
                    toggleEmptyView(true);
                }
                break;
            case REQUEST_CODE_LOCATION:
                if (checkLocationPermissions()) {
                    toggleWeather();
                } else {
                    explainLocationPermissions();
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (id == LOADER_LOCAL_CALENDAR) {
            selection = CalendarContract.Calendars.ACCOUNT_TYPE + "=?";
            selectionArgs = new String[]{String.valueOf(CalendarContract.ACCOUNT_TYPE_LOCAL)};
        }
        return new CursorLoader(this,
                CalendarContract.Calendars.CONTENT_URI,
                CalendarCursor.PROJECTION, selection, selectionArgs,
                CalendarContract.Calendars.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_CALENDARS:
                if (data != null && data.moveToFirst()) {
                    mCalendarSelectionView.swapCursor(new CalendarCursor(data), mExcludedCalendarIds);
                }
                break;
            case LOADER_LOCAL_CALENDAR:
                if (data == null || data.getCount() == 0) {
                    createLocalCalendar();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCalendarSelectionView.swapCursor(null, null);
    }

    private void setUpPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mWeatherEnabled = mPendingWeatherEnabled = sp.getBoolean(
                WeatherSyncService.PREF_WEATHER_ENABLED, false);
        String exclusions = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(CalendarUtils.PREF_CALENDAR_EXCLUSIONS, null);
        if (!TextUtils.isEmpty(exclusions)) {
            mExcludedCalendarIds.addAll(Arrays.asList(exclusions.split(SEPARATOR)));
        }
        CalendarUtils.sWeekStart = sp.getInt(CalendarUtils.PREF_WEEK_START, Calendar.SUNDAY);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(mWeatherChangeListener);
    }

    private void setUpContentView() {
        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        mCalendarSelectionView = (CalendarSelectionView) findViewById(R.id.list_view_calendar);
        //noinspection ConstantConditions
        mCalendarSelectionView.setOnSelectionChangeListener(mCalendarSelectionListener);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mToolbarToggle = (CheckedTextView) findViewById(R.id.toolbar_toggle);
        View toggleButton = findViewById(R.id.toolbar_toggle_frame);
        if (toggleButton != null) { // can be null as disabled in landscape
            toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mToolbarToggle.toggle();
                    toggleCalendarView();
                }
            });
        }
        mCalendarView = (EventCalendarView) findViewById(R.id.calendar_view);
        mAgendaView = (AgendaView) findViewById(R.id.agenda_view);
        mFabAdd = (FloatingActionButton) findViewById(R.id.fab);
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
        //noinspection ConstantConditions
        mFabAdd.hide();
        mCalendarView.setVisibility(View.VISIBLE);
    }


    private void toggleCalendarView() {
        if (mToolbarToggle.isChecked()) {
            mCalendarView.setVisibility(View.VISIBLE);
        } else {
            mCalendarView.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void toggleEmptyView(boolean visible) {
        if (visible) {
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
            findViewById(R.id.empty).bringToFront();
            findViewById(R.id.button_permission)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestCalendarPermissions();
                        }
                    });
        } else {
            findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void changeWeekStart(@IdRes int selection) {
        switch (selection) {
            case R.id.action_week_start_saturday:
                CalendarUtils.sWeekStart = Calendar.SATURDAY;
                break;
            case R.id.action_week_start_sunday:
                CalendarUtils.sWeekStart = Calendar.SUNDAY;
                break;
            case R.id.action_week_start_monday:
                CalendarUtils.sWeekStart = Calendar.MONDAY;
                break;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt(CalendarUtils.PREF_WEEK_START, CalendarUtils.sWeekStart)
                .apply();
        supportInvalidateOptionsMenu();
        mCoordinator.reset();
    }

    private void createEvent() {
        startActivity(new Intent(this, calendarnote.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadEvents() {
        getSupportLoaderManager().initLoader(LOADER_CALENDARS, null, this);
        getSupportLoaderManager().initLoader(LOADER_LOCAL_CALENDAR, null, this);
        mFabAdd.show();
        mCalendarView.setCalendarAdapter(new CalendarCursorAdapter(this, mExcludedCalendarIds));
        mAgendaView.setAdapter(new AgendaCursorAdapter(this, mExcludedCalendarIds));
        loadWeather();
    }

    private void toggleWeather() {
        mWeatherEnabled = mPendingWeatherEnabled;
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(WeatherSyncService.PREF_WEATHER_ENABLED, mWeatherEnabled)
                .apply();
        supportInvalidateOptionsMenu();
        loadWeather();
    }

    private void loadWeather() {
        mAgendaView.setWeather(mWeatherEnabled ? WeatherSyncService.getSyncedWeather(this) : null);
    }

    private void createLocalCalendar() {
        String name = getString(R.string.default_calendar_name);
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Calendars.ACCOUNT_NAME, BuildConfig.APPLICATION_ID);
        cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        cv.put(CalendarContract.Calendars.NAME, name);
        cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, name);
        cv.put(CalendarContract.Calendars.CALENDAR_COLOR, 0);
        cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, BuildConfig.APPLICATION_ID);
        new CalendarQueryHandler(getContentResolver())
                .startInsert(0, null, CalendarContract.Calendars.CONTENT_URI
                        .buildUpon()
                        .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "1")
                        .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME,
                                BuildConfig.APPLICATION_ID)
                        .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                                CalendarContract.ACCOUNT_TYPE_LOCAL)
                        .build()
                        , cv);
    }

    @VisibleForTesting
    protected boolean checkCalendarPermissions() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) |
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @VisibleForTesting
    protected boolean checkLocationPermissions() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) |
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @VisibleForTesting
    protected void requestCalendarPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR},
                REQUEST_CODE_CALENDAR);
    }

    @VisibleForTesting
    protected void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE_LOCATION);
    }

    private void explainLocationPermissions() {
        Snackbar.make(mCoordinatorLayout, R.string.location_permission_required,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.grant_access, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestLocationPermissions();
                    }
                })
                .show();
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        if (position==2){
            Intent intent = new Intent(this, NotepadActivity.class);
            startActivity(intent);
        }
        if (position==3){
            Intent intent = new Intent(this, calculator.class);
            startActivity(intent);
        }
        if (position==-1){
            Intent intent = new Intent(this, settings.class);
            startActivity(intent);
        }
        return false;
    }

    /**
     * Coordinator utility that synchronizes widgets as selected date changes
     */
    static class Coordinator {
        private static final String STATE_SELECTED_DATE = "state:selectedDate";

        private final EventCalendarView.OnChangeListener mCalendarListener
                = new EventCalendarView.OnChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSelectedDayChange(long calendarDate) {
                sync(calendarDate, mCalendarView);
            }
        };
        private final AgendaView.OnDateChangeListener mAgendaListener
                = new AgendaView.OnDateChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSelectedDayChange(long dayMillis) {
                sync(dayMillis, mAgendaView);
            }
        };
        private TextView mTextView;
        private EventCalendarView mCalendarView;
        private AgendaView mAgendaView;
        private long mSelectedDayMillis = CalendarUtils.NO_TIME_MILLIS;

        /**
         * Set up widgets to be synchronized
         * @param textView      title
         * @param calendarView  calendar view
         * @param agendaView    agenda view
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void coordinate(@NonNull TextView textView,
                               @NonNull EventCalendarView calendarView,
                               @NonNull AgendaView agendaView) {
            if (mCalendarView != null) {
                mCalendarView.setOnChangeListener(null);
            }
            if (mAgendaView != null) {
                mAgendaView.setOnDateChangeListener(null);
            }
            mTextView = textView;
            mCalendarView = calendarView;
            mAgendaView = agendaView;
            if (mSelectedDayMillis < 0) {
                mSelectedDayMillis = CalendarUtils.today();
            }
            mCalendarView.setSelectedDay(mSelectedDayMillis);
            agendaView.setSelectedDay(mSelectedDayMillis);
            updateTitle(mSelectedDayMillis);
            calendarView.setOnChangeListener(mCalendarListener);
            agendaView.setOnDateChangeListener(mAgendaListener);
        }

        void saveState(Bundle outState) {
            outState.putLong(STATE_SELECTED_DATE, mSelectedDayMillis);
        }

        void restoreState(Bundle savedState) {
            mSelectedDayMillis = savedState.getLong(STATE_SELECTED_DATE,
                    CalendarUtils.NO_TIME_MILLIS);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        void reset() {
            mSelectedDayMillis = CalendarUtils.today();
            if (mCalendarView != null) {
                mCalendarView.reset();
            }
            if (mAgendaView != null) {
                mAgendaView.reset();
            }
            updateTitle(mSelectedDayMillis);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void sync(long dayMillis, View originator) {
            mSelectedDayMillis = dayMillis;
            if (originator != mCalendarView) {
                mCalendarView.setSelectedDay(dayMillis);
            }
            if (originator != mAgendaView) {
                mAgendaView.setSelectedDay(dayMillis);
            }
            updateTitle(dayMillis);
        }

        private void updateTitle(long dayMillis) {
            mTextView.setText(CalendarUtils.toMonthString(mTextView.getContext(), dayMillis));
        }
    }

    static class AgendaCursorAdapter extends AgendaAdapter {

        @VisibleForTesting
        final DayEventsQueryHandler mHandler;

        public AgendaCursorAdapter(Context context, Collection<String> excludedCalendarIds) {
            super(context);
            mHandler = new DayEventsQueryHandler(context.getContentResolver(), this,
                    excludedCalendarIds);
        }

        @Override
        protected void loadEvents(long timeMillis) {
            mHandler.startQuery(timeMillis, timeMillis, timeMillis + DateUtils.DAY_IN_MILLIS);
        }
    }

    static class CalendarCursorAdapter extends EventCalendarView.CalendarAdapter {
        private final MonthEventsQueryHandler mHandler;

        public CalendarCursorAdapter(Context context, Collection<String> excludedCalendarIds) {
            mHandler = new MonthEventsQueryHandler(context.getContentResolver(), this,
                    excludedCalendarIds);
        }

        @Override
        protected void loadEvents(long monthMillis) {
            long startTimeMillis = CalendarUtils.monthFirstDay(monthMillis),
                    endTimeMillis = startTimeMillis + DateUtils.DAY_IN_MILLIS *
                            CalendarUtils.monthSize(monthMillis);
            mHandler.startQuery(monthMillis, startTimeMillis, endTimeMillis);
        }
    }

    static class DayEventsQueryHandler extends EventsQueryHandler {

        private final AgendaCursorAdapter mAgendaCursorAdapter;

        public DayEventsQueryHandler(ContentResolver cr,
                                     AgendaCursorAdapter agendaCursorAdapter,
                                     @NonNull Collection<String> excludedCalendarIds) {
            super(cr, excludedCalendarIds);
            mAgendaCursorAdapter = agendaCursorAdapter;
        }

        @Override
        protected void handleQueryComplete(int token, Object cookie, EventCursor cursor) {
            mAgendaCursorAdapter.bindEvents((Long) cookie, cursor);
        }
    }

    static class MonthEventsQueryHandler extends EventsQueryHandler {

        private final CalendarCursorAdapter mAdapter;

        public MonthEventsQueryHandler(ContentResolver cr,
                                       CalendarCursorAdapter adapter,
                                       @NonNull Collection<String> excludedCalendarIds) {
            super(cr, excludedCalendarIds);
            mAdapter = adapter;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void handleQueryComplete(int token, Object cookie, EventCursor cursor) {
            mAdapter.bindEvents((Long) cookie, cursor);
        }
    }

    static class CalendarQueryHandler extends AsyncQueryHandler {

        public CalendarQueryHandler(ContentResolver cr) {
            super(cr);
        }
    }


}
