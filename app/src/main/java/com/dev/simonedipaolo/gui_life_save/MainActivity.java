package com.dev.simonedipaolo.gui_life_save;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

/**
 * Created by Simone Di Paolo on 05/07/2020.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
/*
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout.addDrawerListener(toggle);
*/
        initDrawer();
        init();
    }

    /**
     * initDrawer gestisce tutto il bordello per il drawerLayout, la toolbar e compagnia bell
     */
    private void initDrawer() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationDrawer);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);

    }

    /**
     * Init gestisce il navController per la navigation
     */
    private void init() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        FragmentTransaction ft;

        switch (id) {
            case R.id.home_page:
            /*    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, new FirstFragment())
                        .commit();
            */

            if (isValidDestination(R.id.firstFragment)) {
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build();

                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.firstFragment, null, navOptions);
            } else
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.settings_page:
                /*getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, new SettingsFragment())
                        .commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                */
                if (isValidDestination(R.id.settingsFragment)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.settingsFragment);

                }
                break;
        }
        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Controlla che il fragment di destinazione sia "corretto" (ovvero, che non sia lo stesso in cui
     * si è attualmente). Se sì, lo aggiunge al backstack altrimenti no.
     * @param destination
     * @return
     */
    private boolean isValidDestination(int destination) {
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment)
                .getCurrentDestination().getId();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {

            case R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                else
                    return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}