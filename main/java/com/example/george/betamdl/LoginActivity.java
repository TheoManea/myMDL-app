package com.mymdl.george.myymdll;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameET;
    private EditText passwordET;
    private Button submit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Connexion");

        usernameET = (EditText) findViewById(R.id.username);
        passwordET = (EditText) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.valid);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameET.getText().toString();
                String password = passwordET.getText().toString();

                //Toast.makeText(LoginActivity.this, username + password,Toast.LENGTH_LONG).show();

                if(username.equals("aa") && password.equals("bb")) {
                    Toast.makeText(LoginActivity.this, "OK",Toast.LENGTH_LONG).show();
                    Intent connexion = new Intent(LoginActivity.this,AdminActivity.class);
                    startActivity(connexion);
                }
            }
        });




    }





}
