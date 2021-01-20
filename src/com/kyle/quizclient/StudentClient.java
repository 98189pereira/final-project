package com.kyle.quizclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.kyle.quiz.QuizHandler;
import com.kyle.cloudservice.ServiceStudentController;

import java.io.IOException;

/*
    Author:     Kyle
    Class:      StudentClient
    Desc:       JavaFX student client for quiz application
    Args:       Opt. String URL to AWS API Gateway Lambda Endpoint
                if not provided, starts as a local application
 */
public class StudentClient extends Application {
    //  Constants
    static final int SCREEN_WIDTH = 960;
    static final int SCREEN_HEIGHT=  720;
    static final String WINDOW_TITLE = "Quiz Application";

    private static StudentController controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        //  Load from fxml and attach controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("student.fxml"));
        loader.setController(controller);

        //  Set window properties
        Parent root = loader.load();
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setAlwaysOnTop(true);

        //  Load CSS and set stage
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("student.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        //  Add a listener for when user leaves window
        primaryStage.focusedProperty().addListener(
                (observableValue, aBoolean, t1) -> controller.notFocused(aBoolean)
        );

        //  Start application
        controller.setupApplication();
    }// end of start()

    //  Cancel pending timers
    @Override
    public void stop() throws Exception {
        try {
            controller.quizTimer.cancel();
        } catch (Exception ignored) {}
        super.stop();
    }// end of stop()

    //  Attach different controllers depending on args
    public static void main(String[] args) {
        if(args.length > 0) {
            controller = new ServiceStudentController(new QuizHandler(), args[0]);
        } else {
            controller = new LocalStudentController(new QuizHandler());
        }

        launch(args);
    }// end of main()
}// End of StudentClient class
