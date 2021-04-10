package com.mymdl.george.myymdll;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.mymdl.george.myymdll.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Event> listOfItems;
    public static int SPECTATOR_MODE = 0, EDIT_MODE = 1,
            TYPE_EVENT = 0, TYPE_SURVEY = 1;
    private int mode, idAuthor = 0, howManyEvtNow = 0;
    private boolean activiateCornerAcDate = false;
    private Context context;
    private  CardView survey;


    //  SPECTATOR MODE
    // le boolean à la fin permet de faire la difference avec l'autre methode (EDIT MODE) -> necessaire ? Je sais pas
    // si pas de démarcation entre event maintenant et futur -> howmanyevtnow = 0
    // activiateCornerAcDate : activate corner raduis acording to howManyEvtNow (des Date)
    CardAdapter(List listEvents, int mode, int howManyEvtNow, boolean activiateCornerAcDate) {
        this.listOfItems = listEvents;
        this.mode = mode;
        this.howManyEvtNow = howManyEvtNow;
        this.activiateCornerAcDate = activiateCornerAcDate;
    }

    //  EDIT MODE
    CardAdapter(List listEvents, int mode, int idAuthor) {
        this.listOfItems = listEvents;
        this.mode = mode;
        this.idAuthor = idAuthor;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view;

        context = parent.getContext();

        // Mode d'affichage pour un user lambda
        if (mode == SPECTATOR_MODE) {
            if (ViewType == TYPE_EVENT) {
                view = layoutInflater.inflate(R.layout.event_item, parent, false);
                return new CardHolderEvent(view);
            } else /*if (ViewType == TYPE_SURVEY)*/ {
                view = layoutInflater.inflate(R.layout.card_survey, parent, false);
                survey = view.findViewById(R.id.cardViewSurvey);
                // trouver l'id de la card

                return new CardHolderSurvey(view);
            }
        // Mode d'affichage pour un admin (avec la possibilité de modifier défoi)
        } else {
            if (ViewType == TYPE_EVENT) {
                view = layoutInflater.inflate(R.layout.event_item_admin, parent, false);
                return new CardHolderEvent(view);
            }
            else /*if (ViewType == TYPE_SURVEY)*/ {
                view = layoutInflater.inflate(R.layout.card_survey_admin, parent, false);
                survey = view.findViewById(R.id.cardViewSurvey);
                // trouver l'id de la card

                return new CardHolderSurvey(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_EVENT) {
            ((CardHolderEvent) viewHolder).Display(listOfItems.get(position), position);
        } else {
            ((CardHolderSurvey) viewHolder).Display(listOfItems.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return listOfItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(String.valueOf(listOfItems.get(position).getQuestionSurvey())) || listOfItems.get(position).getQuestionSurvey() == null) {
            return TYPE_EVENT;
        } else {
            return TYPE_SURVEY;
        }
    }



    class CardHolderEvent extends RecyclerView.ViewHolder{
        TextView mTitle, mDetail, mEvtDate;
        Button evtSideBtn;
        CardView cardView;
        Context context;
        View separator;

        CardHolderEvent(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.titleEvtTv);
            mDetail = itemView.findViewById(R.id.detailEvtTv);
            mEvtDate = itemView.findViewById(R.id.dayEvtTv);
            if (mode == EDIT_MODE) {
                evtSideBtn = itemView.findViewById(R.id.evtSideBtn);
            }
            cardView = itemView.findViewById(R.id.cardViewEvtNow);
            separator = itemView.findViewById(R.id.separatorCardEvt);

            context = itemView.getContext();
        }

        void Display(final Event event, int position) {

            mTitle.setText(event.getTitle());
            //"Pos :"+position+ "\n adpaPos :"+getAdapterPosition()+ "\n"+
            mDetail.setText(event.getDetail());
            if (activiateCornerAcDate) {
                mEvtDate.setText(event.getRelativeDayOfEvent());
            } else
            {
                mEvtDate.setText(event.getStringDayOfEvent());
            }


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mode == SPECTATOR_MODE)  {
                        Intent intent = new Intent(context,EventDetailActivity.class);
                        intent.putExtra("event", event);
                        context.startActivity(intent);
                        //Toast.makeText(context,"Pos" +position+" ApdaptPos :"+getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    } else if (mode == EDIT_MODE) {
                        // mettre les paramètres pour que les cases soient pré-remplis
                        Intent intent = new Intent(context,AddEventActivity.class);
                        intent.putExtra("event", event);
                        intent.putExtra("idAuthor", String.valueOf(idAuthor));
                        context.startActivity(intent);
                    }
                }
            });
            if (mode == EDIT_MODE) {
                evtSideBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mode == EDIT_MODE) {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setMessage("Confirmer ?");
                            alertDialogBuilder.setPositiveButton("Oui",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            // supprimer l'event dans la base de données
                                            new deleteItem(event).execute();
                                            // supprime l'event dans la liste affiché
                                            listOfItems.remove(getAdapterPosition());
                                            int position = getAdapterPosition();
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, listOfItems.size());


                                            Toast.makeText(context,"Evenement supprimé ! ",Toast.LENGTH_LONG).show();
                                        }
                                    });

                            alertDialogBuilder.setNegativeButton("Non",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ;
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                });
            }
            else {
                cornerOrNot(position, cardView);
                separatorOrNot(position, separator);
            }
        }
    }


    class CardHolderSurvey extends RecyclerView.ViewHolder {
        TextView mTitle, mQuestion, mDateEndSurvey, thanksSurvey, per_rep_1, per_rep_2, nb_rep, answer1Tv, answer2Tv;
        Button mAnswer1, mAnswer2, deleteSurveyBtn;
        CardView cardView;
        Context context;
        String one_or_two;
        TableLayout bodySurvey;
        TextView percentBarSurveyView;
        View separator;

        CardHolderSurvey(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.titleSurvey);
            mQuestion = itemView.findViewById(R.id.question);
            mDateEndSurvey = itemView.findViewById(R.id.dayEndSurvey);
            thanksSurvey = itemView.findViewById(R.id.thanksSurvey);
            bodySurvey = itemView.findViewById(R.id.bodySurvey);
            separator = itemView.findViewById(R.id.separatorCardEvt);


            if (mode != SPECTATOR_MODE) {
                per_rep_1 = itemView.findViewById(R.id.per_rep_1);
                per_rep_2 = itemView.findViewById(R.id.per_rep_2);
                deleteSurveyBtn = itemView.findViewById(R.id.deleteSurveyBtn);
                nb_rep = itemView.findViewById(R.id.nb_rep);
                percentBarSurveyView = itemView.findViewById(R.id.percentBarSurveyV);
                answer1Tv = itemView.findViewById(R.id.answer1Tv);
                answer2Tv = itemView.findViewById(R.id.answer2Tv);
            } else {
                mAnswer1 = itemView.findViewById(R.id.answer1);
                mAnswer2 = itemView.findViewById(R.id.answer2);
            }

            cardView = itemView.findViewById(R.id.cardViewSurvey);
            context = itemView.getContext();

        }

        void Display(final Event event, int position) {
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern(MainActivity.SHOWN_DATE);

            mTitle.setText(event.getTitle());
            mQuestion.setText(event.getQuestionSurvey());
            if (activiateCornerAcDate) {
                mDateEndSurvey.setText("Fin: " + event.getRelativeDayEndOfEvent());
            } else
            {
                mDateEndSurvey.setText("Fin: " + format.format(event.getDayEndOfEvent()));
            }


            if (mode == SPECTATOR_MODE) {
                mAnswer1.setText(event.getAnswer1());
                mAnswer2.setText(event.getAnswer2());
                cornerOrNot(position, cardView);
                separatorOrNot(position, separator);

                // listeners
                mAnswer1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        one_or_two = "nb_rep_1";
                        new UpdateLikes( one_or_two, event.getIdEvent()).execute();




                        bodySurvey.setVisibility(View.GONE);
                        thanksSurvey.setVisibility(View.VISIBLE);
                        saveSurvey(context, event.getIdEvent());
                    }
                });

                mAnswer2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        one_or_two = "nb_rep_2";
                        new UpdateLikes( one_or_two, event.getIdEvent()).execute();

                        bodySurvey.setVisibility(View.GONE);
                        thanksSurvey.setVisibility(View.VISIBLE);
                        saveSurvey(context, event.getIdEvent());
                    }
                });
            } else {
                answer1Tv.setText(event.getAnswer1());
                answer2Tv.setText(event.getAnswer2());
                // nb total / 2 pourcentages / bouton / view
                int total_rep = event.getNb_answer1() + event.getNb_answer2();
                nb_rep.setText(total_rep + " réponse(s)");
                // attention division par 0
                if (total_rep == 0)
                    total_rep = 1;

                int rep_1 = (int)((float)event.getNb_answer1() / (float)total_rep * 100);
                int rep_2 = (int)((float)event.getNb_answer2() / (float)total_rep * 100);
                per_rep_1.setText(rep_1 + "%");
                per_rep_2.setText(rep_2 + "%");

                deleteSurveyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage("Confirmer ?");
                        alertDialogBuilder.setPositiveButton("Oui",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        // supprimer l'event dans la base de données
                                        new deleteItem(event).execute();
                                        // supprime l'event dans la liste affiché
                                        listOfItems.remove(getAdapterPosition());
                                        int position = getAdapterPosition();
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, listOfItems.size());


                                        Toast.makeText(context,"Evenement supprimé ! ",Toast.LENGTH_LONG).show();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("Non",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ;
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });

                /// PREMIER ESSAIE DE DEGRADE
                /*// changer le dégradé du View (pour les pourcentages)
                // setGradientCenter(x, y) ne marche pas que des dégradés linéaires ! => mettres le nombres de couleur
                // en fonction du pourcentage
                //{Color.parseColor("#B5144F"),Color.parseColor("#F8A913")};
                int divisionColors = (int)(rep_1/20);
                int[] colors_rep1 = new int[divisionColors];
                int[] colors_rep2 = new int[5-divisionColors];
                Arrays.fill(colors_rep1, Color.parseColor("#B5144F"));
                Arrays.fill(colors_rep2, Color.parseColor("#F8A913"));
                int len1 = colors_rep1.length, len2 = colors_rep2.length;
                int[] colors = new int[len1 + len2];
                System.arraycopy(colors_rep1, 0, colors, 0, len1);
                System.arraycopy(colors_rep2, 0, colors, len1, len2);

                //{Color.parseColor("#B5144F"),Color.parseColor("#F8A913")};

                //create a new gradient color
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT, colors);

                percentBarSurveyView.setBackground(gd);*/

                /////////////////////////////

                //create a new gradient color
                GradientDrawable gd = new GradientDrawable();

                gd.setColors(new int[]{
                        Color.parseColor("#B5144F"),
                        Color.parseColor("#F8A913")
                });

                gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                gd.setGradientCenter(0,0);
                // factor because height of LayoutParams is in pixel
                float factor = context.getResources().getDisplayMetrics().density;
                // si personne n'a encore voté
                if (event.getNb_answer1() + event.getNb_answer2() == 0) {
                    gd.setGradientRadius(50*3.5f*factor);
                } else {
                    gd.setGradientRadius(rep_1*3.5f*factor);
                }
                // Set GradientDrawable width and in pixels
                gd.setSize((int)(200*factor), (int)(200*factor)); // Width 400 pixels and height 100 pixels
                gd.setCornerRadius(15f*factor);
                percentBarSurveyView.setBackground(gd);



                /// ANCIEN 'CHART'

                /*//Définis un array pour la data
                ArrayList<BarEntry> value = new ArrayList<>();
                BarEntry nb_rep_1_chart = new BarEntry(1, Integer.parseInt(event.getAnswer1()));
                value.add(nb_rep_1_chart);
                BarEntry nb_rep_2_chart = new BarEntry(2, Integer.parseInt(event.getAnswer2()));
                value.add(nb_rep_2_chart);


                BarDataSet set1;
                set1 = new BarDataSet(value, "The year 2017");

                set1.setColors(Color.parseColor("#F78B5D"), Color.parseColor("#FCB232"), Color.parseColor("#FDD930"), Color.parseColor("#ADD137"), Color.parseColor("#A0C25A"));

                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);

                BarData data = new BarData(dataSets);

                // hide Y-axis
                YAxis left = chart.getAxisLeft();
                left.setDrawLabels(false);

                chart.setData(data);

                // custom description
                Description description = new Description();
                description.setText("Statistiques");
                chart.setDescription(description);

                // hide legend
                chart.getLegend().setEnabled(false);

                chart.animateY(1000);
                chart.invalidate();*/
            }
        }


    }



    private void cornerOrNot (int position, CardView cardView) {


        float cornerRadius = 15f; // en dp
        // factor because height of LayoutParams is in pixel
        float factor = context.getResources().getDisplayMetrics().density;

        /// permet d'arrondir les coins
        // les conditions
        boolean isFirst = position == 0,
                isFirstTomorrow = (position == howManyEvtNow && activiateCornerAcDate),
                isLastToday = (position == howManyEvtNow-1 && activiateCornerAcDate),
                isLastTomorrow = position == listOfItems.size()-1;

        // s'il est seul dans sa catégorie -> agit sur les 2 coins
        if (((isFirst && isLastToday) || (isFirstTomorrow && isLastTomorrow)) && activiateCornerAcDate) {

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, (int)(factor*(cornerRadius+100)));

            int bott = 0;
            if (activiateCornerAcDate && isLastTomorrow) {
                bott = 50;
            }
            layoutParams.setMargins((int)(3*factor), 0, (int)(3*factor), (int)(bott*factor));
            cardView.setRadius(cornerRadius*factor);
            //cardView.setContentPadding(0 ,(int)(50*factor), 0, (int)(50*factor));

            cardView.setLayoutParams(layoutParams);
            return;
        }
        // coins sup
        if (isFirst || isFirstTomorrow) {
            //cardView.setBackgroundColor(context.getResources().getColor(R.color.greenfmdl));

            // si la liste est de 1 éléments -> juste mettre les corner
            if (listOfItems.size() > 1) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, (int) (factor * (cornerRadius + 100)));
                layoutParams.setMargins((int) (3 * factor), 0, (int) (3 * factor), -(int) (cornerRadius * factor));
                cardView.setLayoutParams(layoutParams);
            }

            cardView.setRadius(cornerRadius*factor);
        }// coins inf
        else if (isLastToday || isLastTomorrow) {
            //cardView.setBackgroundColor(context.getResources().getColor(R.color.redfmdl));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, (int)(factor*(cornerRadius+100)));

            // sur le mainactivity (activateCornerAcData), si c'est la fin, on laisse un peu d'espace à la fin pour le confort
            int bott = 0;
            if (activiateCornerAcDate && isLastTomorrow) {
                bott = 50;
            }
            layoutParams.setMargins((int)(3*factor), - (int)(cornerRadius*factor), (int)(3*factor), (int)(bott*factor));
            cardView.setRadius(cornerRadius*factor);
            cardView.setContentPadding(0 ,(int)(cornerRadius*factor), 0, 0);

            cardView.setLayoutParams(layoutParams);
        }
    }

    private void separatorOrNot(int position, View separator) {

        boolean isLastToday = (position == howManyEvtNow-1 && activiateCornerAcDate),
                isLastTomorrow = position == listOfItems.size()-1;

        /// permet d'afficher ou non les separator
        if ((isLastToday && activiateCornerAcDate) || isLastTomorrow) {
            separator.setVisibility(View.INVISIBLE);
        }
    }

    private void saveSurvey(Context context, int idSurvey) {
        // load
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.SURVEY_CHECKED_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonSurvey = sharedPreferences.getString(MainActivity.LIST_SURVEY_KEY, null);
        Type typeList = new TypeToken<ArrayList<String>>() {}.getType();
        List<Integer> surveyList = gson.fromJson(jsonSurvey, typeList);
        if (surveyList == null)
            surveyList = new ArrayList<>();


        // ajout de l'id
        surveyList.add(idSurvey);


        // save
        SharedPreferences.Editor editor = sharedPreferences.edit();
        jsonSurvey = gson.toJson(surveyList);
        editor.putString(MainActivity.LIST_SURVEY_KEY, jsonSurvey);
        editor.apply();
    }



    class deleteItem extends AsyncTask<Void,Void,Void>{
        Connection conn;
        Statement stmt;

        private Event event;

        public deleteItem(Event event) {
            this.event = event;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();

                stmt.executeUpdate("DELETE FROM eventsdb WHERE idEvent=" + event.getIdEvent());

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
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(Void aVoid) { }
    }

    class UpdateLikes extends  AsyncTask<Void,Void,Void>{

        Connection conn;
        Statement stmt;

        private String one_or_two;
        private int  idEvent;



        public UpdateLikes(String one_or_two, int idEvent) {

            this.one_or_two = one_or_two;
            this.idEvent = idEvent;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();



                ResultSet rs = stmt.executeQuery("SELECT " + one_or_two + " FROM eventsdb WHERE idEvent=" + idEvent);
                rs.next();
                int nb = rs.getInt(one_or_two);


                nb = nb + 1;

                stmt.executeUpdate("UPDATE eventsdb SET " + one_or_two + "=" + nb + " WHERE idEvent=" + idEvent );


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
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(Void aVoid) {









        }

    }
}
