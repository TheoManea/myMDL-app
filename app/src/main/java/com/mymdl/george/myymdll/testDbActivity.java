package com.mymdl.george.myymdll;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.os.AsyncTask;

import com.mymdl.george.myymdll.R;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class testDbActivity extends AppCompatActivity {

    private EditText editText;
    Date date;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db);

        editText = (EditText) findViewById(R.id.editTextLogDb);

        setTitle("TestDb");

        new testDbClss().execute();
    }

    public class testDbClss extends AsyncTask<String,String,String>
    {
        String toastConnection;

        Connection conn = null;
        Statement stmt = null;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String r) {
            editText.append("\nDébut des tests ! ---");

            // on recupère la donnée depuis un String
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("yyyy-MM-dd HH:mm:ss");
            String theDateTaken = format.format(date);

            // on converti le String en objet Date
            Date dateTest = null;
            try {
                dateTest = format.parse(format.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // on affiche Date en donnée en String
            editText.append("\nIs it the same :"+format.format(dateTest));
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                editText.append("\nConnecting to database...");
                Connection conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                editText.append("\nConnected! \nCreate Statement...");
                stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("select dayOfEvent from eventsdb");
                while(rs.next()){
                    date  = rs.getTimestamp("dayOfEvent");
                    SimpleDateFormat format = new SimpleDateFormat();
                    format.applyPattern("yyyy-MM-dd HH:mm:ss");
                    editText.append("\nLa date : " + format.format(date));
                }

                rs.close();

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
            return toastConnection;
        }
    }
}