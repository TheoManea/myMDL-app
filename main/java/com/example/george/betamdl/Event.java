package com.mymdl.george.myymdll;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private int idEvent;
    private String title;
    private String detail;
    private String nameClubLinked;
    private int idClubLinked;
    //private Date dateOfEvent;
    //Dates ...

    public Event(int idEvent, String title, String detail, String nameClubLinked, int idClubLinked) {
        this.idEvent = idEvent;
        this.title = title;
        this.detail = detail;
        this.nameClubLinked = nameClubLinked;
        this.idClubLinked = idClubLinked;
    }

    protected Event(Parcel in) {
        idEvent = in.readInt();
        title = in.readString();
        detail = in.readString();
        nameClubLinked = in.readString();
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

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public String getNameClubLinked() {
        return nameClubLinked;
    }

    public int getIdClubLinked() {
        return idClubLinked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(idEvent);
        parcel.writeString(title);
        parcel.writeString(detail);
        parcel.writeString(nameClubLinked);
        parcel.writeInt(idClubLinked);
    }
}
