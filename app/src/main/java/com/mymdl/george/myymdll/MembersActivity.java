package com.mymdl.george.myymdll;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MembersActivity extends AppCompatActivity {

    // layout
    RecyclerView MembersRv;
    CardAdapterMembers cardAdapter;
    FloatingActionButton addUserFab;
    SwipeRefreshLayout sRefreshMember;

    String email;
    int ownId, levelAccess;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.membersDrawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("Gestion des membres");
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
        ownId = Integer.valueOf(intent.getStringExtra("idAuthor"));
        email = String.valueOf(intent.getStringExtra("emailUser"));

        // layout
        addUserFab = findViewById(R.id.addUserFab);
        sRefreshMember = findViewById(R.id.SRefreshMember);
        addUserFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MembersActivity.this, AddUserActivity.class);
                intent.putExtra("idAuthor", String.valueOf(ownId));
                // je sais pas à quoi ça sert, mais il passe d'email de la personne actuellement connécté
                intent.putExtra("emailUser", String.valueOf(email));
                startActivity(intent);
            }
        });
        sRefreshMember.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkIfAbleRefresh();
            }
        });

        final Drawable add = ContextCompat.getDrawable(this, R.drawable.add);

        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.addUserFab), "un bouton fantastique !", "Vous avez la possibilité d'ajouter des administrateurs fantastiques !")
                        // All options below are optional
                        .outerCircleColor(R.color.redfmdl)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.redfmdldark)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.redfmdl)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)
                        .icon(add)
                        // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                            // Specify a custom drawable to draw as the target
                        .targetRadius(60),// Specify the target radius (in dp)

                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                    }
                });


    }

    // pour le bouton de retour
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // test la connection internet et réagit par rapport à ça
    public void checkIfAbleRefresh(){
        // test si il est connecté à internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            sRefreshMember.setRefreshing(true);
            // Commence le processus de rafraichissement des cards, à chaque retour à la page
            new UserListDbCo().execute();

            // Toast.makeText(MainActivity.this, "Coco à internet", Toast.LENGTH_SHORT).show();
        } else { // n'est pas connecté à internet
            //afficher un message d'info et afficher les events pré-enregistrés
            Snackbar.make(sRefreshMember, "Vous n'êtes pas connecté à internet", Snackbar.LENGTH_LONG).show();
            sRefreshMember.setRefreshing(false);
        }
    }

    public void refreshCards(List<User> allUserList) {
        //Repérage du RecyclerView
        MembersRv = findViewById(R.id.allUserRv);

        sRefreshMember.setRefreshing(false);
        // Création CardAdapter
        cardAdapter = new CardAdapterMembers(ownId, allUserList);
        MembersRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        MembersRv.setAdapter(cardAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfAbleRefresh();
    }

    class UserListDbCo extends AsyncTask<Void, Void,List> {

        private List<User> allUserList;
        Statement stmt;
        Connection conn;

        int levelAccessControlled = 0;

        // contructeur
        public UserListDbCo() {
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(List userList) {
            refreshCards(userList);
        }

        @Override
        protected List doInBackground(Void... voids) {
            allUserList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                // prendre le levelAccess, pour n'afficher que les gens à sa porté
                ResultSet resultSet = stmt.executeQuery("SELECT levelAccess FROM accountdb WHERE idUser="+ownId);
                if (resultSet.next()) {
                    levelAccess = resultSet.getInt("levelAccess");

                    switch (levelAccess) {
                        case 2: // le levelAccess est 2, il controle tous les levelAccess = 1
                            levelAccessControlled = 1;
                            // Sur quel lycée peut-il agir ?
                            resultSet = stmt.executeQuery("SELECT idSchool FROM accountdb WHERE idUser="+ownId);
                            resultSet.next();
                            int idSchool = resultSet.getInt("idSchool");

                            resultSet = stmt.executeQuery("SELECT * FROM accountdb WHERE idSchool="+idSchool+" AND levelAccess="+levelAccessControlled+" AND idUser !="+ownId+" ORDER BY familyNameUser");
                            break;
                        case 3:
                            levelAccessControlled = 2;
                            // sur quelle région peut-il agir ?
                            resultSet = stmt.executeQuery("SELECT idRegion FROM accountdb WHERE idUser="+ownId);
                            resultSet.next();
                            int idRegion = resultSet.getInt("idRegion");

                            resultSet = stmt.executeQuery("SELECT accountdb.*, schooldb.idRegion FROM accountdb, schooldb WHERE accountdb.idUser!="+ownId+" AND accountdb.levelAccess="+levelAccessControlled+" AND schooldb.idSchool=accountdb.idSchool AND schooldb.idRegion="+idRegion+" ORDER BY accountdb.familyNameUser");
                            break;
                        case 4:
                            levelAccessControlled = 3;
                            resultSet = stmt.executeQuery("SELECT * FROM accountdb WHERE levelAccess="+levelAccessControlled+" AND idUser !="+ownId+" ORDER BY familyNameUser");
                            break;
                    }
                }
                while(resultSet.next()){
                    // prend les valeurs depuis la requête
                    int idUser  = resultSet.getInt("accountdb.idUser");
                    String name  = resultSet.getString("accountdb.nameUser");
                    String fName = resultSet.getString("accountdb.familyNameUser");
                    String email = resultSet.getString("accountdb.emailUser");
                    String password = resultSet.getString("accountdb.passwordUser");
                    int lvlaccess = resultSet.getInt("accountdb.levelAccess");
                    int idClub = resultSet.getInt("accountdb.idClub");
                    int idSchool = resultSet.getInt("accountdb.idSchool");
                    int idRegion = resultSet.getInt("accountdb.idRegion");

                    // Historique XD
                    // Event evt = new Event(idUser,detail,"Nom :" + title + ", mot de passe : " + mdp + ", niveau de sécurité :" + lvlaccess,"nothing",1);
                    User user = new User(idUser, name, fName, password, lvlaccess, email);
                    user.setIdClub(idClub);
                    user.setIdSchool(idSchool);
                    user.setIdRegion(idRegion);

                    allUserList.add(user);
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
            return allUserList;
        }
    }
}

