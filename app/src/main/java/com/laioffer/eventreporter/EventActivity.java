package com.laioffer.eventreporter;
import android.support.v4.app.Fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.Fragment;

public class EventActivity extends AppCompatActivity {
    ReportEventFragment reportFragment;
    String username;
    TextView usernameTextView;
    private ShowEventFragment showEventsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * This activity has three part views
         * 1. TextView: welcome ...
         * 2. Fragment container
         * 3. Navigation button
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        //convert from MainActivity
        Intent intent = getIntent();
        //set welcome tag
        username = intent.getStringExtra("Username");
        usernameTextView = (TextView) findViewById(R.id.text_user);
        usernameTextView.setText("Welcome, " + username);
        // Create ReportEventFragment
        if (reportFragment == null) {
            reportFragment = new ReportEventFragment();
        }

        // Add ReportEventFragment to this activity
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ReportEventFragment()).commit();
        // the report and event navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        // Set Item click listener to the menu items
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_profile:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, reportFragment).commit();
                                break;
                            case R.id.action_events:
                                // user may convert between two fragments not only once,
                                // so you need to check if the user has create the fragment
                                if (showEventsFragment == null) {
                                    showEventsFragment = new ShowEventFragment();
                                }

                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, showEventsFragment).commit();
                                break;

                        }
                        return false;
                    }
                });
    }


    // Username used by fragment
    public String getUsername() {
        return username;
    }
}

