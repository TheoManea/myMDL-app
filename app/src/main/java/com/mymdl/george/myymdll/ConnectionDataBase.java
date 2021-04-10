package com.mymdl.george.myymdll;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConnectionDataBase extends AsyncTask<String,Void,List> {
    // paramètres de connexion
    private Connection conn = null;
    private Statement stmt = null;

    // variable de succès
    private List<Event> allEventList;
    private List<Club> allClubList;

    // Variable paramètres
    String query;
    private int modeObjectReturn;
    public static int EVENT_QUERY = 0, CLUB_QUERY = 1;

    // 1 - Implement listeners methods (Callback)
    public interface Listeners {
        void onPreExecute(int modeQuery);
        void doInBackground();
        void onPostExecute(List result, int mode);
    }

    // 2 - Declare callback
    private final WeakReference<Listeners> callback;

    // 3 - Constructor
    public ConnectionDataBase(Listeners callback, String query, int modeObjectReturn){
        this.query = query;
        this.callback = new WeakReference<>(callback);
        this.modeObjectReturn = modeObjectReturn;
    }

    // méthodes d'AsyncTasks
    @Override
    protected void onPreExecute() {
        this.callback.get().onPreExecute(modeObjectReturn);
    }

    @Override
    protected void onPostExecute(List finalListFromDb) {
        this.callback.get().onPostExecute(finalListFromDb, modeObjectReturn);
    }

    @Override
    protected List doInBackground(String... params) {
        allEventList = new ArrayList<>();
        allClubList = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(MainActivity.DB_URL,MainActivity.USER,MainActivity.PASS);
            stmt = conn.createStatement();

            if (modeObjectReturn == EVENT_QUERY) {
                // requête n°1 pour avoir les objects dans eventsdb, sans nameClub
                ResultSet resultSet = stmt.executeQuery(query);
                while (resultSet.next()) {
                    // prend les valeurs depuis la requête
                    int id = resultSet.getInt("idEvent");
                    String title = resultSet.getString("titleEvent");
                    String detail = resultSet.getString("detailsEvent");
                    String titleClub = "Error name";
                    int idClub = resultSet.getInt("idClub");
                    String question = resultSet.getString("question");
                    String answer1 = resultSet.getString("answer1");
                    String answer2 = resultSet.getString("answer2");

                    Date dateOfEvent = resultSet.getTimestamp("dayOfEvent");
                    Date dateOfCreation = resultSet.getDate("dayOfCreation");
                    Date dateEndOfEvent = resultSet.getTimestamp("dayEndOfEvent");

                    Event evt = new Event(id, title, detail, titleClub, idClub);
                    evt.setQuestionSurvey(question);
                    evt.setAnswer1(answer1);
                    evt.setAnswer2(answer2);
                    evt.setDayOfEvent(dateOfEvent);
                    evt.setDayOfCreation(dateOfCreation);
                    evt.setDayEndOfEvent(dateEndOfEvent);

                    allEventList.add(evt);
                }
                //Si 1er requête à fonctionné :
                // requête n°2 pour associé idClub à un nameClub trouvé dans clubdb
                resultSet = stmt.executeQuery("SELECT idClub, titleClub FROM clubdb");
                int objectSetted = 0;
                while (resultSet.next() && allEventList.size() > objectSetted) {
                    int idClub = resultSet.getInt("idClub");
                    String titleClub = resultSet.getString("titleClub");

                    for (Event evtTemp : allEventList) {
                        if (evtTemp.getIdClubLinked() == idClub) {
                            evtTemp.setTitleClubLinked(titleClub);
                            objectSetted++;
                        }
                    }
                }


                resultSet.close();
            } else if (modeObjectReturn == CLUB_QUERY) {
                ResultSet resultSet = stmt.executeQuery(query);
                while (resultSet.next()) {
                    int idClub = resultSet.getInt("idClub");
                    String titleClub = resultSet.getString("titleClub");
                    String detailsClub = resultSet.getString("detailsClub");

                    allClubList.add(new Club(idClub, titleClub, detailsClub));
                }
                resultSet.close();
            }

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
        this.callback.get().doInBackground();
        if (modeObjectReturn == EVENT_QUERY) {
            return allEventList;
        } else {
            return allClubList;
        }
    }
}