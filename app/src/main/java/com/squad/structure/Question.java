package com.squad.structure;

public class Question {

    public Question(String question, Options options, boolean isCompleted) {
        this.question = question;
        this.options = options;
        this.isCompleted = isCompleted;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public Integer[] getResponses() {
        return responses;
    }

    public void setResponses(Integer[] responses) {
        this.responses = responses;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    private Integer[] responses = new Integer[2];
    private boolean isCompleted = false;
    private String question;
    private Options options;
}
