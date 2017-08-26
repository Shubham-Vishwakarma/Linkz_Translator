package com.example.admin.translator.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.admin.translator.R;
import com.example.admin.translator.fragments.SpeechToSpeechFragment;
import com.example.admin.translator.fragments.SpeechToTextFragment;
import com.example.admin.translator.fragments.TextToSpeechFragment;
import com.example.admin.translator.fragments.TextToTextFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setTitle(R.string.speech_to_speech);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        SpeechToSpeechFragment speechToSpeechFragment = new SpeechToSpeechFragment();
        fragmentTransaction.replace(R.id.fragmentContainer,speechToSpeechFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (item.getItemId())
        {
            case R.id.nav_speech_to_speech:
                SpeechToSpeechFragment speechToSpeechFragment = new SpeechToSpeechFragment();
                fragmentTransaction.replace(R.id.fragmentContainer,speechToSpeechFragment);
                break;
            case R.id.nav_speech_to_text:
                SpeechToTextFragment speechToTextFragment = new SpeechToTextFragment();
                fragmentTransaction.replace(R.id.fragmentContainer,speechToTextFragment);
                break;
            case R.id.nav_text_to_text:
                TextToTextFragment textToTextFragment = new TextToTextFragment();
                fragmentTransaction.replace(R.id.fragmentContainer,textToTextFragment);
                break;
            case R.id.nav_text_to_speech:
                TextToSpeechFragment textToSpeechFragment = new TextToSpeechFragment();
                fragmentTransaction.replace(R.id.fragmentContainer,textToSpeechFragment);
                break;
        }

        fragmentTransaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}