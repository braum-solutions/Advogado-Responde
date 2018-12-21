package com.braumsolutions.advogadoresponde.Model;

public class CommentModel {

    String lawyer, comment;

    public CommentModel() {

    }

    public String getLawyer() {
        return lawyer;
    }

    public void setLawyer(String lawyer) {
        this.lawyer = lawyer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
