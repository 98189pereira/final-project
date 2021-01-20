package com.kyle.quizclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.kyle.quiz.QuizHandler;
import com.kyle.cloudservice.ServiceTeacherController;

/*
    Author:     Kyle
    Class:      TeacherClient
    Desc:       JavaFX teacher client for quiz application
    Args:       Opt. String URL to AWS API Gateway Lambda Endpoint
                if not provided, starts as a local application
 */
public class TeacherClient extends Application {
    //  Constants
    static final int SCREEN_WIDTH = 960;
    static final int SCREEN_HEIGHT = 720;
    static final String WINDOW_TITLE = "Teacher Portal";

    private static TeacherController controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //  Load from fxml and attach controller
        controller.setStage(primaryStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("teacher.fxml"));
        loader.setController(controller);

        //  Set window properties
        Parent root = loader.load();
        primaryStage.setTitle(WINDOW_TITLE);

        //  Load CSS and set stage
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("teacher.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        //  Start application
        controller.setupApplication();
    }// end of start()

    //  Attach different controllers depending on args
    public static void main(String[] args) {
        if(args.length > 0) {
            controller = new ServiceTeacherController(new QuizHandler(), args[0]);
        } else {
            controller = new LocalTeacherController(new QuizHandler());
        }

        launch(args);
    }// end of main()
}// End of TeacherClient class
