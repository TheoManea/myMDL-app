package com.mymdl.george.myymdll;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.mymdl.george.myymdll.R;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import java.security.MessageDigest;

public class AddUserActivity extends AppCompatActivity {

    // mode d'affichage
    private int showMode;
    private int ADD_MODE = 0, EDIT_MODE = 1, BASIC_INFO_MODE = 2;

    private User user;
    private int idAuthor, levelAccessAuthor, levelAccessToGive;
    private List<Club> clubList;
    private List<School> schoolList;
    private List<Region> regionList;


    // éléments du layouts
    EditText nameEt, fNameEt, passwordEt, mailEt;
    TextView chooseTv;
    Button userNewBackBtn, userNewSendBtn, editPasswordBtn;
    SearchableSpinner associateSpinner;
    ConstraintLayout constraintLayAddUser;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    // mail de l'admin (qui veut ajouter un user)
    String intentMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Pout NavigationDrawer/Menu
        mDrawerLayout = findViewById(R.id.newUserDrawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        TextView headTxt = findViewById(R.id.tvTitle);
        headTxt.setText("Nouveau membre");
        headTxt.setTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.redfmdl)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.redfmdldark));
        }


        //#########################Creating SendMail object
        /*String sb = "<head>" +
                "<style type=\"text/css\">" +
                "  .red { color: #f00; }" +
                "</style>" +
                "</head>" +
                "<h1 class=\"red\">"  + "</h1>" +
                "<p>" +
                "Lorem ipsum dolor sit amet, <em>consectetur</em> adipisicing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna <strong>" +
                "aliqua</strong>.</p>";
        SendMail sm = new SendMail(this, "theomanea9@gmail.com", "Voici un test", sb);*/
        //sm.execute();
        //Toast.makeText(AddUserActivity.this, "Mail envoyé", Toast.LENGTH_LONG).show();
        //##########################

        // email de l'admin actuellement connécté ======> /!\ marche pas : revoie null
        Intent intent = getIntent();
        intentMail = String.valueOf(intent.getStringExtra("emailUser"));

        // chercher elements du layout
        nameEt = findViewById(R.id.nameEt);
        fNameEt = findViewById(R.id.fNameEt);
        passwordEt = findViewById(R.id.passwordEt);
        mailEt = findViewById(R.id.mailEt);
        userNewSendBtn = findViewById(R.id.userNewSendBtn);
        userNewBackBtn = findViewById(R.id.userNewBackBtn);
        editPasswordBtn = findViewById(R.id.editPasswordBtn);
        constraintLayAddUser = findViewById(R.id.constraintLayAddUser);

        associateSpinner = findViewById(R.id.associateSpinner);
        chooseTv = findViewById(R.id.chooseTv);

        // bouton retour
        userNewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // bouton sauvegarder
        userNewSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEt.getText().toString().isEmpty() || fNameEt.getText().toString().isEmpty() || (passwordEt.getText().toString().isEmpty() && showMode == ADD_MODE)
                || mailEt.getText().toString().isEmpty()) {
                    Toast.makeText(AddUserActivity.this, "Faut tout remplir", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mailEt.getText().toString().contains("@") || !mailEt.getText().toString().contains(".")) {
                    Toast.makeText(AddUserActivity.this, "L'adresse mail n'est pas valide", Toast.LENGTH_LONG).show();
                    return;
                }
                if (associateSpinner == null || associateSpinner.getSelectedItem() ==null) {
                    return;
                }


                if (showMode == ADD_MODE) {
                    new dbUser(ADD_MODE, "INSERT INTO accountdb(nameUser, familyNameUser, passwordUser, levelAccess, idClub, idSchool, idRegion, emailUser) VALUES" +
                            "(?,?,?,?,?,?,?,?)").execute();
                } else if (showMode == EDIT_MODE) {
                    // si c'est vide, pas de chamgement de mot de passe
                    String query = "";
                    if(passwordEt.getText().toString().isEmpty()) {
                        query = "UPDATE accountdb SET nameUser=?,familyNameUser=?,levelAccess=?," +
                                "idClub=?,idSchool=?,idRegion=?,emailUser=? WHERE idUser=?";
                    } else {
                        query = "UPDATE accountdb SET nameUser=?,familyNameUser=?,passwordUser=?,levelAccess=?," +
                                "idClub=?,idSchool=?,idRegion=?,emailUser=? WHERE idUser=?";
                    }

                    new dbUser(EDIT_MODE, query).execute();
                }
            }
        });


        clubList = new ArrayList<>();
        schoolList = new ArrayList<>();
        regionList = new ArrayList<>();


        // si il y a un objet "user", alors, c'est un edit
        if(intent.hasExtra("user") && intent.hasExtra("idAuthor")) {
            // titre
            headTxt.setText("Modifier un membre");
            user = intent.getParcelableExtra("user");
            idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));
            showMode = EDIT_MODE;

            nameEt.setText(user.getNameUser());
            fNameEt.setText(user.getFamilyNameUser());
            mailEt.setText(user.getEmail());
        } else if (intent.hasExtra("idAuthor")){
            idAuthor = Integer.valueOf(intent.getStringExtra("idAuthor"));
            showMode = ADD_MODE;
        }

        if (showMode == EDIT_MODE) {
            passwordEt.setEnabled(false);
            passwordEt.setVisibility(View.GONE);
            editPasswordBtn.setVisibility(View.VISIBLE);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayAddUser);
            constraintSet.connect(R.id.SpinnerLayout, ConstraintSet.TOP, R.id.editPasswordBtn, ConstraintSet.BOTTOM);
            constraintSet.applyTo(constraintLayAddUser);
            // bouton pour changer le mot de passe: utile pour ne pas obliger le changement à chaque edit de profil
            editPasswordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // boite de dialog
                    final AlertDialog dialogBuilder = new AlertDialog.Builder(AddUserActivity.this).create();
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.edit_password_popup, null);

                    // layout
                    Button save = dialogView.findViewById(R.id.saveBtn);
                    Button cancel = dialogView.findViewById(R.id.cancelBtn);

                    // annule l'action
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogBuilder.dismiss();
                        }
                    });

                    // sauvegared
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // stock le nouveau mot de passe dans le champ de text
                            EditText newpass = dialogView.findViewById(R.id.passwordDialogEt);
                            passwordEt.setText(newpass.getText());
                            dialogBuilder.dismiss();
                        }

                    });

                    dialogBuilder.setView(dialogView);
                    dialogBuilder.show();
                }

            });
        }


        new dbUser(BASIC_INFO_MODE, "SELECT levelAccess FROM accountdb WHERE idUser = "+idAuthor).execute();
    }



    public static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }




    public class dbUser extends AsyncTask<Void, Void, Void>
    {
        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        // quelle mode adopté ?
        private int modeQuery;
        private String query;

        // message d'erreur si besoin
        String errorStr = "";

        //contructeur 1
        public dbUser(int modeQuery, String query) {
            this.modeQuery = modeQuery;
            this.query = query;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(Void nothing) {
            if (modeQuery == BASIC_INFO_MODE) {

                // init le spinner (prend la position dans la liste de l'item lié à l'ancien user)
                int positionFormer = 0;
                int tempPosition = 0;
                List<String> nameItemList = new ArrayList<>();

                switch (levelAccessAuthor) {
                    case 2:
                        for (Club clubTmp : clubList ) {
                            nameItemList.add(clubTmp.getTitleClub());
                            if (showMode == EDIT_MODE) {
                                if (clubTmp.getIdClub() == user.getIdClub()) {
                                    positionFormer = tempPosition;
                                }
                                tempPosition++;
                            }
                        }
                        //associateSpinner.setTitle("Selectioner le club associé");
                        break;
                    case 3:
                        for (School schoolTmp : schoolList ) {
                            nameItemList.add(schoolTmp.getNameSchool());
                            if (showMode == EDIT_MODE) {
                                if (schoolTmp.getIdSchool() == user.getIdSchool()) {
                                    positionFormer = tempPosition;
                                }
                                tempPosition++;
                            }
                        }
                        //associateSpinner.setTitle("Selectioner le lycée associé");
                        break;
                    case 4:
                        for (Region regionTmp : regionList ) {
                            nameItemList.add(regionTmp.getNameRegion());
                            if (showMode == EDIT_MODE) {
                                if (regionTmp.getIdRegion() == user.getIdRegion()) {
                                    positionFormer = tempPosition;
                                }
                                tempPosition++;
                            }
                        }
                        //associateSpinner.setTitle("Selectioner la region associée");
                        break;
                }

                // android.R.layout.simple_dropdown_item_1line
                // https://codingwithsara.com/android-studio-how-to-customize-spinner/
                // NE PAS METTRE DE hint DANS LE SPINNER DU LAYOUT ,SINON IL VEUT PAS QU'ON METTE NOTRE LAYOUT CUSTOM
                ArrayAdapter spinnerAdapter = new ArrayAdapter(AddUserActivity.this, R.layout.custom_spinner, nameItemList);
                associateSpinner.setAdapter(spinnerAdapter);
                associateSpinner.setSelection(positionFormer);
                associateSpinner.setPositiveButton("OK");
                associateSpinner.setTitle("Séléctionner un structure");
            } else {
                if (!errorStr.equals("")) {
                    Toast.makeText(AddUserActivity.this, errorStr, Toast.LENGTH_LONG).show();
                } else {
                    if (modeQuery == ADD_MODE) {
                        Toast.makeText(AddUserActivity.this, "Nouvel utilisateur crée !", Toast.LENGTH_LONG).show();
                        finish();
                    } else if (modeQuery == EDIT_MODE) {
                        Toast.makeText(AddUserActivity.this, "utilisateur modifié !", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                if (modeQuery == BASIC_INFO_MODE) {
                    ResultSet resultSet = stmt.executeQuery(query);
                    if (resultSet.next()) {
                        levelAccessAuthor = resultSet.getInt("levelAccess");
                        switch (levelAccessAuthor) {
                            case 2:
                                levelAccessToGive = 1;

                                resultSet = stmt.executeQuery("SELECT idSchool FROM accountdb WHERE idUser = "+idAuthor);
                                int idSchool = 0;
                                if (resultSet.next()) {
                                    idSchool = resultSet.getInt("idSchool");
                                }

                                resultSet = stmt.executeQuery("SELECT idClub, titleClub FROM clubdb WHERE idSchool="+idSchool);
                                while (resultSet.next()) {
                                    int idClub = resultSet.getInt("idClub");
                                    String titleClub = resultSet.getString("titleClub");

                                    Club club = new Club (idClub, titleClub, null);
                                    club.setIdSchool(idSchool);

                                    clubList.add(club);
                                }
                                break;
                            case 3:
                                levelAccessToGive = 2;

                                resultSet = stmt.executeQuery("SELECT idRegion FROM accountdb WHERE idUser = "+idAuthor);
                                int idRegion = 0;
                                if (resultSet.next()) {
                                    idRegion = resultSet.getInt("idRegion");
                                }

                                resultSet = stmt.executeQuery("SELECT idSchool, nameSchool FROM schooldb WHERE idRegion = "+idRegion);
                                while (resultSet.next()) {
                                    int mIdSchool = resultSet.getInt("idSchool");
                                    String nameSchool = resultSet.getString("nameSchool");

                                    schoolList.add(new School(mIdSchool, nameSchool));
                                }
                                break;
                            case 4:
                                levelAccessToGive = 3;

                                resultSet = stmt.executeQuery("SELECT idRegion, nameRegion FROM regiondb");
                                while (resultSet.next()) {
                                    int mIdRegion = resultSet.getInt("idRegion");
                                    String nameRegion = resultSet.getString("nameRegion");

                                    regionList.add(new Region(mIdRegion, nameRegion));
                                }
                                break;
                        }
                    }
                } else {
                    // on verifie que l'email n'est pas déjà utilisé
                    PreparedStatement prepMail = conn.prepareStatement("SELECT COUNT(idUser) AS numMail FROM accountdb WHERE emailUser = ? AND idUser <> ?");
                    prepMail.setString(1, mailEt.getText().toString());
                    if (showMode == EDIT_MODE) {
                        prepMail.setInt(2, user.getIdUser());
                    } else {
                        prepMail.setInt(2, 0);
                    }

                    ResultSet rs = prepMail.executeQuery();
                    if (rs.next()) {
                        // s'il existe déjà un email similaire
                        if (rs.getInt("numMail") > 0) {
                            errorStr = "Email déjà utilisé";
                        } else {
                            PreparedStatement prep = conn.prepareStatement(query);
                            int i = 1;
                            prep.setString(i, nameEt.getText().toString().toLowerCase()); i++;
                            prep.setString(i, fNameEt.getText().toString().toLowerCase()); i++;
                            // si passwordEt non vide -> changement de mot de passe
                            if (!passwordEt.getText().toString().isEmpty()) {
                                String MD5_HASH_PASSWORD = md5(passwordEt.getText().toString());
                                prep.setString(i, MD5_HASH_PASSWORD); i++;
                            }
                            prep.setInt(i, levelAccessToGive); i++;
                            // differents cas de remplissage selon l'utilisateur crééé
                            switch (levelAccessToGive) {
                                case 1:
                                    prep.setInt(i, clubList.get(associateSpinner.getSelectedItemPosition()).getIdClub()); i++;
                                    prep.setInt(i, clubList.get(associateSpinner.getSelectedItemPosition()).getIdSchool()); i++;
                                    prep.setNull(i, Types.INTEGER); i++;
                                    break;
                                case 2:
                                    prep.setNull(i, Types.INTEGER); i++;
                                    prep.setInt(i, schoolList.get(associateSpinner.getSelectedItemPosition()).getIdSchool()); i++;
                                    prep.setNull(i, Types.INTEGER); i++;
                                    break;
                                case 3:
                                    prep.setNull(i, Types.INTEGER); i++;
                                    prep.setNull(i, Types.INTEGER); i++;
                                    prep.setInt(i, regionList.get(associateSpinner.getSelectedItemPosition()).getIdRegion()); i++;
                                    break;
                            }
                            prep.setString(i, mailEt.getText().toString()); i++;
                            // si c'est une modification
                            if (showMode == EDIT_MODE) {
                                prep.setInt(i, user.getIdUser()); // i++
                            }

                            prep.executeUpdate();
                            prep.close();
                        }
                    }
                    prepMail.close();
                    rs.close();
                }

                stmt.close();
                conn.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) { }
    }


    private class School {
        int idSchool;
        String nameSchool;

        public School(int idSchool, String nameSchool) {
            this.idSchool = idSchool;
            this.nameSchool = nameSchool;
        }

        // getter

        public int getIdSchool() {
            return idSchool;
        }

        public String getNameSchool() {
            return nameSchool;
        }
    }

    private class Region {
        int idRegion;
        String nameRegion;

        public Region(int idRegion, String nameRegion) {
            this.idRegion = idRegion;
            this.nameRegion = nameRegion;
        }

        // getter
        public int getIdRegion() {
            return idRegion;
        }

        public String getNameRegion() {
            return nameRegion;
        }
    }
}
