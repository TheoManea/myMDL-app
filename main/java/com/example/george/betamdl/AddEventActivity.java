package com.mymdl.george.myymdll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;

public class AddEventActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button bouton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        setTitle("Ajouter un énènement");

        
    }
}
