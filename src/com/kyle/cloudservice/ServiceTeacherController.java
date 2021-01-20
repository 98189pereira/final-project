package com.kyle.cloudservice;

import com.kyle.quizclient.TeacherController;
import com.google.gson.Gson;
import javafx.scene.control.Button;
import com.kyle.quiz.QuizFormat;

import java.io.*;

/*
    Author:     Kyle
    Class:      ServiceTeacherController
    Desc:       implementation of TeacherController for cloud application
 */
public class ServiceTeacherController extends TeacherController {

    //  Class constructor
    public ServiceTeacherController(QuizFormat quiz, String serviceEndpoint) {
        super(quiz);
        this.serviceEndpoint = serviceEndpoint;
    }

    //  Extra UI button method bindings
    public void browseAnswerFile() {
        browseFile(answerFile);
    }

    public void browseKeyFile() {
        browseFile(keyFile);
    }

    String serviceEndpoint;

    /*
        Author:     Kyle
        Method:     setupApplication
        Desc:       make the quiz upload screen visible
                    and add extra ui controls
     */
    @Override
    public void setupApplication() {
        switchToUpload();
        Button removeQuiz = new Button();
        removeQuiz.setText("Remove Quiz");
        removeQuiz.setOnAction((actionEvent) -> removeQuiz());
        uploadGrid.add(removeQuiz, 0, 8);
        studentField.setText("Student Name:");
        quizIdField.setText("Quiz ID:");
    }// end of setupApplication()

    /*
        Author:     Kyle
        Method:     removeQuiz
        Desc:       called when 'remove quiz' button pressed
     */
    void removeQuiz() {
        String location = uploadDest.getText();
        try {
            Gson gson = new Gson();
            LambdaClient lambdaClient = new LambdaClient();
            lambdaClient.initConnection(serviceEndpoint + "/uploadquiz");

            lambdaClient.sendRequest(gson.toJson(
                    new LambdaClient.TeacherUploadQuiz(
                            location, "",
                            "teachers_quizzes", true
                    )
            ));

            lambdaClient.initConnection(serviceEndpoint + "/uploadquiz");

            lambdaClient.sendRequest(gson.toJson(
                    new LambdaClient.TeacherUploadQuiz(
                            location, "",
                            "students_quizzes", true
                    )
            ));

            uploadMessage.setText("Successfully removed quiz " + location);
        } catch (IOException ignored) {
            uploadMessage.setText("Error removing quiz from " + location);
        }
    }// end of removeQuiz()

    /*
        Author:     Kyle
        Method:     pushQuizJson
        Desc:       writes quiz to the 'teachers_quizzes' database and
                    a student copy to the 'students_quizzes' one
     */
    @Override
    public boolean pushQuizJson(String quizJson) {
        try {
            String location = uploadDest.getText();
            Gson gson = new Gson();
            LambdaClient lambdaClient = new LambdaClient();
            lambdaClient.initConnection(serviceEndpoint + "/uploadquiz");

            String response = lambdaClient.sendRequest(gson.toJson(
                    new LambdaClient.TeacherUploadQuiz(
                            location, quizJson,
                            "teachers_quizzes", false
                    )
            ));

            LambdaClient.TeacherQuizReply quizReply1 =
                    gson.fromJson(response, LambdaClient.TeacherQuizReply.class);

            lambdaClient.initConnection(serviceEndpoint + "/uploadquiz");

            response = lambdaClient.sendRequest(gson.toJson(
                    new LambdaClient.TeacherUploadQuiz(
                            location, quiz.scrubQuiz(quizJson),
                            "students_quizzes", false
                    )
            ));

            LambdaClient.TeacherQuizReply quizReply2 =
                    gson.fromJson(response, LambdaClient.TeacherQuizReply.class);

            uploadMessage.setText("Successfully uploaded quiz to " + location);

            return !quizReply1.isSuccess() || !quizReply2.isSuccess();
        } catch (IOException ignored) {
            return true;
        }
    }// end of pushQuizJson()

    /*
        Author:     Kyle
        Method:     getAnswersJson
        Desc:       get student response and answer key from the cloud database
     */
    @Override
    public AnswersData getAnswersJson(String responseLocation, String keyLocation) {
        try {
            Gson gson = new Gson();
            LambdaClient lambdaClient = new LambdaClient();
            lambdaClient.initConnection(serviceEndpoint + "/getanswers");

            String response = lambdaClient.sendRequest(gson.toJson(
                    new LambdaClient.TeacherGetAnswers(
                            keyLocation, responseLocation
                    )
            ));

            LambdaClient.TeacherAnswersReply answersReply =
                    gson.fromJson(response, LambdaClient.TeacherAnswersReply.class);

            if(!answersReply.isSuccess())
                throw new IOException();
            return new AnswersData(answersReply.getAnswers(),answersReply.getKey());
        } catch (IOException ignored) {
            reviewMessage.setText("Could not find student " + responseLocation +
                    " on quiz " + keyLocation);
            return null;
        }
    }// end of getAnswersJson()
}// end of ServiceTeacherController class
