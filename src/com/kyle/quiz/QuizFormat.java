package com.kyle.quiz;

import javafx.scene.layout.GridPane;

import java.util.ArrayList;

/*
    Author:     Kyle
    Interface:  QuizFormat
    Desc:       interface for quiz class
 */
public interface QuizFormat {
    String getAnswersJson();
    ArrayList<QuizQuestion> getQuestions();

    String scrubQuiz(String quizJson) throws InvalidQuizException;
    void loadQuestions(String quizJson) throws InvalidQuizException;
    ExtraQuizData addQuestions(SubmittableQuiz controller, GridPane display);
    void submitQuiz(SubmittableQuiz controller, GridPane display, boolean forceSubmit);
    ExtraQuizData checkAnswers(SubmittableQuiz controller, GridPane display,
                               String answerJson, String keyJson) throws InvalidQuizException;
}// end of QuizFormat interface
