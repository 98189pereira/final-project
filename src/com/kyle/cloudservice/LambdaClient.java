package com.kyle.cloudservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/*
    Author:     Kyle
    Class:      LambdaClient
    Desc:       class to interface with AWS lambda application
                send and receive data from DynamoDB cloud database
 */
public class LambdaClient {
    private HttpURLConnection connection;

    /*
        Author:     Kyle
        Method:     initConnection
        Desc:       open an http connection to the specified endpoint
     */
    public void initConnection(String endpoint) throws IOException {
        URL url = new URL(endpoint);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        connection.setRequestProperty("content-type", "application/json; utf-8");
        connection.setRequestProperty("accept", "application/json");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        connection.setDoOutput(true);
    }// end of initConnection()

    /*
        Author:     Kyle
        Method:     sendRequest
        Desc:       send a json request to the endpoint and
                    return the json response as a String
     */
    public String sendRequest(String jsonRequest) throws IOException {
        try(OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );
        String line = in.readLine();
        StringBuilder input = new StringBuilder();
        while(line != null) {
            input.append(line);
            line = in.readLine();
        }
        in.close();
        connection.disconnect();

        return input.toString();
    }// end of sendRequest()

    /*
        Author:     Kyle
        Class:      TeacherUploadQuiz
        Desc:       schema for Lambda TeacherUploadQuiz request
     */
    static class TeacherUploadQuiz {

        TeacherUploadQuiz(String quizName, String quizData,
                          String quizDest, boolean remove) {
            this.quizName = quizName;
            this.quizData = quizData;
            this.quizDest = quizDest;
            this.remove = remove;
        }

        String quizName;
        String quizData;
        String quizDest;
        boolean remove;
    }// end of TeacherUploadQuiz class

    /*
        Author:     Kyle
        Class:      TeacherQuizReply
        Desc:       schema for Lambda TeacherQuizReply response
     */
    static class TeacherQuizReply {

        public boolean isSuccess() {
            return success;
        }

        private boolean success;
    }// end of TeacherQuizReply class

    /*
        Author:     Kyle
        Class:      TeacherGetAnswers
        Desc:       schema for Lambda TeacherGetAnswers request
     */
    static class TeacherGetAnswers {

        TeacherGetAnswers(String quizName, String studentName) {
            this.quizName = quizName;
            this.studentName = studentName;
        }

        String quizName;
        String studentName;
    }// end of TeacherGetAnswers class

    /*
        Author:     Kyle
        Class:      TeacherAnswersReply
        Desc:       schema for Lambda TeacherAnswersReply response
     */
    static class TeacherAnswersReply {

        public boolean isSuccess() {
            return success;
        }

        public String getKey() {
            return key;
        }

        public String getAnswers() {
            return answers;
        }

        private boolean success;
        private String key;
        private String answers;
    }// end of TeacherAnswersReply class

    /*
        Author:     Kyle
        Class:      StudentGetQuiz
        Desc:       schema for Lambda StudentGetQuiz request
     */
    static class StudentGetQuiz {

        public StudentGetQuiz(String quizName) {
            this.quizName = quizName;
        }

        String quizName;
    }// end of StudentGetQuiz class

    /*
        Author:     Kyle
        Class:      StudentQuizReply
        Desc:       schema for Lambda StudentQuizReply response
     */
    static class StudentQuizReply {

        public boolean isSuccess() {
            return success;
        }

        public String getQuizData() {
            return quizData;
        }

        private boolean success;
        private String quizData;
    }// end of StudentQuizReply class

    /*
        Author:     Kyle
        Class:      StudentUploadAnswers
        Desc:       schema for Lambda StudentUploadAnswers request
     */
    static class StudentUploadAnswers {

        public StudentUploadAnswers(String quizName, String studentName, String answers) {
            this.quizName = quizName;
            this.studentName = studentName;
            this.answers = answers;
        }

        String quizName;
        String studentName;
        String answers;
    }// end of StudentUploadAnswers class

    /*
        Author:     Kyle
        Class:      StudentAnswersReply
        Desc:       schema for Lambda StudentAnswersReply response
     */
    static class StudentAnswersReply {

        public boolean isSuccess() {
            return success;
        }

        private boolean success;
    }// end of StudentAnswersReply class
}// end of LambdaClient class
