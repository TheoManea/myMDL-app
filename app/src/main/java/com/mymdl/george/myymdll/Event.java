package com.mymdl.george.myymdll;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Parcelable {
    // Event simple
    private int idEvent, idClubLinked, nb_answer1, nb_answer2;
    private String title, detail, titleClubLinked;
    private Date dayOfCreation, dayOfEvent, dayEndOfEvent;

    // Event Survey
    String questionSurvey, answer1, answer2;

    transient SimpleDateFormat format = new SimpleDateFormat();

    // constructeur
    public Event(){format.applyPattern(MainActivity.SHOWN_DATE);}

    // constructeur
    public Event(int idEvent, String title, String detail, String titleClubLinked, int idClubLinked) {
        this.idEvent = idEvent;
        this.title = title;
        this.detail = detail;
        this.titleClubLinked = titleClubLinked;
        this.idClubLinked = idClubLinked;

        format.applyPattern(MainActivity.SHOWN_DATE);
    }

    // constructeur
    public Event(int idEvent, String title, String questionSurvey, String answer1, String answer2) {
        this.idEvent = idEvent;
        this.title = title;
        this.questionSurvey = questionSurvey;
        this.answer1 = answer1;
        this.answer2 = answer2;
    }

    protected Event(Parcel in) {
        // Dans l'ordre
        idEvent = in.readInt();
        title = in.readString();
        nb_answer1 = in.readInt();
        nb_answer2 = in.readInt();
        try {
            format.applyPattern(MainActivity.UNI_DATE);
            dayOfCreation = format.parse(in.readString());
            format.applyPattern(MainActivity.UNI_DATE_TIME);
            dayOfEvent = format.parse(in.readString());
            dayEndOfEvent = format.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        detail = in.readString();
        titleClubLinked = in.readString();
        idClubLinked = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    //Getter
    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public String getTitleClubLinked() {
        return titleClubLinked;
    }

    public int getNb_answer1() {
        return nb_answer1;
    }

    public int getNb_answer2() {
        return nb_answer2;
    }

    public Date getDayOfCreation() {
        return dayOfCreation;
    }

    public Date getDayOfEvent() {
        return dayOfEvent;
    }

    public Date getDayEndOfEvent() {
        return dayEndOfEvent;
    }

    public String getStringDayOfCreation() {
        format.applyPattern(MainActivity.SHOWN_DATE);
        return format.format(dayOfCreation);
    }

    public String getStringDayOfEvent() {
        format.applyPattern(MainActivity.SHOWN_DATE_TIME);
        return format.format(dayOfEvent);
    }

    public String getStringDayEndOfEvent() {
        format.applyPattern(MainActivity.SHOWN_DATE_TIME);
        return format.format(dayEndOfEvent);
    }

    public String getStringUniversalDayOfCreation() {
        format.applyPattern(MainActivity.UNI_DATE);
        return format.format(dayOfCreation);
    }

    public String getStringUniversalDayOfEvent() {
        format.applyPattern(MainActivity.UNI_DATE_TIME);
        return format.format(dayOfEvent);
    }

    public String getStringUniversalDayEndOfEvent() {
        format.applyPattern(MainActivity.UNI_DATE_TIME);
        return format.format(dayEndOfEvent);
    }

    public int getIdClubLinked() {
        return idClubLinked;
    }

    public String getQuestionSurvey() {
        return questionSurvey;
    }

    public String getAnswer1() {
        return answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getRelativeDayOfEvent() {
        return DateAbsToRel(dayOfEvent);
    }

    public String getRelativeDayEndOfEvent() {
        return DateAbsToRel(dayEndOfEvent);
    }

    // Setter
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setTitleClubLinked(String titleClubLinked) {
        this.titleClubLinked = titleClubLinked;
    }

    public void setIdClubLinked(int idClubLinked) {
        this.idClubLinked = idClubLinked;
    }

    public void setNb_answer1(int nb_answer1) {
        this.nb_answer1 = nb_answer1;
    }

    public void setNb_answer2(int nb_answer2) {
        this.nb_answer2 = nb_answer2;
    }

    public void setDayOfCreation(Date dayOfCreation) {
        this.dayOfCreation = dayOfCreation;
    }

    public void setDayOfEvent(Date dayOfEvent) {
        this.dayOfEvent = dayOfEvent;
    }

    public void setDayEndOfEvent(Date dayEndOfEvent) {
        this.dayEndOfEvent = dayEndOfEvent;
    }

    public void setQuestionSurvey(String questionSurvey) {
        this.questionSurvey = questionSurvey;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }


    //methodes utiles
    public String DateAbsToRel(Date universalDate) {
        //  Si il y a plus de 1 jours -> renvoyer 'x jours'
        // sinon si l'heure  >= 1 renvoyer 'x heures' ...

        long diff =universalDate.getTime() - new Date().getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days/ 30;

        if (months >= 12) {
            return "dans "+months/12+" année(s)";
        } else if (months >= 1) {
            return "dans "+months+" mois";
        } else if (days >= 1) {
            return "dans "+days+" jour(s)";
        } else if (hours >= 1) {
            return "dans "+hours+" heure(s)";
        } else if(minutes >= 1) {
            return "dans "+minutes+" minute(s)";
        } else {
            return "maintenant";
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        // Dans l'ordre
        parcel.writeInt(idEvent);
        parcel.writeString(title);
        parcel.writeInt(nb_answer1);
        parcel.writeInt(nb_answer2);
        format.applyPattern(MainActivity.UNI_DATE);
        parcel.writeString(format.format(dayOfCreation));
        format.applyPattern(MainActivity.UNI_DATE_TIME);
        parcel.writeString(format.format(dayOfEvent));
        parcel.writeString(format.format(dayEndOfEvent));
        parcel.writeString(detail);
        parcel.writeString(titleClubLinked);
        parcel.writeInt(idClubLinked);
    }
}
