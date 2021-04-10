package com.mymdl.george.myymdll;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.mymdl.george.myymdll.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManageHighSchoolActivity extends AppCompatActivity {

    // éléments du layout
    ProgressBar progressBarSchool;
    RecyclerView allSchoolRv;
    FloatingActionButton addSchoolFab;

    // variables user
    private int idAuthor = 0, levelAccess, idRegion;

    // mode de la base de données
    int BASIC_INFO_MODE = 0, SHOW_MODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_high_school);

        progressBarSchool = findViewById(R.id.progressBarSchool);
        allSchoolRv = findViewById(R.id.allSchoolRv);
        addSchoolFab = findViewById(R.id.allSchoolRv);
        addSchoolFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // boite de dialogue pour entrer juste un nom ?
                // il faut pour les lvl 4 entrer egalement une region
                // possibilité d'y assigner directement un directeur ??? ->déjà créé
                //                                                     -> créér le lycée, puis lancer l'AddMemberActivity, avec un paramètre
                //                                                          pour directement lui assigner un lycée ==> possible qu'en lvl3 !!
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("idAuthor")) {
            idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));
        }

        // cherche le levelAccess de l'author, et si lvl 3 seulement, chercher sa region
        new SchoolListDbCo(BASIC_INFO_MODE).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new SchoolListDbCo(SHOW_MODE).execute();
    }

    public void refreshCards(List<Club> allClubList) {

        progressBarSchool.setVisibility(View.GONE);

        // Création CardAdapter
        /*cardAdapter = new CardAdapterClub(allClubList);
        allClubRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        allClubRv.setAdapter(cardAdapter);*/
    }

    class SchoolListDbCo extends AsyncTask<Void, Void, List> {

        private List<School> allSchoolList;
        Connection conn;
        Statement stmt;

        private int modeQuery;

        public SchoolListDbCo(int modeQuery) {
            this.modeQuery = modeQuery;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(List schoolList) {
            if (modeQuery == SHOW_MODE) {
                refreshCards(schoolList);
            }
        }

        @Override
        protected List doInBackground(Void... voids) {
            allSchoolList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                if (modeQuery == BASIC_INFO_MODE) {
                    ResultSet resultSet = stmt.executeQuery("SELECT levelAccess, idRegion FROM accountdb WHERE idUser="+idAuthor);
                    if (resultSet.next()) {
                        levelAccess = resultSet.getInt("levelAccess");
                        if(levelAccess == 3) {
                            idRegion = resultSet.getInt("idRegion");
                        }
                    }
                    resultSet.close();

                } else if (modeQuery == SHOW_MODE) {
                    String sql = "SELECT idSchool, nameSchool FROM schooldb";
                    if (levelAccess == 3) {
                        sql += " WHERE idRegion="+idRegion;
                    }
                    ResultSet resultSet = stmt.executeQuery(sql);

                    while (resultSet.next()) {
                        int idSchool = resultSet.getInt("idSchool");
                        String nameSchool = resultSet.getString("nameSchool");

                        allSchoolList.add(new School(idSchool, nameSchool));
                    }
                    resultSet.close();

                }

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
            return allSchoolList;
        }
    }
}
