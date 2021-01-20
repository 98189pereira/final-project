package com.kyle.quizclient;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import com.kyle.quiz.ExtraQuizData;
import com.kyle.quiz.InvalidQuizException;
import com.kyle.quiz.QuizFormat;
import com.kyle.quiz.SubmittableQuiz;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/*
    Author:     Kyle
    Class:      StudentController
    Desc:       abstract class defining the student application fxml controller
 */
public abstract class StudentController implements SubmittableQuiz {
    //  Methods that must be implemented
    public abstract void setupApplication();
    public abstract void sendQuizJson(String quizJson, String studentName);
    public abstract String fetchQuizJson(String location);

    //  Class Constructor
    public StudentController(QuizFormat quiz) {
        this.quiz = quiz;
    }

    //  constant
    public static int MAX_QUIZ_EXIT_TIME = 5;

    //  Handles to JavaFX assets
    public QuizFormat quiz;

    public ScrollPane quizPane;
    public GridPane quizGrid;

    public StackPane quizSelect;
    public Label quizTitle;

    public TextField nameField;
    public TextField quizField;
    public Label selectMessage;

    public AnchorPane quizOverlay;
    public Label overlayMessage;

    protected String studentName;
    protected String quizFileName;
    protected Button submitButton;

    /*
        Author:     Kyle
        Method:     quizComplete
        Desc:       called once quiz is submitted
     */
    @Override
    public void quizComplete() {
        quizPane.setVisible(false);
        quizSelect.setVisible(true);

        String quizJson = quiz.getAnswersJson();
        sendQuizJson(quizJson, studentName);
    }// end of quizComplete()

    /*
        Author:     Kyle
        Method:     findQuiz
        Desc:       called once begin button is clicked
     */
    public void findQuiz() {
        //  try to get quiz from text entered in quizFileName field
        studentName = nameField.getText().trim();
        quizFileName = quizField.getText().trim();
        String quizJson = fetchQuizJson(quizFileName);
        if(quizJson == null)
            return;

        //  clear error message
        selectMessage.setText("");

        //  try to load quiz
        try {
            quiz.loadQuestions(quizJson);
        } catch (InvalidQuizException ignored) {
            selectMessage.setText(quizFileName + " is not in a valid quiz format");
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
        quizGrid.getChildren().remove(submitButton);

        //  add quiz questions to quizGrid pane display
        nameField.setText("");
        quizField.setText("");
        ExtraQuizData data = quiz.addQuestions(this, quizGrid);
        quizTitle.setText(data.getQuizTitleText());
        submitButton = new Button("Submit");
        submitButton.setOnAction(actionEvent -> quiz.submitQuiz(this, quizGrid, false));
        submitButton.setId("submitButton");
        quizGrid.add(submitButton, 0, data.getLastGridPos() + 1);

        //  Switch screens
        quizSelect.setVisible(false);
        quizPane.setVisible(true);
    }// end of findQuiz()

    /*
        Author:     Kyle
        Method:     notFocused
        Desc:       called when user leaves application window
     */
    public void notFocused(boolean unfocused) {
        //  If they are currently taking a quiz
        if(quizPane.isVisible()) {
            quizOverlay.setVisible(unfocused);
            //  Prompt them to return to the window
            if(unfocused) {
                overlayMessage.setText(Integer.toString(MAX_QUIZ_EXIT_TIME));
                quizTimer.cancel();

                TimerTask quizPause = new TimerTask() {

                    @Override
                    public void run() {
                        pauseTask();
                    }
                };

                quizTimer = new Timer();
                quizTimer.scheduleAtFixedRate(quizPause, 1000, 1000);
            } else {
                quizTimer.cancel();
            }// end else
        }// endif
    }// end of notFocused()

    //  Quiz countdown timer
    Timer quizTimer = new Timer();

    /*
        Author:     Kyle
        Method:     pauseTask
        Desc:       timerTask in quizTimer
                    counts down time away from quiz
                    if this exceeds a certain amount
                    automatically submit quiz
     */
    public void pauseTask() {
        try {
            SubmittableQuiz controller = this;
            Platform.runLater(() -> {
                int countdown = Integer.parseInt(overlayMessage.getText());
                --countdown;
                if(countdown < 0) {
                    quizTimer.cancel();
                    quizOverlay.setVisible(false);
                    quiz.submitQuiz(controller, quizGrid, true);
                } else {
                    overlayMessage.setText(String.valueOf(countdown));
                }
            });
        } catch (Exception ignored) {}
    }// end of pauseTask()
}// end of StudentController class
