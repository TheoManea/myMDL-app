package com.mymdl.george.myymdll;

public class Message {
    private int id, idAuthor;
    private String message, nameAuhtor;

    // setter
    public void setNameAuhtor(String nameAuhtor) {
        this.nameAuhtor = nameAuhtor;
    }


    //getter
    public String getMessage() {
        return message;
    }

    public String getNameAuhtor() {
        return nameAuhtor;
    }
}
