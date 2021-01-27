package com.kyle.quizclient;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.kyle.parser.MarkupParser;
import com.kyle.quiz.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*
    Author:     Kyle
    Class:      TeacherController
    Desc:       abstract class defining the teacher application fxml controller
    Methods:
                pushQuizJson()      -   write quiz json to a location
                getAnswersJson()    -   get student response from somewhere
 */
public abstract class TeacherController implements SubmittableQuiz {
    //  Methods that must be implemented
    public abstract void setupApplication();
    public abstract boolean pushQuizJson(String quizJson);
    public abstract AnswersData getAnswersJson(String responseLocation,
                                   String keyLocation);

    /*
        Author:     Kyle
        Class:      AnswersData
        Desc:       inner class to mark quizzes
     */
    protected static class AnswersData {

        public AnswersData(String answerJson, String keyJson) {
            this.answerJson = answerJson;
            this.keyJson = keyJson;
        }

        final String answerJson;
        final String keyJson;
    }// end of AnswersData class

    //  Class Constructor
    public TeacherController(QuizFormat quiz) {
        this.quiz = quiz;
    }

    //  Handles to JavaFX assets
    public QuizFormat quiz;

    public GridPane quizGrid;
    public GridPane reviewGrid;
    public GridPane selectionGrid;
    public GridPane uploadGrid;

    public TextField quizFile;
    public TextField answerFile;
    public TextField keyFile;
    public TextField uploadDest;

    public Label studentField;
    public Label quizIdField;
    public Label uploadMessage;
    public Label quizTitle;
    public Label reviewMessage;
    public Label reviewTitle;

    public Button previewButton;
    public Button markButton;

    public HBox reviewSelect;
    public HBox uploadSelect;

    public VBox reviewBox;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    protected Stage stage;
    protected Node marksChart;

    final FileChooser fileChooser = new FileChooser();

    /*
        Author:     Kyle
        Method:     switchToUpload
        Desc:       switch visible pane
     */
    public void switchToUpload() {
        uploadSelect.setVisible(true);
        previewButton.setDefaultButton(true);
        reviewSelect.setVisible(false);
        markButton.setDefaultButton(false);
    }// end of switchToUpload()

    /*
        Author:     Kyle
        Method:     switchToReview
        Desc:       switch visible pane
     */
    public void switchToReview() {
        uploadSelect.setVisible(false);
        previewButton.setDefaultButton(false);
        reviewSelect.setVisible(true);
        markButton.setDefaultButton(true);
    }// end of switchToReview()

    /*
        Author:     Kyle
        Method:     browseUploadFile
        Desc:       called when 'browse' button pressed
     */
    public void browseUploadFile() {
        browseFile(quizFile);
    }// end of browseUploadFile()

    /*
        Author:     Kyle
        Method:     browseFile
        Desc:       pull up a file chooser dialog
     */
    public void browseFile(TextField textField) {
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            textField.setText(file.getAbsolutePath());
        }
    }// end of browseFile()

    /*
        Author:     Kyle
        Method:     uploadQuiz
        Desc:       called when 'upload' button pressed
     */
    public void uploadQuiz() {
        tryParsing(true);
    }// end of uploadQuiz()

    /*
        Author:     Kyle
        Method:     previewQuiz
        Desc:       called when 'preview' button pressed
     */
    public void previewQuiz() {
        tryParsing(false);
    }// end of previewQuiz()

    /*
        Author:     Kyle
        Method:     tryParsing
        Desc:       try parsing a quiz file to preview or upload
     */
    private void tryParsing(boolean upload) {
        //  get the file location
        String location = quizFile.getText().trim();
        String quizJson;

        //  clear error message
        uploadMessage.setText("");

        if(!location.endsWith(".txt")) {
            uploadMessage.setText(location + " is not a valid quiz format");
            return;
        }

        //  try to parse the quiz file
        try {
            MarkupParser parser = new MarkupParser(QuizFileFormat.class);
            quizJson = parser.parseFile(location);
        } catch (IOException e) {
            uploadMessage.setText("could not find quiz: " + location);
            return;
        }

        //  upload or preview the quiz
        if(upload) {
            if (pushQuizJson(quizJson)) {
                uploadMessage.setText(location + " could not be uploaded");
            }
        } else {
            //  try to load quiz
            try {
                quiz.loadQuestions(quizJson);
            } catch (InvalidQuizException ignored) {
                uploadMessage.setText(location + " is not in a valid quiz format");
                return;
            }

            //  Clear current JavaFX assets
            ArrayList<Node> toRemove = new ArrayList<>();
            for(Node node : quizGrid.getChildren()) {
                String id = node.getId();
                if(id != null && (
                        id.equals("requiredBox")
                                || id.equals("questionBox")
                )) toRemove.add(node);
            }
            for(Node node : toRemove) {
                quizGrid.getChildren().remove(node);
            }

            //  add quiz questions to quizGrid pane display
            quizTitle.setText(
                    quiz.addQuestions(this, quizGrid).getQuizTitleText()
            );
        }// end else
    }// end of tryParsing()

    /*
        Author:     Kyle
        Method:     markQuiz
        Desc:       mark a quiz against a students answers
     */
    public void markQuiz() {
        //  get file locations
        String answers = answerFile.getText().trim();
        String answerKey = keyFile.getText().trim();
        String answerJson, keyJson;

        //  clear error message
        reviewMessage.setText("");

        //  try to get student answers
        AnswersData answersData = getAnswersJson(answers, answerKey);
        if(answersData == null)
            return;

        answerJson = answersData.answerJson;
        keyJson = answersData.keyJson;

        //  Clear current JavaFX assets
        ArrayList<Node> toRemove = new ArrayList<>();
        for(Node node : reviewGrid.getChildren()) {
            String id = node.getId();
            if(id != null && id.equals("questionBox"))
                toRemove.add(node);
        }
        for(Node node : toRemove) {
            reviewGrid.getChildren().remove(node);
        }
        reviewBox.getChildren().remove(marksChart);

        //  try to mark quiz and add results to reviewGrid pane display
        try {
            ExtraQuizData data = quiz.checkAnswers(this, reviewGrid, answerJson, keyJson);
            reviewTitle.setText(data.getQuizTitleText());
            marksChart = data.getExtraNode();
            marksChart.getStyleClass().add("white-bg-node");
            reviewBox.getChildren().add(0, marksChart);
        } catch (InvalidQuizException ignored) {
            reviewMessage.setText("Could not mark student " + answers +
                    " on quiz " + answerKey);
        }
    }// end of markQuiz()
}// end of TeacherController class
