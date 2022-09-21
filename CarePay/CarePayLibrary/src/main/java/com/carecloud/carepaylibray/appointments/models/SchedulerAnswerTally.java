package com.carecloud.carepaylibray.appointments.models;

import java.io.Serializable;

public class SchedulerAnswerTally implements Serializable {
    private String question;
    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
