package com.mymdl.george.myymdll;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class EventDetailActivity extends AppCompatActivity {

    TextView evtClub;
    TextView evtTitle;
    TextView evtDetail;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_full);

        setTitle("Evenement");

        evtClub = (TextView) findViewById(R.id.evtClub);
        evtTitle = (TextView) findViewById(R.id.evtTitle);
        evtDetail = (TextView) findViewById(R.id.evtDetail);

        Intent intent = getIntent();
        if (intent != null) {
            event = intent.getParcelableExtra("event");
            if (event != null) {
                evtTitle.setText(event.getTitle());
                evtDetail.setText(event.getDetail());
                evtClub.setText(event.getNameClubLinked());
            }
        }
    }
}
