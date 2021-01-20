package com.kyle.cloudservice;

import com.kyle.quizclient.StudentController;
import com.google.gson.Gson;
import com.kyle.quiz.QuizFormat;

import java.io.IOException;

/*
    Author:     Kyle
    Class:      LocalStudentController
    Desc:       implementation of StudentController for cloud application
 */
public class ServiceStudentController extends StudentController {

    //  Class Constructor
    public ServiceStudentController(QuizFormat quiz, String serviceEndpoint) {
        super(quiz);
        this.serviceEndpoint = serviceEndpoint;
    }

    String serviceEndpoint;

    /*
        Author:     Kyle
        Method:     setupApplication
        Desc:       make the quiz selection screen visible
     */
    @Override
    public void setupApplication() {
        quizSelect.setVisible(true);
    }// end of setupApplication()

    /*
        Author:     Kyle
        Method:     sendQuizJson
        Desc:       writes answers to the cloud database
     */
    @Override
    public void sendQuizJson(String quizJson, String studentName) {
        try {
            Gson gson = new Gson();
            LambdaClient lambdaClient = new LambdaClient();
            lambdaClient.initConnection(serviceEndpoint + "/uploadanswers");

            String response = lambdaClient.sendRequest(gson.toJson(
                    new LambdaClient.StudentUploadAnswers(
                            quizFileName, studentName, quizJson
                    )
            ));

            selectMessage.setText("Successfully submitted quiz!");
        } catch (IOException ignored) {
            selectMessage.setText("An unknown error occurred while submitting the quiz.");
        }
    }// end of sendQuizJson()

    /*
        Author:     Kyle
        Method:     fetchQuizJson
        Desc:       get quiz from the cloud database
     */
    @Override
    public String fetchQuizJson(String location) {
        try {
            Gson gson = new Gson();
            LambdaClient lambdaClient = new LambdaClient();
            lambdaClient.initConnection(serviceEndpoint + "/getquiz");

            String response = lambdaClient.sendRequest(gson.toJson(
                    new LambdaClient.StudentGetQuiz(
                            location
                    )
            ));

            LambdaClient.StudentQuizReply quizReply =
                    gson.fromJson(response, LambdaClient.StudentQuizReply.class);

            if(!quizReply.isSuccess())
                throw new IOException();
            return quizReply.getQuizData();
        } catch (IOException ignored) {
            selectMessage.setText("Could not find quiz " + location);
            return null;
        }
    }// end of fetchQuizJson()
}// end of ServiceStudentController class
