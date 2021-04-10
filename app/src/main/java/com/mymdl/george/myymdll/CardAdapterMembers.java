package com.mymdl.george.myymdll;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mymdl.george.myymdll.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CardAdapterMembers extends RecyclerView.Adapter<CardAdapterMembers.CardHolderMember> {

    List<User> userList;
    int idAuthor;

    // constructeur
    CardAdapterMembers(int idAuthor, List<User> userList) {
        this.userList = userList;
        this.idAuthor = idAuthor;
    }

    @Override
    public CardHolderMember onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view;
        view = layoutInflater.inflate(R.layout.user_item, parent, false);
        return new CardHolderMember(view);
    }

    @Override
    public void onBindViewHolder(CardHolderMember cardHolderMember, int position) {
        cardHolderMember.Display(userList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class CardHolderMember extends RecyclerView.ViewHolder {

        private TextView userNameTv;
        private TextView userFNameTv;
        private TextView userLvlAccTv;
        private TextView userPassTv;
        private Button userSideBtn;
        private CardView userCard;
        private Context context;

        public CardHolderMember(View itemView) {
            super(itemView);

            userNameTv = itemView.findViewById(R.id.userNameTv);
            userFNameTv = itemView.findViewById(R.id.userFNameTv);
            userLvlAccTv = itemView.findViewById(R.id.userLvlAccTv);
            userPassTv = itemView.findViewById(R.id.userPassTv);
            userSideBtn = itemView.findViewById(R.id.userSideBtn);
            userCard = itemView.findViewById(R.id.userCard);

            context = itemView.getContext();
        }

        private void Display(final User user, int position) {

            userNameTv.setText(user.getNameUser());
            userFNameTv.setText(user.getFamilyNameUser());
            userLvlAccTv.setText("Niveau " +String.valueOf(user.getLevelAccess()));
            userPassTv.setText("mot de passe : *****" /*+user.getPasswordUser()*/);

            // quand on clique sur une card
            userCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mettre les paramètres pour que les cases soient pré-remplis
                    Intent intent = new Intent(context,AddUserActivity.class);
                    intent.putExtra("user", user);
                    intent.putExtra("idAuthor", String.valueOf(idAuthor));
                    context.startActivity(intent);
                }
            });

            // quand on clique sur la poubelle rouge
            userSideBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Confirmer ?");
                    alertDialogBuilder.setPositiveButton("Oui",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    new deleteItem(user).execute();
                                    userList.remove(getAdapterPosition());
                                    int position = getAdapterPosition();
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, userList.size());


                                    Toast.makeText(context,"Utilisateur supprimé ! ",Toast.LENGTH_LONG).show();
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

            float factor = context.getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, (int)(57*factor));

            // si c'est la fin, on laisse un peu d'espace à la fin pour le confort
            if (position == userList.size() -1) {

                layoutParams.setMargins((int)(5*factor), (int)(5*factor), (int)(5*factor), (int)(80*factor));
                userCard.setLayoutParams(layoutParams);
            }
        }
    }

    class deleteItem extends AsyncTask<Void,Void,Void> {
        Connection conn;
        Statement stmt;

        private User user;

        public deleteItem(User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(MainActivity.DB_URL, MainActivity.USER, MainActivity.PASS);
                stmt = conn.createStatement();

                stmt.executeUpdate("DELETE FROM accountdb WHERE idUser=" + user.getIdUser());

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