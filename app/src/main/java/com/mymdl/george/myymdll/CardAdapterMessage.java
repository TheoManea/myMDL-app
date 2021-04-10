package com.mymdl.george.myymdll;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mymdl.george.myymdll.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CardAdapterMessage extends RecyclerView.Adapter<CardAdapterMessage.CardHolderMessage>{

    private List<Message> messageList;

    CardAdapterMessage(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public CardAdapterMessage.CardHolderMessage onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view;
        view = layoutInflater.inflate(R.layout.message_card, parent, false);
        return new CardAdapterMessage.CardHolderMessage(view);
    }

    @Override
    public void onBindViewHolder(CardAdapterMessage.CardHolderMessage cardHolderMessage, int position) {
        cardHolderMessage.Display(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class CardHolderMessage extends RecyclerView.ViewHolder {

        private TextView author, messageTxt;
        private CardView cardView;

        private Context context;

        public CardHolderMessage(View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.authorMsg);
            messageTxt = itemView.findViewById(R.id.textMsg);
            cardView = itemView.findViewById(R.id.cardViewMsg);

            context = itemView.getContext();
        }

        private void Display(final Message message) {

            author.setText(message.getNameAuhtor());
            messageTxt.setText(message.getMessage());

            // quand on clique sur une card
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mettre les paramètres pour que les cases soient pré-remplis
                    /*Intent intent = new Intent(context,ShowMsg.class);
                    intent.putExtra("message", message);
                    context.startActivity(intent);*/
                }
            });

            // quand on glisse vers la gauche -> supprimer

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
