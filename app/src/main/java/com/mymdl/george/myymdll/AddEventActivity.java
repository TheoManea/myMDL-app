package com.mymdl.george.myymdll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mymdl.george.myymdll.R;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {

    // Variables liées au layout
    private EditText evtTitle, evtDetail;
    private SearchableSpinner spinner; //https://github.com/miteshpithadiya/SearchableSpinner
    private Button sendBtn, backBtn, evtDateTimeStart, evtDateTimeEnd, evtPic;
    private LinearLayout secondDateLayout;
    private Switch multiDateSwitch;
    private ImageView imgEvent;

    private Bitmap bitmap;
    private boolean bitmapIsProvided;

    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    private String pathImage;

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

    int idAuthor, levelAccessAuthor, idSchool, positionClubSelected = 0;
    private List<Club> clubList;

    //variables base de données
    // SELECT_MODE : séléctionne les clubs à afficher dans le spinner
    // INSERT_MODE : insère ou update les données de la base
    // BASIC_INFO_MODE : selectionne des infos nécessaire par la suite comme idSchool ou levelAccess
    private int SELECT_MODE = 0, INSERT_MODE = 1, BASIC_INFO_MODE = 2, UPDATE_MODE = 3;

    // variable pour le mode d'affichage et du systeme
    private int show_mode;
    // INSERT_EVENT_MODE : les données sont nouvelles (nouvel event)
    // EDIT_EVENT_MODE : les données doivent être modifiés (venant dans ancien event)
    private static int INSERT_EVENT_MODE = 0, EDIT_EVENT_MODE = 1,
            FIRST_DATE_ID = 0, LAST_DATE_ID = 1;

    // format des dates
    SimpleDateFormat format;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.evtNewMainLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("Nouvel Evénement");
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

        // recupère l'idAuthor de l'acivité précédente, si il y en a
        Intent intent = getIntent();

        if (intent.hasExtra("event") && intent.hasExtra("idAuthor")) { // si il y a un ancien event, c'est un edit
            // Titre
            setTitle("Modifier un évènement");
            formerEvent = intent.getParcelableExtra("event");
            idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));
            show_mode = EDIT_EVENT_MODE;
        } else if (intent.hasExtra("idAuthor")) {
            // Titre
            setTitle("Ajouter un évènement");

            idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));
            levelAccessAuthor = Integer.valueOf(intent.getStringExtra("levelAccess"));
            show_mode = INSERT_EVENT_MODE;
        }

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
        if (show_mode == INSERT_EVENT_MODE) { // avec les variables d'aujourd'hui dans le cas d'un ajout
            setEvtDay(FIRST_DATE_ID, yearNow, monthNow, dayNow, hourNow, minuteNow);
            setEvtDay(LAST_DATE_ID, yearNow, monthNow, dayNow, hourNow, minuteNow);
        } else { // avec les variables de l'ancien event dans le cas d'un edit
            newEvent = formerEvent;
        }

        // Initialisation des éléments du layout
        evtTitle = findViewById(R.id.evtNewTitle);
        evtDetail = findViewById(R.id.evtNewDetail);
        evtDateTimeStart = findViewById(R.id.evtNewDateTimeStart);
        evtDateTimeEnd = findViewById(R.id.evtNewDateTimeEnd);
        sendBtn = findViewById(R.id.evtNewSendBtn);
        backBtn = findViewById(R.id.evtNewBackBtn);
        spinner = findViewById(R.id.sSpinnerClub);
        secondDateLayout = findViewById(R.id.dateTimeEndLayout);
        multiDateSwitch = findViewById(R.id.multiDaysSwitch);
        evtPic = findViewById(R.id.evtNewPic);
        imgEvent = findViewById(R.id.imageView3);

            // Affichage de la date et l'heure
        if (show_mode == INSERT_EVENT_MODE) {
            evtDateTimeStart.setText(showDateTime(dayNow, monthNow, yearNow, hourNow, minuteNow));
            evtDateTimeEnd.setText(showDateTime(dayNow, monthNow, yearNow, hourNow, minuteNow));
        } else {
            evtTitle.setText(formerEvent.getTitle());
            evtDetail.setText(formerEvent.getDetail());

            evtDateTimeStart.setText(formerEvent.getStringDayOfEvent());
            evtDateTimeEnd.setText(formerEvent.getStringDayEndOfEvent());
        }

        // affichage : coche ou décoche le switch du multi date selon l'event à modifier
        //  Si on modifie (que la 2e dates n'est pas la même que la première
        if (!newEvent.getStringUniversalDayOfEvent().equals(newEvent.getStringUniversalDayEndOfEvent())) {
            multiDateSwitch.setChecked(true);
            secondDateLayout.setVisibility(View.VISIBLE);
        } else {
            secondDateLayout.setVisibility(View.GONE);
        }

        // DatePicker Dialog : First day of Event
        evtDateTimeStart.setOnClickListener(new View.OnClickListener() {
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

        evtPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });

        // DatePicker Dialog : Last day of Event
        evtDateTimeEnd.setOnClickListener(new View.OnClickListener() {
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

        // Send/valid Button
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tester si tous les champs ont été remplis + conditions de(s) date(s)
                if (
                        ( // si tout est remplit
                            !evtTitle.getText().toString().isEmpty() &&
                            !evtDetail.getText().toString().isEmpty() &&
                            spinner != null && spinner.getSelectedItem() !=null
                        ) &&
                        //si le switch et les dates entre elles corrrespondent
                        (// pour 2 dates
                            (
                                multiDateSwitch.isChecked() &&
                                newEvent.getDayOfEvent().before(newEvent.getDayEndOfEvent())
                            ) || (
                                !multiDateSwitch.isChecked()
                            )

                        )
                ) {
                    if (!multiDateSwitch.isChecked()) {
                        newEvent.setDayEndOfEvent(newEvent.getDayOfEvent());
                    }

                    newEvent.setTitle(evtTitle.getText().toString());
                    newEvent.setDetail(evtDetail.getText().toString());
                    newEvent.setDayOfCreation(today);
                    newEvent.setTitleClubLinked(clubList.get(positionClubSelected).getTitleClub());
                    newEvent.setIdClubLinked(clubList.get(positionClubSelected).getIdClub());

                    // si il est bien connecté à internet
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Insert ou edit selon le mode
                        if (show_mode == INSERT_EVENT_MODE) {
                            new DbClass(INSERT_MODE, "INSERT INTO eventsdb (titleEvent, detailsEvent, idAuthor, idClub, dayOfCreation, dayOfEvent, dayEndOfEvent, idSchool) " +
                                    "VALUES (?,?,?,?,?,?,?,?)").execute();
                        } else {
                            new DbClass(UPDATE_MODE, "UPDATE eventsdb SET titleEvent=?, detailsEvent=?, idClub=?, dayOfEvent=?, dayEndOfEvent=? WHERE idEvent= ?").execute();
                        }

                        //Test pour le FTP
                        String[] args = {"a","b"};
                        FtpUploader upload = new FtpUploader(getApplicationContext());
                        upload.doInBackground(args);


                    } else {
                        Toast.makeText(AddEventActivity.this, "Vous n'êtes pas connécté à internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddEventActivity.this, "Veullez remplir tous les champs", Toast.LENGTH_LONG).show();
                }

            }
        });

        // Back Button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        multiDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    secondDateLayout.setVisibility(View.VISIBLE);
                } else {
                    secondDateLayout.setVisibility(View.GONE);
                }
            }
        });

        // ici, l'idSchool peut être different que celui stoquer quand le SharedPref, car un admin d'un lycée peut selectionner
        // n'importe quel autre lycée
        new DbClass(BASIC_INFO_MODE, "SELECT idSchool FROM accountdb WHERE idUser = "+ idAuthor).execute();

    }

    private void FtpUpload()
    {

    }


    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};


            try {
                Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
                cursor.moveToFirst();

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgEvent.setImageBitmap(bitmap);

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String pathImage = cursor.getString(columnIndex);
                cursor.close();

                bitmapIsProvided = true;

                evtPic.setText(pathImage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // pour le bouton de retour
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // DayPickerDialog, selectedMonth commence à 0
    private void showDayPickerDialog(int idDate, int selectedYear, int selectedMonth, int selectedDay,
                                     int selectedHour, int selectedMinute) {

        // Montre le Dialog de selection de date
        datePickerDialog = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int Dyear, int Dmonth, int DdayOfMonth) {

                interDialog.setDay(String.valueOf(Dyear), String.valueOf(Dmonth), String.valueOf(DdayOfMonth));

                //TimePicker Dialog
                    //Montre le Dialog de selection de l'heure
                timePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int DhourOfDay, int Dminute) {
                        //Affiche et fixe les variables du nouvel evenement par l'heure choisit
                        interDialog.setTime(String.valueOf(DhourOfDay), String.valueOf(Dminute));

                        setEvtDay(idDate, interDialog.year, interDialog.month, interDialog.day, interDialog.hour, interDialog.minute);
                        if (idDate == FIRST_DATE_ID) {
                            evtDateTimeStart.setText(showDateTime(interDialog.day, interDialog.month, interDialog.year,
                                    interDialog.hour, interDialog.minute));
                        } else if (idDate == LAST_DATE_ID) {
                            evtDateTimeEnd.setText(showDateTime(interDialog.day, interDialog.month, interDialog.year,
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

    // Connexion à la base de donnée
    public class DbClass extends AsyncTask<Void, Void, List<Club>> {
        // variables de connexion
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement prep = null;

        // variable récupérées/ on récupère idSchool au passage , car trop compliqué par la suite
        int idClub, midSchool, mlevelAccess;
        String titleClub;


        // quelle mode adopté ?
        private int modeQuery;
        private String query;

        //contructeur 1
        public DbClass(int modeQuery, String query) {
            this.modeQuery = modeQuery;
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<Club> clubListArray) {
            if (modeQuery == INSERT_MODE || modeQuery == UPDATE_MODE) {
                if (show_mode == INSERT_EVENT_MODE)
                    Toast.makeText(AddEventActivity.this, "Nouvel évènement crée !", Toast.LENGTH_LONG).show();
                else if (show_mode == EDIT_EVENT_MODE) {
                    Toast.makeText(AddEventActivity.this, "Evènement modifié !", Toast.LENGTH_LONG).show();
                }
                finish();
            } else if (modeQuery == SELECT_MODE) {
                // fait des trucs avec la liste de club, et le spinner, determine la position du club lié à l'ancien event, si il y en a un
                clubList = clubListArray;
                int positionFormerEvtClub = 0;
                int tempPositionClub = 0;
                List<String> nameClubList = new ArrayList<>();
                for (Club clubTmp : clubList) {
                    nameClubList.add(clubTmp.getTitleClub());
                    if (show_mode == EDIT_EVENT_MODE) {
                        if (clubTmp.getIdClub() == newEvent.getIdClubLinked()) {
                            positionFormerEvtClub = tempPositionClub;
                        }
                        tempPositionClub++;
                    }
                }

                // /!\ ne pas mettre de hint dans le layout sinon, marche plus, jsp pk
                ArrayAdapter clubAdapter = new ArrayAdapter(AddEventActivity.this, android.R.layout.simple_dropdown_item_1line, nameClubList);
                spinner.setAdapter(clubAdapter);
                spinner.setSelection(positionFormerEvtClub);
                spinner.setTitle("Selectioner le club associé");
                spinner.setPositiveButton("OK");
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        positionClubSelected = spinner.getSelectedItemPosition();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } else if (modeQuery == BASIC_INFO_MODE) {
                // initialise l'idSchool pour la classe AddEventActivity
                idSchool = midSchool;
                levelAccessAuthor = mlevelAccess;

                // appelle de la base de donnée pour afficher le drop down des clubs
                if (mlevelAccess == 2) {
                    new DbClass(SELECT_MODE, "SELECT idClub,titleClub FROM clubdb WHERE idSchool="+idSchool+" ORDER BY titleClub ASC").execute();
                } else {
                    /*spinner.setVisibility(View.GONE);
                    ClubLinkedTv.setVisibility(View.GONE);*/
                    new DbClass(SELECT_MODE, "SELECT clubdb.idClub,clubdb.titleClub FROM clubdb JOIN accountdb " +
                            "ON clubdb.idClub = accountdb.idClub WHERE accountdb.idUser = "+idAuthor+" AND clubdb.idSchool = "+idSchool+" ORDER BY titleClub ASC").execute();
                }
            }
        }

        @Override
        protected List<Club> doInBackground(Void... params) {
            List<Club> clubList = new ArrayList<>();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();

                if (modeQuery == SELECT_MODE) {
                    ResultSet resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        idClub = resultSet.getInt("idClub");
                        titleClub = resultSet.getString("titleClub");
                        clubList.add(new Club(idClub, titleClub,""));
                    }
                } else if (modeQuery == INSERT_MODE) {
                    prep = conn.prepareStatement(query);
                    prep.setString(1, newEvent.getTitle());
                    prep.setString(2, newEvent.getDetail());
                    prep.setInt(3, idAuthor);
                    prep.setInt(4, newEvent.getIdClubLinked());
                    prep.setDate(5, new java.sql.Date(newEvent.getDayOfCreation().getTime()));
                    prep.setTimestamp(6, new Timestamp(newEvent.getDayOfEvent().getTime()));
                    prep.setTimestamp(7, new Timestamp(newEvent.getDayEndOfEvent().getTime()));
                    prep.setInt(8, idSchool);

                    prep.executeUpdate();
                    prep.close();

                } else if (modeQuery == UPDATE_MODE) {
                    prep = conn.prepareStatement(query);
                    // modif l'id de l'auteur ?
                    prep.setString(1, newEvent.getTitle());
                    prep.setString(2, newEvent.getDetail());
                    prep.setInt(3, newEvent.getIdClubLinked());
                    prep.setTimestamp(4, new Timestamp(newEvent.getDayOfEvent().getTime()));
                    prep.setTimestamp(5, new Timestamp(newEvent.getDayEndOfEvent().getTime()));
                    prep.setInt(6, formerEvent.getIdEvent());

                    prep.executeUpdate();
                    prep.close();
                } else if (modeQuery == BASIC_INFO_MODE) {
                    // on récupère l'idSchool, lié à l'utilisateur qui créé l'event
                    ResultSet resultSet = stmt.executeQuery(query);
                    if (resultSet.next()) {
                        midSchool = resultSet.getInt("idSchool");
                    }

                    resultSet = stmt.executeQuery("SELECT levelAccess FROM accountdb WHERE idUser = " + idAuthor);
                    if (resultSet.next()) {
                        mlevelAccess = resultSet.getInt("levelAccess");
                    }
                    resultSet.close();
                }

                stmt.close();
                conn.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return clubList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
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
}
