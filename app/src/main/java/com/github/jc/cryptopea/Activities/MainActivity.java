package com.github.jc.cryptopea.Activities;

import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.github.jc.cryptopea.Fragments.Dashboard;
import com.github.jc.cryptopea.Fragments.MyProfile;
import com.github.jc.cryptopea.Fragments.Reward;
import com.github.jc.cryptopea.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    //Android Navigation
    private DrawerLayout nav_drawer;
    private ActionBarDrawerToggle nav_toggle;
    private Toolbar toolbar;
    private FragmentManager fragManager;
    private FragmentTransaction fragTransaction;
    private NavigationView nav_view;
    private int nav_item_index = 1000, nav_current_item = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
//        initializeDrawer();

        //Material navigation area
        nav_drawer = findViewById(R.id.nav_drawer);
        nav_drawer.addDrawerListener(this);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        Fragment reward = new Reward();
        addFragment(reward);
    }

    public void addFragment(Fragment frag) {
        fragManager = getSupportFragmentManager();
        fragTransaction = fragManager.beginTransaction();
        fragTransaction.replace(R.id.frmContent, frag, frag.getClass().getSimpleName());
        fragTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragTransaction.addToBackStack(frag.getClass().getSimpleName());
        fragTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                nav_drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                nav_item_index = 0;
                break;
            case R.id.nav_my_profile:
                nav_item_index = 1;
                break;
            case R.id.nav_report:
                nav_item_index = 2;
                break;
            case R.id.nav_mail_us:
                nav_item_index = 2;
                break;
            case R.id.nav_about_us:
                nav_item_index = 3;
                break;
        }

        nav_drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        if (nav_item_index == 1000 || nav_current_item == nav_item_index)
            return;

        switch (nav_item_index) {
            case 0:
                nav_current_item = 0;
                addFragment(new Dashboard());
                break;
            case 1:
                nav_current_item = 1;
                addFragment(new MyProfile());
                break;
            case 2:
                nav_current_item = 2;
                addFragment(new Reward());
                break;
            case 3:
                nav_current_item = 3;
                addFragment(new MyProfile());
                break;
            case 4:
                nav_current_item = 4;
                addFragment(new MyProfile());
                break;
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
