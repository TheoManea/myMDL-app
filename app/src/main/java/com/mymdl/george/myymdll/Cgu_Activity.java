package com.mymdl.george.myymdll;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Cgu_Activity extends AppCompatActivity {

    TextView cguTv;
    Button validCguBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cgu_);

        cguTv = findViewById(R.id.cguTv);
        validCguBtn = findViewById(R.id.validCguBtn);

        validCguBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sauvegarde l'acceptation
                SharedPreferences prefs = getSharedPreferences(MainActivity.DIDACTITIEL_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(MainActivity.FIRST_CO_KEY, false);
                editor.apply();

                finish();
            }
        });

        new CguCo().execute();

    }

    class CguCo extends AsyncTask<Void, Void, Void> {

        Statement stmt;
        Connection conn;

        String cguTxt = "";

        // contructeur
        public CguCo() {
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(Void truc) {
            cguTv.setText(cguTxt);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                // prendre le levelAccess, pour n'afficher que les gens à sa porté
                ResultSet resultSet = stmt.executeQuery("SELECT contentInfo FROM legalinfodb WHERE idInfo = 1");
                if (resultSet.next()) {
                    cguTxt = resultSet.getString("contentInfo");
                }

                resultSet.close();
                stmt.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }finally{
                try{
                    if(stmt!=null)
                        stmt.close();
                }catch(SQLException se2){
                }// nothing we can do
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
            return null;
        }
    }
}
