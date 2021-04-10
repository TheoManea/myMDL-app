package com.mymdl.george.myymdll;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mymdl.george.myymdll.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class StatsActivity extends AppCompatActivity {

    /*private static final String[][] DATA_TO_SHOWW = { { "This", "is", "a", "test" },
            { "and", "a", "second", "test" } };*/

    private static final String[] TABLE_HEADERS = { "NameUser", "NbEvent", "Club", "Prozent" };

    private int idSchool, idAuthor;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private PieChart pieChart;
    ArrayList<Integer> colors;
    private Spinner spinner;

    // données des stats
    // stats des personnes du lycées (NUM1)
    ArrayList<String> nameUsers; // nom user
    ArrayList<String> nameClubOfUsers; // club associé au user du même index dans nameUser
    ArrayList<Integer> nbPostUsers; // nombre de postes d'un user, même index que nameUser
    // stats des clubs du lycée (NUM2)
    ArrayList<String> nameClub; // Nom de chaque club
    ArrayList<Integer> nbPostClub; // nb post par club

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.drawerStats);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("Stats");
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
            idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));
        } else
            finish();

        // spinner
        Spinner spinner = findViewById(R.id.kindStatSpinn);
        String[] kindStat = new String[] {"Par éleves", "Par clubs"};
        ArrayAdapter spinnerAdapter = new ArrayAdapter(StatsActivity.this, android.R.layout.simple_dropdown_item_1line, kindStat);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(StatsActivity.this, kindStat[i], Toast.LENGTH_LONG).show();
                switch (i) {
                    case 0:
                        populatePieChart("Groupé par éleves", nbPostUsers, nameUsers);
                        break;
                    case 1:
                        populatePieChart("Groupé par clubs", nbPostClub, nameClub);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*TableView<String[]> tableView = (TableView<String[]>) findViewById(R.id.tableView);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));*/

        // generation partielle du pie chart
        //https://www.android-examples.com/pie-chart-graph-android-app-using-mpandroidchart/
        //add colors to dataset
        colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.yellowPie));
        colors.add(getResources().getColor(R.color.lightOrangePie));
        colors.add(getResources().getColor(R.color.darkOrangePie));
        colors.add(getResources().getColor(R.color.redPie));
        colors.add(getResources().getColor(R.color.darkRedPie));
        colors.add(getResources().getColor(R.color.pinkRedPie));
        colors.add(getResources().getColor(R.color.darkPinkPie));
        colors.add(getResources().getColor(R.color.pinkPie));
        colors.add(getResources().getColor(R.color.lightPurplePie));
        colors.add(getResources().getColor(R.color.lightBluePie));
        colors.add(getResources().getColor(R.color.purplePie));
        colors.add(getResources().getColor(R.color.darkPurplePie));
        colors.add(getResources().getColor(R.color.darkBluePie));
        colors.add(getResources().getColor(R.color.bluePie));
        colors.add(getResources().getColor(R.color.darkGreenPie));
        colors.add(getResources().getColor(R.color.lightWhiteGreenPie));
        colors.add(getResources().getColor(R.color.softGreenPie));
        colors.add(getResources().getColor(R.color.lightGreenPie));
        colors.add(getResources().getColor(R.color.whiteGreenPie));
        colors.add(getResources().getColor(R.color.darkBlackGreenPie));

        pieChart = findViewById(R.id.statPC);
        pieChart.setRotationEnabled(false);
        pieChart.setUsePercentValues(false);
        pieChart.setTransparentCircleColor(Color.BLACK);
        pieChart.setCenterTextColor(R.color.white);
        pieChart.setCenterText("Posts");
        pieChart.setCenterTextSize(14);
        pieChart.setHoleColor(getResources().getColor(R.color.flatblack));
        pieChart.setCenterTextColor(getResources().getColor(R.color.white));
        pieChart.setEntryLabelColor(getResources().getColor(R.color.flatblack));

        new Bolsonaro().execute();

    }

    // pour le bouton de retour
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public class Bolsonaro extends AsyncTask<Void, Void, Void> {

        // variables de connexion
        Connection conn = null;
        Statement stmt = null, stmt1 = null, stmt2 = null;

        private  String nom, nomClub;
        private int nb_club,clubmec,total, levelaccess;
        private float prozent;


        @Override
        protected void onPreExecute() {
            nameUsers = new ArrayList<String>();
            nameClubOfUsers = new ArrayList<String>();
            nbPostUsers = new ArrayList<Integer>();

            nameClub = new ArrayList<String>();
            nbPostClub = new ArrayList<Integer>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            /*TableView<String[]> tableView = (TableView<String[]>) findViewById(R.id.tableView);

            TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
            columnModel.setColumnWeight(1, 2);
            columnModel.setColumnWeight(2, 2);
            tableView.setColumnModel(columnModel);

            tableView.setDataAdapter(new SimpleTableDataAdapter(StatsActivity.this, DATA_TO_SHOW ));*/

            /////////////////////////////////////////////////
            populatePieChart("", nbPostUsers, nameUsers);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();
                stmt1 = conn.createStatement();
                stmt2 = conn.createStatement();


                /////////// NUM1
                ResultSet resultSet0 = stmt.executeQuery("SELECT idSchool FROM accountdb WHERE idUser="+idAuthor);
                if (resultSet0.next()) {
                    idSchool = resultSet0.getInt("idSchool");
                }

                //Requête pour le nombre total d'event dans le lycée (pas les sondages)
                resultSet0 = stmt.executeQuery("SELECT COUNT(*) AS nb_events_lycee FROM eventsdb WHERE question IS NULL and idSchool=" + idSchool);
                if (resultSet0.next()) {
                    total = resultSet0.getInt("nb_events_lycee");
                }

                //Requête pour récupérer les noms
                ResultSet resultSet1 = stmt.executeQuery("SELECT idUser,nameUser,idClub,levelAccess FROM accountdb WHERE idSchool=" + idSchool );
                while(resultSet1.next()){
                    ResultSet resultSet2 = stmt1.executeQuery("SELECT COUNT(*) AS nb_events_user FROM eventsdb WHERE idAuthor=" + resultSet1.getInt("idUser"));
                    resultSet2.next();


                    nom = resultSet1.getString("nameUser");
                    nb_club = resultSet2.getInt("nb_events_user");
                    clubmec = resultSet1.getInt("idClub");
                    levelaccess =  resultSet1.getInt("levelAccess");
                    ResultSet resultSet3 = stmt2.executeQuery("SELECT titleClub FROM clubdb WHERE idClub="+clubmec);
                    resultSet3.next();
                    if(levelaccess == 1)
                    {
                        nomClub = resultSet3.getString("titleClub");
                    }
                    else
                    {
                        nomClub = "";
                    }


                    prozent = (float) nb_club/total * 100 ;
                    System.out.print("\n Nb club : " + nb_club);
                    System.out.print("\n total :" +total);
                    System.out.print("\n prozent " +prozent);


                    //DATA_TO_SHOW.add(new String[] {nom, String.valueOf(nb_club), nomClub, String.valueOf(prozent) + "%"});
                    // nom user, nom du club associé au user, nombre de post du user
                    nameUsers.add(nom);
                    nameClubOfUsers.add(nomClub);
                    nbPostUsers.add(nb_club);

                }
                /////// NUM2
                resultSet0 = stmt.executeQuery("SELECT clubdb.titleClub, Count(idEvent) AS nbClubEvt FROM `eventsdb` JOIN clubdb ON clubdb.idClub = eventsdb.idClub WHERE eventsdb.idSchool = "+idSchool+" GROUP BY eventsdb.idClub");
                while (resultSet0.next()) {
                    nameClub.add(resultSet0.getString("titleClub"));
                    nbPostClub.add(resultSet0.getInt("nbClubEvt"));
                }



                stmt.close();
                conn.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // /!\ data.length = labels.length
    public void populatePieChart (String desciption,ArrayList<Integer> data, ArrayList<String> labels ) {
        Description desc = new Description();
        desc.setTextColor(Color.WHITE);
        // champ de description
        desc.setText(desciption);
        desc.setTextSize(12);
        pieChart.setDescription(desc);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<LegendEntry> xEntrys = new ArrayList<>();

        for(int i = 0; i < data.size(); i++){
            if (data.get(i) != 0) {
                yEntrys.add(new PieEntry(data.get(i) , labels.get(i)));
            }
        }

        /*for(int i = 1; i < xData.length; i++){
            xEntrys.add(new LegendEntry(xData[i], Legend.LegendForm.CIRCLE,10f,2f,null,colors.get(i)));
        }

        //create the data set
        legend.setCustom(xEntrys);
        legend.setTextColor(Color.WHITE);*/


        //pieDataSet.setSliceSpace(2);
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColors(colors);
        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

}
