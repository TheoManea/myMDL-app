package com.mymdl.george.myymdll;


import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mymdl.george.myymdll.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CardAdapterSchool extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<School> listOfItems;
    private Context mContext;

    CardAdapterSchool(List listOfItems) {
        this.listOfItems = listOfItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view;
        view = layoutInflater.inflate(R.layout.school_item, parent, false);
        return new CardHolderSchool(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((CardHolderSchool) viewHolder).Display(listOfItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return listOfItems.size();
    }



    class CardHolderSchool extends RecyclerView.ViewHolder{
        TextView mNameSchool;
        Button evtSideBtn;
        CardView cardViewSchool;
        Context context;

        CardHolderSchool(View itemView) {
            super(itemView);

            mNameSchool = itemView.findViewById(R.id.nameSchoolTv);

            context = itemView.getContext();
        }

        void Display(final School school, int position) {

            mNameSchool.setText(school.getNameSchool());
            cardViewSchool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mettre les paramètres pour que les cases soient pré-remplis
                    /*Intent intent = new Intent(context,AddEventActivity.class);
                    intent.putExtra("event", event);
                    intent.putExtra("idAuthor", String.valueOf(idAuthor));
                    context.startActivity(intent);*/
                }
            });
            evtSideBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Confirmer ?");
                    alertDialogBuilder.setPositiveButton("Oui",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    // supprimer l'event dans la base de données
                                    /*new deleteItem(event).execute();
                                    // supprime l'event dans la liste affiché
                                    listOfItems.remove(getAdapterPosition());
                                    int position = getAdapterPosition();
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, listOfItems.size());


                                    Toast.makeText(context,"Evenement supprimé ! ",Toast.LENGTH_LONG).show();*/
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
        }
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

                stmt.executeUpdate("DELETE FROM schooldb WHERE idEvent=" + event.getIdEvent());

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
}
