package com.mymdl.george.myymdll;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.mymdl.george.myymdll.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloService extends Service {
    private static final String TAG = "HelloService";
    private boolean isRunning  = false;
    private Context context;
    private int oldCount, newCount;
    private String CHANNEL_ID="1";


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent myIntent = new Intent(this, HelloService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, myIntent, 0);

        //Log.i(TAG, "Service onStartCommand");



        SharedPreferences prefs = getSharedPreferences("DB", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("FIRST_CO", true);

        if(firstStart){
            new FirstConn().execute();
            int nbevent = prefs.getInt("NB_EVENT",0);
        }




        // Create the Handler object (on the main thread by default)
        Handler handler = new Handler();
        // Define the code block to be executed
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {

                //Log.d("Handlers", "Called on main thread");

                // se connecte à la base
                new Poutine().execute();

                // Repeat this the same runnable code block again another 2 seconds
                // 'this' is referencing the Runnable object
                handler.postDelayed(this, 10000);
            }
        };
        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }

    class FirstConn extends AsyncTask<Void,Void,Void> {
        Connection conn = null;
        Statement stmt = null;

        @Override
        protected   Void doInBackground(Void... voids){
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();
                SharedPreferences pref = getSharedPreferences(MainActivity.SCHOOL_ID_KEY, MODE_PRIVATE);
                int idSchool = pref.getInt(MainActivity.SCHOOL_ID_KEY, 0);
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS rowcount FROM eventsdb WHERE idSchool="+idSchool);

                rs.next();
                oldCount = rs.getInt("rowcount");

                stmt.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null)
                        stmt.close();
                } catch (SQLException se2) {
                }// nothing we can do
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // effectue les processus post-connexion
            Log.i(TAG, "Connection Database Checked");
            System.out.print("La table a : " + oldCount);
            SharedPreferences prefs = getSharedPreferences("DB", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("NB_EVENT", oldCount);
            editor.putBoolean("FIRST_CO", false);
            editor.apply();
        }
    }

    class Poutine extends AsyncTask<Void,Void,Void> {
        Connection conn = null;
        Statement stmt = null;

        @Override
         protected Void doInBackground(Void... voids){
            try {
                SharedPreferences pref = getSharedPreferences(MainActivity.SCHOOL_ID_KEY, MODE_PRIVATE);
                int idSchool = pref.getInt(MainActivity.SCHOOL_ID_KEY, 0);
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS rowcount FROM eventsdb WHERE idSchool="+idSchool);

                rs.next();
                newCount = rs.getInt("rowcount");

                stmt.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stmt != null)
                        stmt.close();
                } catch (SQLException se2) {
                }// nothing we can do
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            return null;
        }

         @Override
         protected void onPostExecute(Void aVoid) {

            // effectue les processus post-connexion
             Log.i(TAG, "Connection Database Checked");
             System.out.print("La table a : " + newCount);

             SharedPreferences prefs = getSharedPreferences("DB", MODE_PRIVATE);
             int nb_event = prefs.getInt("NB_EVENT", 0);

             if(nb_event == newCount){
                 //ils sont égaux, donc on fait rien.

             }
             else if(newCount > nb_event)
             {

                 //Si les deux variables sont différentes on fait vibrer les téléphone et on affiche une notification.

                 Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                 // Vibrate for 500 milliseconds
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                     v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                 } else {
                     //deprecated in API 26
                     v.vibrate(500);
                 }

                 // Create an explicit intent for an Activity in your app
                 Intent intent = new Intent(HelloService.this, MainActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 PendingIntent pendingIntent = PendingIntent.getActivity(HelloService.this, 0, intent, 0);

                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                     int importance = NotificationManager.IMPORTANCE_DEFAULT;
                     NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "un service fantastique produit par une personne fantastique ! :D", importance);
                     channel.setDescription("Une description, quoi donc écrire ? ");
                     // Register the channel with the system; you can't change the importance
                     // or other notification behaviors after this
                     NotificationManager notificationManager = getSystemService(NotificationManager.class);
                     notificationManager.createNotificationChannel(channel);
                 }

                 NotificationCompat.Builder builder = new NotificationCompat.Builder(HelloService.this, CHANNEL_ID)
                         .setSmallIcon(R.drawable.text)
                         .setContentTitle("Un nouvel article vient d'être publié ! ")
                         .setStyle(new NotificationCompat.BigTextStyle()
                                 .bigText("Double tapez pour être redirigé vers l'application."))
                         .setContentIntent(pendingIntent)
                         .setPriority(NotificationCompat.PRIORITY_DEFAULT);



                 NotificationManagerCompat notificationManager = NotificationManagerCompat.from(HelloService.this);

                 // notificationId is a unique int for each notification that you must define
                 notificationManager.notify(1, builder.build());

             }



             SharedPreferences preferences = getSharedPreferences("DB", MODE_PRIVATE);
             SharedPreferences.Editor edit = preferences.edit();
             edit.putInt("NB_EVENT", newCount);
             edit.apply();






         }
     }
}
