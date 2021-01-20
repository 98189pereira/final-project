package com.kyle.quizclient;

import com.kyle.quiz.QuizFormat;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
    Author:     Kyle
    Class:      LocalStudentController
    Desc:       implementation of StudentController for local application
 */
public class LocalStudentController extends StudentController {

    //  Class constructor
    public LocalStudentController(QuizFormat quiz) {
        super(quiz);
    }

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
        Desc:       writes answers to a local file
     */
    @Override
    public void sendQuizJson(String quizJson, String studentName) {
        try {
            String dest = studentName + "_" + quizFileName;
            FileWriter fileWriter = new FileWriter(dest);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(quizJson);
            printWriter.close();
            fileWriter.close();
        } catch (IOException ignored) {}
    }// end of sendQuizJson()

    /*
        Author:     Kyle
        Method:     fetchQuizJson
        Desc:       get quiz from a local file
     */
    @Override
    public String fetchQuizJson(String location) {
        String quizJson;

        if(!location.endsWith(".json")) {
            selectMessage.setText(location + " is not a valid quiz format");
            return null;
        }

        try {
            quizJson = new String(Files.readAllBytes(Paths.get(location)));
        } catch (IOException ignored) {
            selectMessage.setText("could not find quiz: " + location);
            return null;
        }

        return quizJson;
    }// end of fetchQuizJson()
}// end of LocalStudentController class
