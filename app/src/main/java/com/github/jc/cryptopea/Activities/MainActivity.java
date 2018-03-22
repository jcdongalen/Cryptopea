package com.github.jc.cryptopea.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jc.cryptopea.BuildConfig;
import com.github.jc.cryptopea.Fragments.AboutUs;
import com.github.jc.cryptopea.Fragments.Dashboard;
import com.github.jc.cryptopea.Fragments.Help;
import com.github.jc.cryptopea.Fragments.MyProfile;
import com.github.jc.cryptopea.Fragments.Report;
import com.github.jc.cryptopea.Fragments.Reward;
import com.github.jc.cryptopea.Models.ProfileDetails;
import com.github.jc.cryptopea.R;
import com.github.jc.cryptopea.Utils.Constants;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, View.OnClickListener {

    private static final int ITEM_DASHBOARD = 0,
            ITEM_PROFILE = 1,
            ITEM_REPORT = 2,
            ITEM_HELP = 3,
            ITEM_ABOUT_US = 4;

    private Constants mConstants;
    private ProfileDetails profileDetails;

    //Android Navigation
    private DrawerLayout nav_drawer;
    private ActionBarDrawerToggle nav_toggle;
    private Toolbar toolbar;
    private FragmentManager fragManager;
    private FragmentTransaction fragTransaction;
    private NavigationView nav_view;
    private int nav_item_index = 1000, nav_current_item = 1000;
    private ImageView imgProfilePic;
    private TextView tvName, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConstants = new Constants(this);
        profileDetails = ProfileDetails.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Material navigation area
        nav_drawer = findViewById(R.id.nav_drawer);
        nav_drawer.addDrawerListener(this);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        nav_view.setItemTextColor(ColorStateList.valueOf(Color.BLACK));
        nav_view.setItemIconTintList(null);
        nav_toggle = new ActionBarDrawerToggle(this, nav_drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        nav_toggle.syncState();

        imgProfilePic = nav_view.getHeaderView(0).findViewById(R.id.imgProfilePic);
        imgProfilePic.setOnClickListener(this);
        mConstants.GlideImageLoader(profileDetails.getPhoto_url(), imgProfilePic);

        tvName = nav_view.getHeaderView(0).findViewById(R.id.tvName);
        tvName.setText(profileDetails.getDisplay_name());
        tvEmail = nav_view.getHeaderView(0).findViewById(R.id.tvEmail);
        tvEmail.setText(profileDetails.getEmail());

        Dashboard dashboard = new Dashboard();
        addFragment(dashboard, true);
    }

    public void addFragment(Fragment frag, boolean isAddtoBackStack) {
        fragManager = getSupportFragmentManager();
        String FRAGMENT_TAG = frag.getClass().getSimpleName().toUpperCase();
        fragTransaction = fragManager.beginTransaction();
        fragTransaction.setCustomAnimations(R.anim.anim_enter_right, R.anim.anim_exit_left);
        fragTransaction.replace(R.id.frmContent, frag, FRAGMENT_TAG);
        if (isAddtoBackStack) {
            fragTransaction.addToBackStack(FRAGMENT_TAG);
        } else {
            fragTransaction.addToBackStack(null);
        }
        fragTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                nav_item_index = ITEM_DASHBOARD;
                break;
            case R.id.nav_my_profile:
                imgProfilePic.performClick();
                return false;
            case R.id.nav_report:
                nav_item_index = ITEM_REPORT;
                break;
            case R.id.nav_mail_us:
                nav_item_index = ITEM_HELP;
                break;
            case R.id.nav_about_us:
                nav_item_index = ITEM_ABOUT_US;
                break;
            case R.id.nav_sign_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }

        nav_drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (nav_drawer.isDrawerOpen(GravityCompat.START)) {
            nav_drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
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
            case ITEM_DASHBOARD:
                nav_current_item = ITEM_DASHBOARD;
                addFragment(new Dashboard(), true);
                break;
            case ITEM_PROFILE:
                break;
            case ITEM_REPORT:
                nav_current_item = ITEM_REPORT;
                addFragment(new Report(), true);
                break;
            case ITEM_HELP:
                nav_current_item = ITEM_HELP;
                addFragment(new Help(), true);
                break;
            case ITEM_ABOUT_US:
                nav_current_item = ITEM_ABOUT_US;
                addFragment(new AboutUs(), true);
                break;
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgProfilePic:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this, imgProfilePic,
                            ViewCompat.getTransitionName(imgProfilePic)).toBundle();
                    Intent intent = new Intent(this, Profile.class);
                    startActivity(intent, bundle);
                } else {
                    startActivity(new Intent(this, Profile.class));
                }
                break;
        }
    }
}
