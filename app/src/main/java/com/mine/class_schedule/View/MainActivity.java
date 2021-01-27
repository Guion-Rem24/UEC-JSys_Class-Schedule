package com.mine.class_schedule.View;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mine.class_schedule.Model.MyClass.MyClass;
import com.mine.class_schedule.R;
import com.mine.class_schedule.ui.dashboard.DashboardFragment;
import com.mine.class_schedule.ui.dashboard.DashboardViewModel;
import com.mine.class_schedule.ui.home.HomeFragment;
import com.mine.class_schedule.ui.home.HomeViewModel;
import com.mine.class_schedule.ui.notifications.NotificationsFragment;
import com.mine.class_schedule.ui.notifications.NotificationsViewModel;

import java.util.List;
import java.util.Objects;

// Alert Sample
 // https://mura-hiro.com/android-dev-sample-alarm-clock/
 // https://akira-watson.com/android/alarmmanager-timer.html

/**
 * cite for System app
 * https://stackoverflow.com/questions/45995322/oreo-api-26-drawoverlay-draw-over-status-bar
 */

public class MainActivity extends AppCompatActivity{


    private static class FragNum{
        public static final int Home=0;
        public static final int DashBoard=1;
        public static final int Notification=2;
    }

    private HomeViewModel homeViewModel;
    private DashboardViewModel dashboardViewModel;
    private NotificationsViewModel notificationsViewModel;
    static private HomeFragment homeFragment;
    static private DashboardFragment dashboardFragment;
    static private NotificationsFragment notificationsFragment;

    private static final String TAG = "MainActivity";
    private ViewPager2 viewPager;
    private ViewPagerAdapter pagerAdapter;
    private NavController navController;
    //  For ViewPager
    // BottomNavigationViewPager viewPager;
    private MenuItem currentItem;
    private BottomNavigationView navView;
    private static Toolbar toolbar;
    public static MainActivity current_pointer_;
    public final static int REQUEST_SYSTEM_OVERLAY = 1;
    public static final int REQUEST_CODE_EDIT_CLASS = 10;
    private static ActivityManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar);
        current_pointer_ = this;
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("Home");
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        pagerAdapter = new ViewPagerAdapter(this);
//        For ViewPager
        //  viewPager = new BottomNavigationViewPager(getApplicationContext());

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false); // forbid to swipe

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();

// ViewPager2 と navController は共存しない
/*
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.viewPager);
        navController = navHostFragment.getNavController();
        navController = Navigation.findNavController(this, R.id.viewPager);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
 */


        navView.setOnNavigationItemSelectedListener(
                // new BottomNavigationView.OnNavigationItemSelectedListener()
                item -> {
                    switch(item.getItemId()){
                        case R.id.navigation_home:
                            Toast.makeText(getApplicationContext(), "Home..", Toast.LENGTH_LONG).show();
                            toolbar.setTitle("Home");
                            viewPager.setCurrentItem(FragNum.Home, false);
                            return true;

                        case R.id.navigation_dashboard:
                            Toast.makeText(getApplicationContext(), "DashBoard..", Toast.LENGTH_LONG).show();
                            toolbar.setTitle("DashBoard");
                            viewPager.setCurrentItem(FragNum.DashBoard, false);
                            return true;

                        case R.id.navigation_notifications:
                            Toast.makeText(getApplicationContext(), "Notification..", Toast.LENGTH_LONG).show();
                            toolbar.setTitle("Notifications");
                            viewPager.setCurrentItem(FragNum.Notification, false);
                            return true;
                    }
                    return false;
                });
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        // extends FragmentStateAdapter
        public ViewPagerAdapter(FragmentActivity fa){
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch(position){
                case FragNum.Home:
                    homeFragment = new HomeFragment();
                    return homeFragment;
                case FragNum.DashBoard:
                    dashboardFragment = new DashboardFragment();
                    return dashboardFragment;
                case FragNum.Notification:
                    notificationsFragment =  new NotificationsFragment();
                    return notificationsFragment;
            }
            // position given as an argument do not match any FragNums,
            // returns HomeFragment
            return new HomeFragment();
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    public boolean checkOverlayPermission() {
        if(Build.VERSION.SDK_INT < 23) return true;
        return Settings.canDrawOverlays(this);
    }

    // OverlayのPermissionを要求
    //
    public void requestOverlayPermission(){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                                    Uri.parse("package:${getPackageName()}"));
                                    Uri.parse("package:"+ getPackageName()));
        this.startActivityForResult(intent, REQUEST_SYSTEM_OVERLAY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Overlay Permissionの要求後
        if (requestCode == REQUEST_SYSTEM_OVERLAY){
            toolbar.setTitle("DashBoard");
            viewPager.setCurrentItem(FragNum.DashBoard, false);
//            if(checkOverlayPermission()) {
//                /**
//                 *
//                 */
//            }

        }

        if (requestCode == REQUEST_CODE_EDIT_CLASS && resultCode == RESULT_OK){
            MyClass classData = (MyClass) data.getSerializableExtra("ClassData");
//            byte pos = data.getByteExtra(EditClassActivity.EXTRA_REPLY+EditClassActivity.DATA_POS, (byte)0xFF);
//            Log.d(TAG, TYPE_CLASS.castToString(pos));
//            if(pos<0) throw new IllegalStateException();
//            MyClass mClass = new MyClass(pos,
//                                         data.getStringExtra(EditClassActivity.EXTRA_REPLY+EditClassActivity.DATA_NAME));
//            mClass.setClassPlace(data.getStringExtra(EditClassActivity.EXTRA_REPLY+EditClassActivity.DATA_PLACE));
//            boolean flag = data.getBooleanExtra(EditClassActivity.EXTRA_REPLY+ EditClassActivity.DATA_FLAG, false);
//            mClass.setOnlineFlag(flag);
//            if(flag) mClass.setOnlineUrl(data.getStringExtra(EditClassActivity.EXTRA_REPLY+EditClassActivity.DATA_URL));

            int preAlertNum = classData.getAlertNum();
            long[] preAlertTime = new long[3];
            if (preAlertNum >= 0) System.arraycopy(classData.getAlerts(), 0, preAlertTime, 0, preAlertNum);
//      ->
//      for(int i=0;i<preAlertNum;i++){
//            preAlertTime[i] = preAlertTime[i];
//      }

            Log.d(TAG, "[get DATA] " +
                    "\nclassPos:   " + classData.getClassPos() +
                    "\nclassName:  " + classData.getClassName() +
                    "\nclassPlace: " + classData.getClassPlace() +
                    "\nOnline URL: " + classData.getOnlineUrl() +
                    "\nisOnline:   " + classData.getOnlineFlag() +
                    "\npreAlertNum:" + classData.getAlertNum() +
                    "\npreAlerts = {"+preAlertTime[0]+", "+preAlertTime[1]+", "+preAlertTime[2]+"}");
            // TODO: repositoryからinsert
            homeViewModel.insert(classData);
            toolbar.setTitle("Home");
            viewPager.setCurrentItem(FragNum.Home, false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setViewModel(HomeViewModel vm){
        homeViewModel = vm;
    }
    public void setViewModel(DashboardViewModel vm){
        dashboardViewModel = vm;
    }
    public void setViewModel(NotificationsViewModel vm){
        notificationsViewModel = vm;
    }

    public HomeViewModel getHomeViewModel() { return this.homeViewModel; }

    public HomeFragment getHomeFragment()                   { return homeFragment; }
    public DashboardFragment getDashboardFragment()         { return dashboardFragment; }
    public NotificationsFragment getNotificationsFragment() { return notificationsFragment; }

    // serviceがactiveかどうか
    // https://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    public boolean activatedServiceof(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(manager == null) return false;
        if(manager.getRunningServices(Integer.MAX_VALUE).isEmpty()) return false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_preferences, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mItem){
        switch(mItem.getItemId()){
            case R.id.optionsMenu_Pref:
                Toast.makeText(getApplicationContext(),"'設定' is selected...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                Log.d(TAG, "...onOptionsItemSelected is active");
                return true;
            case R.id.optionsMenu_help:
                Toast.makeText(getApplicationContext(),"'ヘルプ' is selected...", Toast.LENGTH_LONG).show();
                return true;
            case R.id.optionsMenu_info:
                Toast.makeText(getApplicationContext(), "'お知らせ' is selected...", Toast.LENGTH_LONG).show();
                return true;
//            case R.id.action_settings:
//                Toast.makeText(getApplicationContext(), "'action_settings' is selected...", Toast.LENGTH_LONG).show();
//                return true;
            default:
                return super.onOptionsItemSelected(mItem);
        }
    }

    // そもそもsetup自体もいらないのでは
    /*
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void setupViewPager(){
        List<Fragment> Frags = List.of(new HomeFragment(), new DashBoardFragment(), new NotificationFragment());

//        extends FragmentStatePagerAdapter
//      ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),Frags);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        //        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        viewPager.OnPageChangeCallback( new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (currentItem != null) {
                    currentItem.setChecked(false);
                } else {
                    navView.getMenu().getItem(0).setChecked(false);
                }
                navView.getMenu().getItem(position).setChecked(true);
                currentItem = navView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

     */
    // ViewPager の場合，Overrideが必須，ViewPager2の場合，NonSwipe機能がついている．
    /*
    private static class BottomNavigationViewPager extends ViewPager2 {
        BottomNavigationViewPager(Context context){
            super(context);
        }
        BottomNavigationViewPager(Context context, AttributeSet attrs){
            super(context, attrs);
        }
        @Override
        public boolean onInterceptHoverEvent(@NonNull MotionEvent event){
            return false;
        }
        @Override
        public boolean onTouchEvent(@NonNull MotionEvent event){
            return false;
        }
    }
     */

    // 各ページに保存するデータ量によって，Adapterを変える必要がある．
    private static class ViewPagerRecyclerAdapter extends RecyclerView.Adapter<ViewPagerRecyclerAdapter.ViewHolder> {

        private List<Fragment> mFrags;
        private LayoutInflater inflater;

        private static final String TAG="ViewPagerRecyclerAdapter";


//        @Override
//        public Fragment createFragment(int position){
//            //
//            return new ViewPageFragment();
//        }

        ////////////////////////////////////////

        /*
        // extends FragmentStatePagerAdapter

        public ViewPagerAdapter(@NonNull FragmentManager fm, List<Fragment> frags, int behavior) {
            super(fm, behavior);
            mFrags = frags;
        }
        public ViewPagerAdapter(@NonNull FragmentManager fm,List<Fragment> frags) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFrags.get(position);
        }

        @Override
        public int getCount() {
            return mFrags.size();
        }

         */

        // extends RecyclerView.Adapter ///////////
        public ViewPagerRecyclerAdapter (Context context){
            Log.v(TAG,"--- constructor ---");
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ViewPagerRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }


        public static class
        ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView){
                super(itemView);

            }
        }

        @Override
        public int getItemCount() {
            return 0;
        }
        //////////////////////////////////

    }


//    protected boolean onNavigationItemSelectedListener(MenuItem item){
//        int id = item.getItemId();
//        switch(id){
//
//        }
//        return true;
//    }

}