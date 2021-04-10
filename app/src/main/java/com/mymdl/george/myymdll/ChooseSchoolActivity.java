package com.mymdl.george.myymdll;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.mymdl.george.myymdll.R;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChooseSchoolActivity extends AppCompatActivity  {

    private SearchableSpinner spinner; //https://github.com/miteshpithadiya/SearchableSpinner
    private Button button;

    private List<School> theSchoolList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_school);

        theSchoolList = new ArrayList<>();

        spinner = findViewById(R.id.SpinnerClub);
        button = findViewById(R.id.button);




        new MargaretThatcher().execute();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinner != null && spinner.getSelectedItem() !=null) {
                    // enregistre l'id du lycée séléctionné

                    SharedPreferences preferences = getSharedPreferences(MainActivity.SCHOOL_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor edit = preferences.edit();

                    edit.putInt(MainActivity.SCHOOL_ID_KEY, theSchoolList.get(spinner.getSelectedItemPosition()).getIdSchool());
                    edit.putString(MainActivity.SCHOOL_NAME_KEY, theSchoolList.get(spinner.getSelectedItemPosition()).getNameSchool());
                    edit.apply();

                    finish();
                }
            }
        });

    }


    public class MargaretThatcher extends AsyncTask<Void, Void, List<School>>{

        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        @Override
        protected void onPostExecute(List<School> schoolList) {
                int positionFormerEvtClub = 0;
                List<String> nameSchoolList = new ArrayList<>();
                for (School schooltmp : schoolList) {
                    nameSchoolList.add(schooltmp.getNameSchool());
                }

                theSchoolList = schoolList;

                // initialiser le spinner
                ArrayAdapter adapter = new ArrayAdapter(ChooseSchoolActivity.this, android.R.layout.simple_dropdown_item_1line, nameSchoolList);
                spinner.setAdapter(adapter);
                spinner.setSelection(positionFormerEvtClub);
                spinner.setTitle("Selectioner le lycée associé");
                spinner.setPositiveButton("OK");
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }


        @Override
        protected List<School> doInBackground(Void... params) {
            List schoolList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT * FROM schooldb");
                while (resultSet.next()) {
                    int idSchool = resultSet.getInt("idSchool");
                    String nameSchool = resultSet.getString("nameSchool");
                    String toolBarColor = resultSet.getString("nb_connection");
                    String textColor = resultSet.getString("textColor");

                    schoolList.add(new School(idSchool, nameSchool, toolBarColor, textColor));
                }
                resultSet.close();

                stmt.close();
                conn.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return schoolList;
        }

        @Override
        protected void onPreExecute() {
        }
    }


    private class School{
        int idSchool;
        String nameSchool;
        String toolBarColor;
        String textColor;

        public School(int idSchool, String nameSchool, String toolBarColor, String textColor) {
            this.idSchool = idSchool;
            this.nameSchool = nameSchool;
            this.toolBarColor = toolBarColor;
            this.textColor = textColor;

        }

        public int getIdSchool() {
            return idSchool;
        }

        public void setIdSchool(int idSchool) {
            this.idSchool = idSchool;
        }

        public String getNameSchool() {
            return nameSchool;
        }

        public void setNameSchool(String nameSchool) {
            this.nameSchool = nameSchool;
        }

        public String getToolBarColor() {
            return toolBarColor;
        }

        public String getTextColor() {
            return textColor;
        }
    }

}
