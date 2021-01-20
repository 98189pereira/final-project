package com.kyle.quiz;

import javafx.scene.layout.VBox;

/*
    Author:     Kyle
    Interface:  QuizQuestion
    Desc:       interface for quiz questions
 */
public interface QuizQuestion {

    default boolean isRequired() {
        return false;
    }

    default String getDefaultAnswer() {
        return "";
    }

    int getGridPos();
    VBox getQuestionBox();
    String getQuestion();
    String getAnswer();
    void setAnswer(String value);
}// end of QuizQuestion interface
