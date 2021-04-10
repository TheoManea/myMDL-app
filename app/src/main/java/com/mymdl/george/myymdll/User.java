package com.mymdl.george.myymdll;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String nameUser, familyNameUser, passwordUser, email;
    private int idUser, levelAccess, idClub, idSchool, idRegion;

    // contructeurs
    public User(int idUser, String nameUser, String familyNameUser, String passwordUser, int levelAccess, String email) {
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.familyNameUser = familyNameUser;
        this.passwordUser = passwordUser;
        this.levelAccess = levelAccess;
        this.email = email;
    }

    protected User(Parcel in) {
        idUser = in.readInt();
        nameUser = in.readString();
        familyNameUser = in.readString();
        email = in.readString();
        passwordUser = in.readString();
        levelAccess = in.readInt();
        idClub = in.readInt();
        idSchool = in.readInt();
        idRegion = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // getter
    public int getIdUser() {
        return idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public String getFamilyNameUser() {
        return familyNameUser;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public int getLevelAccess() {
        return levelAccess;
    }

    public int getIdClub() {
        return idClub;
    }

    public int getIdSchool() {
        return idSchool;
    }

    public int getIdRegion() {
        return idRegion;
    }

    public String getEmail() {
        return email;
    }

    // setter
    public void setIdSchool(int idSchool) {
        this.idSchool = idSchool;
    }

    public void setIdRegion(int idRegion) {
        this.idRegion = idRegion;
    }

    public void setIdClub(int idClub) {
        this.idClub = idClub;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        // dans l'ordre
        parcel.writeInt(idUser);
        parcel.writeString(nameUser);
        parcel.writeString(familyNameUser);
        parcel.writeString(email);
        parcel.writeString(passwordUser);
        parcel.writeInt(levelAccess);
        parcel.writeInt(idClub);
        parcel.writeInt(idSchool);
        parcel.writeInt(idRegion);
    }
}
