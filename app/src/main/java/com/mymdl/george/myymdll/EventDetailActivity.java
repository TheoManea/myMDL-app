package com.mymdl.george.myymdll;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mymdl.george.myymdll.R;

public class EventDetailActivity extends AppCompatActivity {

    Event event;
    TextView titleEvtTv;
    TextView toDateEvtTv;
    TextView clubEvtTv;
    TextView detailsEvtTv;
    TextView postedDateEvtTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        titleEvtTv = findViewById(R.id.titleEvtTv);
        toDateEvtTv = findViewById(R.id.toDateEvtTv);
        clubEvtTv = findViewById(R.id.clubEvtTv);
        detailsEvtTv = findViewById(R.id.detailsEvtTv);
        postedDateEvtTv = findViewById(R.id.postedDateEvtTv);

        Context context = getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            event = intent.getParcelableExtra("event");
            if (event != null) {
                setTitle(event.getTitle());

                titleEvtTv.setText(event.getTitle());
                String dateStr = "Prévu le "+event.getStringDayOfEvent();
                if (!event.getStringDayOfEvent().equals(event.getStringDayEndOfEvent())) {
                    dateStr = dateStr + " jusqu'au " + event.getStringDayEndOfEvent();
                }
                toDateEvtTv.setText(dateStr);
                clubEvtTv.setText("par "+event.getTitleClubLinked());
                detailsEvtTv.setText(event.getDetail());
                postedDateEvtTv.setText("Posté le " + event.getStringDayOfCreation());
            }
        }
    }


    /*private void showFABMenu(){
        isFABOpen=true;

        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_55));

    }

    private void closeFABMenu(){
        isFABOpen=false;

        fab2.animate().translationY(0);

    }*/

}
