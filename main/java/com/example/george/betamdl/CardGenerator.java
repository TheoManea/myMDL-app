package com.mymdl.george.myymdll;

import java.util.ArrayList;
import java.util.List;

public class CardGenerator {

    List<Event> listEventNow;
    List<Event> listEventFuture;


    public List<Event> nowEvents(int howMany) {
        //Requête à la base de données EventDb
        //appeler deleteOldRededEvent
        //par date, déjà lu, et seulement 5
        //afficher infos dans cadre|| donner liste<Event>

        //Simulation d'interation à la base de données
        listEventNow = new ArrayList<>();
        for(int i=1; i<=howMany; i++) {
            listEventNow.add(new Event(i, "Ce titre n'est en aucun cas une boutade "+i,
                    i+"Voici les détails d'une très longue histoire que je vais, " +
                            "si l'envie m'en prend, vous conter. Je vais juste manger des macarons !",
                    "Club A des évènements actuels", 1));
        }
        return listEventNow;
    }

    public List<Event> futureEvents(int howMany) {
        //Requête base de données EventDb
        //par dates ultèrieurs, par ordre des plus récents

        //Simulation d'interation à la base de données
        listEventFuture = new ArrayList<>();
        for(int i=1; i<=howMany; i++) {
            listEventFuture.add(new Event(i+5,"Titre "+i+" fut", "detail "+i+" fut", "Club B", 2));
        }
        return listEventFuture;
    }

    public void deleteEvent() {
        //suppression d'evenements précis via son id -> EventDb
    }

    public void deleteOldReadedEvent() {
        //deleteOldReadedEvent
        //regarder les evenements supprimés de la base de données EventDb
        //supprimer les trop vieux ??
        //retirer du tel les id des infos déjà lu qui ne sont plus dans EventDb
    }
}
