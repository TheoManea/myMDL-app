package com.mymdl.george.myymdll;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mymdl.george.myymdll.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    String email, username, password;
    int levelAccess;
    int id;

    TextView userET, passwordET, levelET, countArticleTv, emailET;
    Button action;

    EditText newusername, newpassword, confirm_password, newemail;

    // variables de modification
    String newusernameStr, newpasswordStr, confirm_passwordStr, neweemailstr, MD5_HASH_PASSWORD = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Modification des données");

        Intent intent = getIntent();
        id = Integer.valueOf(intent.getStringExtra("id"));
        email = intent.getStringExtra("email");
        levelAccess = Integer.valueOf(intent.getStringExtra("levelAccess"));
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        // layout
        userET = findViewById(R.id.user);
        passwordET = findViewById(R.id.password);
        levelET = findViewById(R.id.levelaccess);
        action = findViewById(R.id.action);
        countArticleTv = findViewById(R.id.countArticle);
        emailET = findViewById(R.id.email);



        //emailET.setText(email);
        levelET.setText(String.valueOf(levelAccess));
        emailET.setText(email);

        // edit button
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog dialogBuilder = new AlertDialog.Builder(SettingsActivity.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custompopup, null);

                // layout
                Button button1 = dialogView.findViewById(R.id.buttonSubmit);
                Button button2 = dialogView.findViewById(R.id.buttonCancel);

                newusername =  dialogView.findViewById(R.id.username);
                newusername.setText(username);
                newpassword = dialogView.findViewById(R.id.password);
                confirm_password = dialogView.findViewById(R.id.confirm_mdp);
                newemail = dialogView.findViewById(R.id.emailnew);
                newemail.setText(email);

                // cancel btn
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                // save btn
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        newusernameStr = newusername.getText().toString();
                        newpasswordStr = newpassword.getText().toString();
                        confirm_passwordStr = confirm_password.getText().toString();
                        neweemailstr = newemail.getText().toString();

                        if(!newusernameStr.equals("") && !confirm_passwordStr.equals(""))
                        {
                            if (!newpasswordStr.equals("")) {
                                if (newpasswordStr.equals(confirm_passwordStr)) {
                                    MD5_HASH_PASSWORD = md5(newpasswordStr);
                                    new Insert( "UPDATE accountdb SET nameUser=?, passwordUser=?, emailUser=? WHERE idUser=?").execute();
                                } else {
                                    Snackbar.make(view, "Mots de passe différents", Snackbar.LENGTH_SHORT).show();
                                }

                            } else {
                                new Insert( "UPDATE accountdb SET nameUser=?, emailUser=? WHERE idUser=?").execute();
                            }

                            dialogBuilder.dismiss();
                        }
                        else
                        {
                            Snackbar.make(view, "Erreur, veuillez rééssayer", Snackbar.LENGTH_SHORT).show();
                        }

                    }

                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });

        new refreshInfos().execute();

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


    public class Insert extends AsyncTask<Void, Void, Void>{
        // variables de connexion
        Connection conn = null;
        PreparedStatement prep = null;

        private String query;

        // variable d'erreur si besoin
        String errorStr = "";

        //contructeur 1
        public Insert(String query) {

            this.query = query;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(Void hjy) {
            if (!errorStr.equals("")) {
                Toast.makeText(SettingsActivity.this, errorStr, Toast.LENGTH_LONG).show();
            }
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Yooolllllooooo ! Modifications effectuées ! ", Snackbar.LENGTH_SHORT).show();
            new refreshInfos().execute();
        }
        @Override
        protected  Void doInBackground(Void ...params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);

                // on verifie que l'email n'est pas déjà utilisé
                PreparedStatement prepMail = conn.prepareStatement("SELECT COUNT(idUser) AS numMail FROM accountdb WHERE emailUser = ? AND idUser <> ?");
                prepMail.setString(1, emailET.getText().toString());
                // on prend son propre id car on est sur la modif de son propre profil
                prepMail.setInt(2, id);
                ResultSet rs = prepMail.executeQuery();
                if (rs.next()) {
                    // s'il existe déjà un email similaire
                    if (rs.getInt("numMail") > 0) {
                        errorStr = "Email déjà utilisé";
                    } else {
                        prep = conn.prepareStatement(query);
                        int i = 1;
                        prep.setString(i, newusernameStr); i++;
                        // si le mot de passe n'a pas été modifié
                        if (MD5_HASH_PASSWORD.isEmpty())
                            prep.setString(i, MD5_HASH_PASSWORD); i++;
                        prep.setString( i, neweemailstr); i++;
                        prep.setInt(i, id);

                        prep.executeUpdate();
                        prep.close();
                        conn.close();
                    }
                }
                prepMail.close();
                rs.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) { }
    }

    public class refreshInfos extends AsyncTask<Void, Void, Void> {

        // variables de connexion
        Connection conn = null;
        Statement stmt = null;

        private int countArticles;
        private String newname, newFName, newpass, newemailuser;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            countArticleTv.setText(String.valueOf(countArticles));
            userET.setText(newname + " " +  newFName);
            passwordET.setText("*******");/*String.valueOf(newpass)*/
            emailET.setText(String.valueOf(newemailuser));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                stmt = conn.createStatement();

                ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS rowcount FROM eventsdb WHERE idAuthor=" + String.valueOf(id));
                resultSet.next();
                countArticles = resultSet.getInt("rowcount");



                ResultSet rs = stmt.executeQuery("SELECT nameUser,familyNameUser, passwordUser, emailUser FROM accountdb WHERE idUser=" +  String.valueOf(id));
                if(rs.next()){
                    newname = rs.getString("nameUser");
                    newpass = rs.getString("passwordUser");
                    newemailuser = rs.getString("emailUser");
                    newFName = rs.getString("familyNameUser");
                }



                stmt.close();
                conn.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
