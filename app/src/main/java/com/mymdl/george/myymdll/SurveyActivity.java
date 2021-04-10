package com.mymdl.george.myymdll;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SurveyActivity extends AppCompatActivity {

    EditText title, question, answer1, answer2;
    Button confirm, backBtn, date1Btn, date2Btn;
    Switch multiDaysSwitch;
    LinearLayout dateTimeEndLayout;
    int idAuthor, idSchool;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    // dates
    // Object du nouvel évènement
    private Event newEvent, formerEvent;
    // objet pour le bon fonctionnement des methodes de dialog ,entre elles (objet de transition)
    private InterDialog interDialog;

    // Variables pour afficher les PickerDialogue
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    // Variables des dates
    //aujourd'hui
    Date today;
    String dayNow, monthNow, yearNow, hourNow, minuteNow;
    private static int FIRST_DATE_ID = 0, LAST_DATE_ID = 1;
    SimpleDateFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.survNewMainLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("Nouveau Sondage");
        headTxt.setTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.redfmdl)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.redfmdldark));
        }

        Intent intent = getIntent();
        idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));


        title = findViewById(R.id.survNewTitle);
        question = findViewById(R.id.survQuestion);
        answer1 = findViewById(R.id.survAnswer1);
        answer2 = findViewById(R.id.survAnswer2);
        confirm = findViewById(R.id.survNewSendBtn);
        backBtn = findViewById(R.id.survNewBackBtn);
        date1Btn = findViewById(R.id.survNewDateTimeStart);
        date2Btn = findViewById(R.id.survNewDateTimeEnd);
        multiDaysSwitch = findViewById(R.id.multiDaysSwitch);
        dateTimeEndLayout = findViewById(R.id.dateTimeEndLayout);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Il faudrait faire attention au failles, genre inclusions
                // Tester si tous les champs ont été remplis + conditions de(s) date(s)
                if (
                        ( // si tout est remplit
                                !title.getText().toString().isEmpty() &&
                                 !question.getText().toString().isEmpty() &&
                                        !answer1.getText().toString().isEmpty() &&
                                        !answer2.getText().toString().isEmpty()
                        ) &&
                                //si le switch et les dates entre elles corrrespondent
                                (// pour 2 dates
                                        (
                                                multiDaysSwitch.isChecked() &&
                                                        newEvent.getDayOfEvent().before(newEvent.getDayEndOfEvent())
                                        ) || (
                                                !multiDaysSwitch.isChecked()
                                        )

                                )
                ) {
                    if (!multiDaysSwitch.isChecked()) {
                        newEvent.setDayEndOfEvent(newEvent.getDayOfEvent());
                    }

                    newEvent.setTitle(title.getText().toString());
                    newEvent.setQuestionSurvey(question.getText().toString());
                    newEvent.setAnswer1(answer1.getText().toString());
                    newEvent.setAnswer2(answer2.getText().toString());
                    newEvent.setDayOfCreation(today);

                    // si il est bien connecté à internet
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new Eddy_Malou(getApplicationContext()).execute();
                    } else {
                        Toast.makeText(SurveyActivity.this, "Vous n'êtes pas connécté à internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SurveyActivity.this, "Veullez remplir tous les champs", Toast.LENGTH_LONG).show();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        multiDaysSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dateTimeEndLayout.setVisibility(View.VISIBLE);
                } else {
                    dateTimeEndLayout.setVisibility(View.GONE);
                }
            }
        });
        dateTimeEndLayout.setVisibility(View.GONE);

        // init le format des dates
        format = new SimpleDateFormat();
        format.applyPattern(MainActivity.UNI_DATE_TIME);

        // instancie le nouvel objet
        newEvent = new Event();
        interDialog = new InterDialog();

        // initialise les variables d'aujourd'hui
        calendar = Calendar.getInstance();
        dayNow = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        // monthNow commence au mois 0
        monthNow = String.valueOf(calendar.get(Calendar.MONTH));
        yearNow = String.valueOf(calendar.get(Calendar.YEAR));
        hourNow = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        minuteNow = String.valueOf(calendar.get(Calendar.MINUTE));
        try {
            today = format.parse(yearNow + "-" + (Integer.valueOf(monthNow) + 1) + "-" + dayNow + " " + hourNow + ":" + minuteNow);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // initialise les variables de la date choisi au nouvel objet
        setEvtDay(FIRST_DATE_ID, yearNow, monthNow, dayNow, hourNow, minuteNow);
        setEvtDay(LAST_DATE_ID, yearNow, monthNow, dayNow, hourNow, minuteNow);

        // Affichage de la date et l'heure
        date1Btn.setText(showDateTime(dayNow, monthNow, yearNow, hourNow, minuteNow));
        date2Btn.setText(showDateTime(dayNow, monthNow, yearNow, hourNow, minuteNow));

        // DatePicker Dialog : First day of Event
        date1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(FIRST_DATE_ID,
                        Integer.valueOf(new SimpleDateFormat("yyyy").format(newEvent.getDayOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("MM").format(newEvent.getDayOfEvent())) - 1,
                        Integer.valueOf(new SimpleDateFormat("dd").format(newEvent.getDayOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("HH").format(newEvent.getDayOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("mm").format(newEvent.getDayOfEvent())));

            }
        });

        // DatePicker Dialog : Last day of Event
        date2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPickerDialog(LAST_DATE_ID,
                        Integer.valueOf(new SimpleDateFormat("yyyy").format(newEvent.getDayEndOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("MM").format(newEvent.getDayEndOfEvent())) - 1,
                        Integer.valueOf(new SimpleDateFormat("dd").format(newEvent.getDayEndOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("HH").format(newEvent.getDayEndOfEvent())),
                        Integer.valueOf(new SimpleDateFormat("mm").format(newEvent.getDayEndOfEvent())));
            }
        });
    }

    // DayPickerDialog, selectedMonth commence à 0
    private void showDayPickerDialog(int idDate, int selectedYear, int selectedMonth, int selectedDay, int selectedHour, int selectedMinute) {

        // Montre le Dialog de selection de date
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int Dyear, int Dmonth, int DdayOfMonth) {

                interDialog.setDay(String.valueOf(Dyear), String.valueOf(Dmonth), String.valueOf(DdayOfMonth));

                //TimePicker Dialog
                //Montre le Dialog de selection de l'heure
                timePickerDialog = new TimePickerDialog(SurveyActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int DhourOfDay, int Dminute) {
                        //Affiche et fixe les variables du nouvel evenement par l'heure choisit
                        interDialog.setTime(String.valueOf(DhourOfDay), String.valueOf(Dminute));

                        setEvtDay(idDate, interDialog.year, interDialog.month, interDialog.day, interDialog.hour, interDialog.minute);
                        if (idDate == FIRST_DATE_ID) {
                            date1Btn.setText(showDateTime(interDialog.day, interDialog.month, interDialog.year,
                                    interDialog.hour, interDialog.minute));
                        } else if (idDate == LAST_DATE_ID) {
                            date2Btn.setText(showDateTime(interDialog.day, interDialog.month, interDialog.year,
                                    interDialog.hour, interDialog.minute));
                        }
                    }
                }, selectedHour, selectedMinute, true);
                timePickerDialog.show();
            }
        }, selectedYear, selectedMonth, selectedDay);
        datePickerDialog.show();
    }

    private void setEvtDay(int idDate, String selectedYear, String selectedMonth, String selectedDay, String selectedHour, String selectedMinute) {
        Date dayToEdit = new Date();
        try {
            dayToEdit = format.parse(selectedYear + "-" + (Integer.valueOf(selectedMonth) + 1) + "-" + selectedDay + " " + selectedHour + ":" + selectedMinute);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (idDate == FIRST_DATE_ID) {
            newEvent.setDayOfEvent(dayToEdit);
        } else {
            newEvent.setDayEndOfEvent(dayToEdit);
        }

    }

    // petite classe permettant le dialog entre les 2 dialogs (DatePickerDialog et TimePickerDialog)
    private class InterDialog {
        String year, month, day, hour, minute;

        public void setDay(String year, String month, String day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public void setTime(String hour, String minute) {
            this.hour = hour;
            this.minute = minute;
        }
    }

    // les mois doivent commencer à 0
    private String showDateTime(String day, String month, String year, String hour, String minute) {
        SimpleDateFormat format = new SimpleDateFormat();
        Date date;
        try {
            format.applyPattern(MainActivity.UNI_DATE_TIME);
            date = format.parse(year + "-" + String.valueOf(Integer.valueOf(month) + 1) + "-" + day + " " + hour + ":" + minute);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        format.applyPattern(MainActivity.SHOWN_DATE_TIME);
        return format.format(date);
    }

    public class Eddy_Malou extends AsyncTask<Void, Void, Void> {

        Connection conn = null;
        Statement stmt = null;
        Context context;

        public Eddy_Malou(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void v) {
            Toast.makeText(context, "Sondage créééé", Toast.LENGTH_LONG).show();
            finish();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT idSchool, idClub, levelAccess FROM accountdb WHERE idUser="+idAuthor);
                int idClub = 0;
                int levelAccess = 1;
                if (resultSet.next()) {
                    idSchool = resultSet.getInt("idSchool");
                    levelAccess = resultSet.getInt("levelAccess");
                    if (levelAccess == 1) {
                        idClub = resultSet.getInt("idClub");
                    }
                }

                PreparedStatement prep = conn.prepareStatement("INSERT INTO eventsdb " +
                        "(titleEvent, question, answer1, answer2, idAuthor, idClub, dayOfCreation, dayOfEvent, dayEndOfEvent, idSchool) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?)");
                prep.setString(1, newEvent.getTitle());
                prep.setString(2, newEvent.getQuestionSurvey());
                prep.setString(3, newEvent.getAnswer1());
                prep.setString(4, newEvent.getAnswer2());
                prep.setInt(5, idAuthor);
                prep.setInt(6, idClub);
                prep.setDate(7, new java.sql.Date(newEvent.getDayOfCreation().getTime()));
                prep.setTimestamp(8, new Timestamp(newEvent.getDayOfEvent().getTime()));
                prep.setTimestamp(9, new Timestamp(newEvent.getDayEndOfEvent().getTime()));
                prep.setInt(10, idSchool);

                prep.executeUpdate();
                prep.close();
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
