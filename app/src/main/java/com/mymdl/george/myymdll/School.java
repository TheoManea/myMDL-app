package com.mymdl.george.myymdll;

public class School {
    int idSchool;
    String nameSchool;

    // constructeur
    public School(int idSchool, String nameSchool) {
        this.idSchool = idSchool;
        this.nameSchool = nameSchool;
    }

    // getter
    public int getIdSchool() {
        return idSchool;
    }

    public String getNameSchool() {
        return nameSchool;
    }
}
