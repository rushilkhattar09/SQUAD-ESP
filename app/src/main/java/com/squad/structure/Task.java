package com.squad.structure;

import androidx.annotation.NonNull;

import java.util.List;

public class Task {

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Task(
            boolean isCompleted,
            List<Integer> questions,
            String player1,
            String player2,
            String docID,
            String responsePlayer1,
            String responsePlayer2
    ) {
        this.isCompleted = isCompleted;
        this.questions = questions;
        this.player1 = player1;
        this.player2 = player2;
        this.docID = docID;
        this.responsePlayer1 = responsePlayer1;
        this.responsePlayer2 = responsePlayer2;
    }

    public List<Integer> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Integer> questions) {
        this.questions = questions;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getResponsePlayer1() {
        return responsePlayer1;
    }

    public void setResponsePlayer1(String responsePlayer1) {
        this.responsePlayer1 = responsePlayer1;
    }

    public String getResponsePlayer2() {
        return responsePlayer2;
    }

    public void setResponsePlayer2(String responsePlayer2) {
        this.responsePlayer2 = responsePlayer2;
    }

    private String docID;
    private String player1;
    private String player2;
    private boolean isCompleted = false;
    private List<Integer> questions;

    private String responsePlayer1;
    private String responsePlayer2;

    @NonNull
    @Override
    public String toString() {
        return player1.toString() + "  " + player2.toString() + "  " + questions.toString();
    }

}