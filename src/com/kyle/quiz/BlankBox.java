package com.kyle.quiz;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;

/*
    Author:     Kyle
    Class:      BlankBox
    Desc:       blank box quiz question format
 */
public class BlankBox implements QuizQuestion {

    //  Class Constructor
    public BlankBox(String question) {
        this.question = question;
        createQuestion();
    }

    /*
        Author:     Kyle
        Method:     createQuestion
        Desc:       create the question's JavaFX asset
     */
    private void createQuestion() {
        questionBox = new VBox(SPACING);

        Label questionText = new Label(question);
        questionBox.getChildren().add(questionText);

        boxAnswer = new TextField();
        questionBox.getChildren().add(boxAnswer);
        questionBox.setId("questionBox");
    }// end of createQuestion()

    public static final String DEFAULT_ANSWER = "";

    protected static final int SPACING = 10;

    protected final String question;

    protected VBox questionBox;
    protected TextField boxAnswer;

    public void setGridPos(int gridPos) {
        this.gridPos = gridPos;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    protected int gridPos;

    protected boolean required = false;

    @Override
    public int getGridPos() {
        return gridPos;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public String getAnswer() {
        return boxAnswer.getText();
    }

    @Override
    public void setAnswer(String value) {
        boxAnswer.setText(value);
    }

    @Override
    public VBox getQuestionBox() {
        return questionBox;
    }
}// End of BlankBox class
