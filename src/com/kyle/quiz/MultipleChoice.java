package com.kyle.quiz;

import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/*
    Author:     Kyle
    Class:      MultipleChoice
    Desc:       multiple choice quiz question format
 */
public class MultipleChoice extends BlankBox {

    //  Class constructor
    public MultipleChoice(String question, String[] options) {
        super(question);
        this.options = options;
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
        answers = new ToggleGroup();

        questionBox.getChildren().add(questionText);

        for(String option : options) {
            RadioButton a = new RadioButton(option);
            a.setToggleGroup(answers);
            questionBox.getChildren().add(a);
        }

        questionBox.setId("questionBox");
    }// end of createQuestion()

    protected final String[] options;

    protected ToggleGroup answers;

    /*
        Author:     Kyle
        Method:     getAnswer
        Desc:       get answer from ToggleGroup
     */
    @Override
    public String getAnswer() {
        if(answers.getSelectedToggle() != null) {
            RadioButton answer = (RadioButton) answers.getSelectedToggle();
            return answer.getText();
        }
        return DEFAULT_ANSWER;
    }// end of getAnswer()

    /*
        Author:     Kyle
        Method:     setAnswer
        Desc:       set answer in ToggleGroup
     */
    @Override
    public void setAnswer(String value) {
        for(Toggle toggle : answers.getToggles()) {
            RadioButton option = (RadioButton) toggle;
            if(option.getText().equals(value)) {
                option.setSelected(true);
            }
        }
    }// end of setAnswer()
}// end of MultipleChoice class
