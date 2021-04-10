package com.mymdl.george.myymdll;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private EditText emailET, passwordET;
    private String MD5_HASH_PASSWORD;

    private Button submit;
    private CheckBox rememberMeCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Connexion");

        submit = findViewById(R.id.valid);

        rememberMeCb = findViewById(R.id.rememberMeCb);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailET = findViewById(R.id.mailUser);
                passwordET = findViewById(R.id.password);

                if (emailET.getText().toString().length() > 0 && passwordET.getText().toString().length() > 0) {
                    Toast.makeText(LoginActivity.this, "Connexion en cours", Toast.LENGTH_SHORT).show();

                    final AlertDialog dialogBuilder = new AlertDialog.Builder(LoginActivity.this).create();
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.progresspopup, null);

                    new DbClass().execute();

                }else {
                    Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs",Toast.LENGTH_LONG).show();
                }
            }
        });
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

    public class DbClass extends AsyncTask<String,String,String>
    {
        Connection conn = null;
        PreparedStatement stmt = null;

        String toastConnection = "Un probleme est survenu";

        String email, password;
        boolean rememberMe;

        boolean isSuccess = false;

        // variable récupérée
        int id = 0;
        int levelAccess;
        String username;

        @Override
        protected void onPreExecute() {
            rememberMe = rememberMeCb.isChecked();

            // cherche avec la condition que le nameUser soit en minuscule
            email = emailET.getText().toString().toLowerCase();
            password = passwordET.getText().toString();
        }

        @Override
        protected void onPostExecute(String r) {
            if (isSuccess == true) {
                // "se souvient" de l'utilisateur si il le désir
                if (rememberMe == true) {
                    SharedPreferences preferences = getSharedPreferences(MainActivity.REMEMBER_USER_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putInt(MainActivity.REMEMBER_ID_KEY, id);
                    edit.putInt(MainActivity.REMEBER_LEVEL_ACCESS_KEY, levelAccess);
                    edit.apply();
                }

                // prepare le changement d'activité
                Intent adminAct = new Intent(LoginActivity.this,AdminActivity.class);
                adminAct.putExtra("idAuthor",String.valueOf(id));
                adminAct.putExtra("email", email);
                adminAct.putExtra("levelAccess", String.valueOf(levelAccess));
                adminAct.putExtra("username", username);
                adminAct.putExtra("password", password);

                startActivity(adminAct);
                finish();
            }
            else {

                Toast.makeText(LoginActivity.this, r, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
                String MD5_HASH_PASSWORD = md5(password);

                stmt = conn.prepareStatement("SELECT idUser, levelAccess, emailUser FROM accountdb WHERE emailUser=? AND passwordUser=?");
                stmt.setString(1, email);
                stmt.setString(2, MD5_HASH_PASSWORD);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    id  = resultSet.getInt("idUser");
                    levelAccess = resultSet.getInt("levelAccess");
                    // email de l'admin qui s'est connécté
                    email = resultSet.getString("emailUser");

                    isSuccess = true;
                    toastConnection = "Bonjour "+username;
                }else {
                    toastConnection = "Ce compte n'existe pas";
                    passwordET.setText("");
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
            return toastConnection;
        }
    }
}
