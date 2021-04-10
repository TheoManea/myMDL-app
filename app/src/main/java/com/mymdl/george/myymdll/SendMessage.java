package com.mymdl.george.myymdll;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.mymdl.george.myymdll.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SendMessage extends AppCompatActivity {


    String id;
    String name;
    EditText message;
    CheckBox admin_region, admin_school;
    Button submit;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        View view;

        //get extras
        Intent intent = getIntent();
        id = String.valueOf(intent.getStringExtra("id"));
        name = String.valueOf(intent.getStringExtra("name"));

        //find elements
        message = findViewById(R.id.msg);
        admin_region = findViewById(R.id.toregionadmin);
        admin_school = findViewById(R.id.schooladmin);
        submit = findViewById(R.id.buttonmsg);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //get text and check if rb were checked
                content = message.getText().toString();


                if(admin_region.isChecked() == false && admin_school.isChecked() == false  ){
                    Snackbar.make(view, "Merci bien de cocher au moins une des options ", Snackbar.LENGTH_LONG).show();




                }
                else{

                    if(content.equals("")){
                        Snackbar.make(view, "Merci de mettre un message (c'est le but) ", Snackbar.LENGTH_LONG).show();

                    }
                    else
                    {
                        new BorisJohnson().execute();
                    }
                }

            }
        });








    }



    public class  BorisJohnson extends AsyncTask<Void,Void,Void>{

        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        //1 ou 0 ?
        int binary1 = 1;
        int binary2 = 1;





        @Override
        protected void onPostExecute(Void aVoid) {

            finish();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();



                if(admin_region.isChecked() == false){
                    binary1 = 0;
                }
                if(admin_school.isChecked() == false){
                    binary2 = 0;
                }


                stmt.executeUpdate("INSERT INTO message (idAuthor,name,message,to_3,to_2) VALUES (" + id + ",'" + name + "','" + content + "'," + binary1 + "," + binary2 + ")"  );


                stmt.close();
                conn.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
