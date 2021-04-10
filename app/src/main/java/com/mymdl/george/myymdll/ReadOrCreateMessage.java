package com.mymdl.george.myymdll;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.mymdl.george.myymdll.R;

public class ReadOrCreateMessage extends AppCompatActivity {

    private Button send;
    int id;
    String name;
    RecyclerView messageRv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_or_create_message);


        //get extras
        Intent intent = getIntent();
        id = Integer.valueOf(intent.getStringExtra("id"));
        name = String.valueOf(intent.getStringExtra("name"));

        send = findViewById(R.id.create);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(ReadOrCreateMessage.this, SendMessage.class);
                send.putExtra("id", String.valueOf(id));
                send.putExtra("name", String.valueOf(name));
                startActivity(send);
            }
        });


    }

    // fonction de génération des cards pour les events
    /*public void refreshCards(List<Message> allMsgList) {

        //Repérage du RecyclerView
        messageRv = findViewById(R.id.);

        // Création CardAdapter
        CardAdapterMessage cardAdapter = new CardAdapterMessage(allEvtList);
        //SimpleAdapter simpleAdapter = new SimpleAdapter(this, new String[] {"le 1","le 2", "le 3","le 4"});
        messageRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        messageRv.setAdapter(cardAdapter);
    }*/
}
