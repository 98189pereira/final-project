package com.kyle.quiz;

import javafx.scene.Node;

/*
    Author:     Kyle
    Class:      ExtraQuizData
    Desc:       additional data passed back when writing to display grid panes
 */
public class ExtraQuizData {

    //  Class constructor
    public ExtraQuizData(int i, String title) {
        lastGridPos = i;
        quizTitleText = title;
    }

    public int getLastGridPos() {
        return lastGridPos;
    }

    public String getQuizTitleText() {
        return quizTitleText;
    }

    private final int lastGridPos;
    private final String quizTitleText;

    public Node getExtraNode() {
        return extraNode;
    }

    public ExtraQuizData setExtraNode(Node extraNode) {
        this.extraNode = extraNode;
        return this;
    }

    private Node extraNode = null;
}// end of ExtraQuizData class
