package com.mymdl.george.myymdll;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mymdl.george.myymdll.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManageClubActivity extends AppCompatActivity {

    int ownId;
    //loader
    SwipeRefreshLayout swipeRefreshLayout;

    // layout
    RecyclerView allClubRv;
    CardAdapterClub cardAdapter;
    private ProgressBar progressBar;
    private FloatingActionButton addClubFab;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_club);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.drawerClubs);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("Gestion des clubs");
        headTxt.setTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.redfmdl)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arr);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.redfmdldark));
        }

        Intent intent = getIntent();
        if (intent.hasExtra("idAuthor")) {
            ownId = Integer.valueOf(intent.getStringExtra("idAuthor"));
        }

        // layout
        swipeRefreshLayout = findViewById(R.id.SRefreshClubs);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ClubListDbCo().execute();
            }
        });
        addClubFab = findViewById(R.id.addClubFab);
        addClubFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageClubActivity.this, AddClubActivity.class);
                intent.putExtra("idAuthor", String.valueOf(ownId));
                startActivity(intent);
            }
        });
    }

    public void refreshCards(List<Club> allClubList) {
        //Repérage du RecyclerView
        allClubRv = findViewById(R.id.allClubsRv);

        // Création CardAdapter
        cardAdapter = new CardAdapterClub(allClubList);
        allClubRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        allClubRv.setAdapter(cardAdapter);
    }

    // pour le bouton de retour
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ClubListDbCo().execute();
    }

    class ClubListDbCo extends AsyncTask<Void, Void,List> {

        private List<Club> allClubList;
        Connection conn;
        Statement stmt;

        // contructeur
        public ClubListDbCo() {
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(List clubList) {
            swipeRefreshLayout.setRefreshing(false);
            refreshCards(clubList);
        }

        @Override
        protected List doInBackground(Void... voids) {
            allClubList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT idSchool FROM accountdb WHERE idUser="+ownId);
                int idSchool = 0;
                if(resultSet.next()) {
                    idSchool = resultSet.getInt("idSchool");
                }

                resultSet = stmt.executeQuery("SELECT * FROM clubdb WHERE idSchool="+idSchool+" ORDER BY titleClub ASC");

                while(resultSet.next()){
                    // prend les données
                    int id = resultSet.getInt("idClub");
                    String title = resultSet.getString("titleClub");
                    String details = resultSet.getString("detailsClub");

                    Club club = new Club(id, title, details);
                    club.setIdSchool(idSchool);
                    allClubList.add(club);
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
            return allClubList;
        }
    }

}
