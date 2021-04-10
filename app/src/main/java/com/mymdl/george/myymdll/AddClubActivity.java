package com.mymdl.george.myymdll;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mymdl.george.myymdll.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddClubActivity extends AppCompatActivity {

    // variables du layout
    private EditText addClubTitleEt,addClubDetailsEt;
    private Button clubNewBackBtn, clubNewSendBtn;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    // mode
    private int showMode;
    int ADD_MODE = 0, EDIT_MODE = 1, BASIC_INFO_MODE = 2;

    // propres variables
    int idAuthor, idSchool;

    // objet actuellement modifié ou créé
    private Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.addClubDrawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("Nouveau Club");
        headTxt.setTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.redfmdl)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.redfmdldark));
        }

        // trouver les éléments du layout
        addClubTitleEt = findViewById(R.id.addClubTitleEt);
        addClubDetailsEt = findViewById(R.id.addClubDetailsEt);
        clubNewBackBtn = findViewById(R.id.clubNewBackBtn);
        clubNewSendBtn = findViewById(R.id.clubNewSendBtn);

        // bouton retour
        clubNewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // bouton sauvegarder
        clubNewSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // .replace("\'", "\\\'").replace("\"", "\\\"")

                if (showMode == ADD_MODE) {
                    new dbClub(ADD_MODE, "INSERT INTO clubdb(titleClub, detailsClub, idSchool) VALUES (?,?,?)").execute();
                } else {
                    new dbClub(EDIT_MODE, "UPDATE clubdb SET titleClub=?,detailsClub=? WHERE idClub=?").execute();
                }
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra("club") /*&& intent.hasExtra("idAuthor")*/) {
            // titre
            setTitle("Modifier un club");
            club = intent.getParcelableExtra("club");
            idSchool = club.getIdSchool();
            //idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));
            showMode = EDIT_MODE;

            addClubTitleEt.setText(club.getTitleClub());
            addClubDetailsEt.setText(club.getDetailsClub());
        } else if (intent.hasExtra("idAuthor")){
            // titre
            setTitle("Ajouter un club");
            idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));
            showMode = ADD_MODE;

            new dbClub(BASIC_INFO_MODE, "SELECT idSchool FROM accountdb WHERE idUser="+idAuthor).execute();
        }
    }

    public class dbClub extends AsyncTask<Void, Void, Void>
    {
        // variables de connexion
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement prep = null;

        // quelle mode adopté ?
        private int modeQuery;
        private String query;

        //contructeur 1
        public dbClub(int modeQuery, String query) {
            this.modeQuery = modeQuery;
            this.query = query;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(Void nothing) {
            if (modeQuery == ADD_MODE) {
                Toast.makeText(AddClubActivity.this, "Nouveau club crée !", Toast.LENGTH_LONG).show();
                finish();
            }else if (modeQuery == EDIT_MODE) {
                Toast.makeText(AddClubActivity.this, "club modifié !", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            List clubList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);

                if (modeQuery == BASIC_INFO_MODE) {
                    stmt = conn.createStatement();
                    ResultSet resultSet = stmt.executeQuery(query);
                    if(resultSet.next()) {
                        idSchool = resultSet.getInt("idSchool");
                    }
                    resultSet.close();

                    stmt.close();
                } else {
                    prep = conn.prepareStatement(query);
                    prep.setString(1, addClubTitleEt.getText().toString());
                    prep.setString(2, addClubDetailsEt.getText().toString());
                    if (modeQuery == ADD_MODE)
                        prep.setInt(3, idSchool);
                    else if (modeQuery == EDIT_MODE)
                        prep.setInt(3, club.getIdClub());

                    prep.executeUpdate();
                    prep.close();
                }

                conn.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) { }
    }
}
