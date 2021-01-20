package com.kyle.quizclient;

import javafx.scene.control.Button;
import com.kyle.quiz.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
    Author:     Kyle
    Class:      LocalTeacherController
    Desc:       implementation of TeacherController for local application
 */
public class LocalTeacherController extends TeacherController {

    //  Class constructor
    public LocalTeacherController(QuizFormat quiz) {
        super(quiz);
    }

    //  Extra UI button method bindings
    public void browseAnswerFile() {
        browseFile(answerFile);
    }

    public void browseKeyFile() {
        browseFile(keyFile);
    }

    /*
        Author:     Kyle
        Method:     setupApplication
        Desc:       make the quiz upload screen visible
                    and add extra ui controls
     */
    @Override
    public void setupApplication() {
        switchToUpload();
        Button browseAnswer = new Button();
        browseAnswer.setText("Browse");
        browseAnswer.setOnAction((actionEvent) -> browseAnswerFile());
        Button browseKey = new Button();
        browseKey.setText("Browse");
        browseKey.setOnAction((actionEvent -> browseKeyFile()));
        selectionGrid.add(browseAnswer, 2 ,3);
        selectionGrid.add(browseKey, 2 ,4);
    }// end of setupApplication()

    /*
        Author:     Kyle
        Method:     pushQuizJson
        Desc:       writes quiz to a local file and makes a student copy
                    without any marked answers
     */
    @Override
    public boolean pushQuizJson(String quizJson) {
        String location = uploadDest.getText();

        if(!location.endsWith(".json")) {
            return true;
        }

        try {
            FileWriter fileWriter = new FileWriter(location);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(quizJson);
            printWriter.close();
            fileWriter.close();

            quizJson = quiz.scrubQuiz(quizJson);
            fileWriter = new FileWriter("student_copy_" + location);
            printWriter = new PrintWriter(fileWriter);
            printWriter.print(quizJson);
            printWriter.close();
            fileWriter.close();

        } catch (IOException ignored) {
            return true;
        }

        return false;
    }// end of pushQuizJson()

    /*
        Author:     Kyle
        Method:     getAnswersJson
        Desc:       get student response and answer key from local files
     */
    @Override
    public AnswersData getAnswersJson(String responseLocation, String keyLocation) {
        String answerJson, keyJson;

        if(!responseLocation.endsWith(".json")) {
            reviewMessage.setText(responseLocation + " is not a valid quiz answer format");
            return null;
        }

        if(!keyLocation.endsWith(".json")) {
            reviewMessage.setText(keyLocation + " is not a valid quiz answer format");
            return null;
        }

        try {
            answerJson = new String(Files.readAllBytes(Paths.get(responseLocation)));
        } catch (IOException e) {
            reviewMessage.setText("could not find answers to: " + responseLocation);
            return null;
        }

        try {
            keyJson = new String(Files.readAllBytes(Paths.get(keyLocation)));
        } catch (IOException e) {
            reviewMessage.setText("could not find answers to: " + keyLocation);
            return null;
        }

        return new AnswersData(answerJson, keyJson);
    }// end of getAnswersJson()
}// end of LocalTeacherController class
