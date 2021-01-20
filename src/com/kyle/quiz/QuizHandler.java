package com.kyle.quiz;

import com.google.gson.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/*
    Author:     Kyle
    Class:      QuizHandler
    Desc:       implementation of QuizFormat for demo quiz
 */
public class QuizHandler implements QuizFormat {
    //  constants
    protected static final int SPACING = 10;
    protected static final int QUESTION_OFFSET = 1;

    /*
        Author:     Kyle
        Method:     getQuestions
        Desc:       return an ArrayList of the current questions on the quiz
     */
    @Override
    public ArrayList<QuizQuestion> getQuestions() {
        return questions;
    }// end of getQuestions()

    private ArrayList<QuizQuestion> questions = new ArrayList<>();

    /*
        Author:     Kyle
        Method:     getAnswersJson
        Desc:       return the student response json
     */
    @Override
    public String getAnswersJson() {
        return answersJson;
    }// end of getAnswersJson()

    private String answersJson = "";

    public String quizTitle = "";

    /*
        Author:     Kyle
        Method:     getRequiredText
        Desc:       return a JavaFX element to place next to
                    unanswered questions on a quiz
     */
    public static HBox getRequiredText() {
        HBox required = new HBox(SPACING);

        Label requiredText = new Label("* this is a required question");
        requiredText.getStyleClass().add("message-text");
        required.getChildren().add(requiredText);
        required.setId("requiredBox");

        return required;
    }// end of getRequiredText()

    /*
        Author:     Kyle
        Method:     submitQuiz
        Desc:       submits the quiz
     */
    @Override
    public void submitQuiz(SubmittableQuiz controller, GridPane display, boolean forceSubmit) {
        //  begin writing student response JSON
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("EXITED", forceSubmit);
        JsonArray questionsArray = new JsonArray();
        jsonObject.add("QUESTIONS", questionsArray);

        for(QuizQuestion question : questions) {
            JsonObject questionObject = new JsonObject();
            questionObject.addProperty("QUESTION", question.getQuestion());
            questionObject.addProperty("ANSWER", question.getAnswer());
            questionsArray.add(questionObject);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        answersJson = gson.toJson(jsonObject);

        //  if quiz timed out
        if(forceSubmit) {
            controller.quizComplete();
            return;
        }

        //  clear JavaFX assets
        ArrayList<Node> toRemove = new ArrayList<>();
        for(Node node : display.getChildren()) {
            String id = node.getId();
            if(id != null && id.equals("requiredBox")) {
                toRemove.add(node);
            }
        }
        for(Node node : toRemove) {
            display.getChildren().remove(node);
        }

        //  check if all required questions have been answered
        boolean ready = true;
        for (QuizQuestion question : questions) {
            if (question.isRequired()) {
                if (question.getDefaultAnswer().equals(question.getAnswer())) {
                    ready = false;
                    HBox element = getRequiredText();
                    display.add(element, 2, question.getGridPos());
                }
            }
        }

        //  only if they have, then submit
        if(ready) {
            controller.quizComplete();
        }
    }// end of submitQuiz()

    /*
        Author:     Kyle
        Method:     checkAnswers
        Desc:       check whether a student's answers matches the answer key provided
     */
    @Override
    public ExtraQuizData checkAnswers(SubmittableQuiz controller, GridPane display,
                                      String answersJson, String keyJson) throws InvalidQuizException {
        JsonObject answersObject, keysObject;
        JsonArray answersArray, keyArray;

        //  try parsing the provided data
        try {
            answersObject = JsonParser.parseString(answersJson).getAsJsonObject();
            keysObject = JsonParser.parseString(keyJson).getAsJsonObject();
        } catch (IllegalStateException ignored) {
            throw new InvalidQuizException();
        }

        //  set the quiz's title
        if(keysObject.has("QUIZ_TITLE")) {
            quizTitle = keysObject.get("QUIZ_TITLE").getAsString();
        }

        //  try to get the quiz's questions
        if(answersObject.has("QUESTIONS") && keysObject.has("QUESTIONS")) {
            answersArray = answersObject.getAsJsonArray("QUESTIONS");
            keyArray = keysObject.getAsJsonArray("QUESTIONS");
        } else {
            throw new InvalidQuizException();
        }

        //  keep track of correct answers
        int score = 0, maxScore = 0;
        ObservableList<PieChart.Data> markData;
        ArrayList<String> correctQuestions = new ArrayList<>();
        LinkedHashMap<String, String> answers = getQuestionAnswerMap(answersArray);
        LinkedHashMap<String, String> correctAnswers = getQuestionAnswerMap(keyArray);

        //  display questions in quiz
        boolean invalidQuiz = true;
        if(answersObject.has("EXITED")) {
            invalidQuiz = answersObject.get("EXITED").getAsBoolean();
        }

        loadQuestions(keyJson);

        for(QuizQuestion question : questions) {
            if(answers.containsKey(question.getQuestion())) {
                question.setAnswer(answers.get(question.getQuestion()));
                question.getQuestionBox().setDisable(true);
            }
        }

        //  if properly submitted
        if(!invalidQuiz) {
            //  mark quiz questions
            for (Map.Entry<String, String> question : correctAnswers.entrySet()) {
                if (answers.containsKey(question.getKey())) {
                    ++maxScore;
                    String correctAnswer = correctAnswers.get(question.getKey());
                    String answer = answers.get(question.getKey());
                    if (answer.trim().toLowerCase(Locale.ROOT).startsWith(
                            correctAnswer.trim().toLowerCase(Locale.ROOT)
                    )) {
                        ++score;
                        correctQuestions.add(question.getKey());
                    }
                }
            }

            //  change colors
            for(QuizQuestion question : questions) {
                if(answers.containsKey(question.getQuestion())) {
                    if(correctQuestions.contains(question.getQuestion()))
                        for(Node node : question.getQuestionBox().getChildren()) {
                            node.getStyleClass().add("question-correct");
                        }
                    else if(correctAnswers.containsKey(question.getQuestion()))
                        for(Node node : question.getQuestionBox().getChildren()) {
                            node.getStyleClass().add("question-incorrect");
                        }
                }
            }

            //  set pie chart
            markData = FXCollections.observableArrayList(
                    new PieChart.Data("Not Marked",
                            ( (double) (answers.size() - maxScore) / answers.size() ) * 100
                    ),
                    new PieChart.Data("Correct",
                            ( (double) (score) / answers.size() ) * 100
                    ),
                    new PieChart.Data("Incorrect",
                            ( (double) (maxScore - score) / answers.size() ) * 100
                    )
            );
        } else {
            //  empty pie chart
            markData = FXCollections.observableArrayList(
                new PieChart.Data("Left Quiz Early", 100)
            );
        }// endif

        //  set pie chart display
        PieChart markBreakdown = new PieChart(markData);
        markBreakdown.setLegendSide(Side.RIGHT);
        markBreakdown.setStartAngle(90);
        markBreakdown.setClockwise(false);
        markBreakdown.setMinSize(200, 200);

        return addQuestions(controller, display).setExtraNode(markBreakdown);
    }// end of checkAnswers()

    /*
        Author:     Kyle
        Method:     getQuestionAnswerMap
        Desc:       get hash map pointing questions to answers
     */
    private LinkedHashMap<String, String> getQuestionAnswerMap(JsonArray array) {
        LinkedHashMap<String, String> results = new LinkedHashMap<>();
        for(JsonElement e : array) {
            if(e.isJsonObject()) {
                JsonObject question = e.getAsJsonObject();
                if(question.has("QUESTION")
                        && question.has("ANSWER")) {
                    results.put(
                            question.get("QUESTION").getAsString(),
                            question.get("ANSWER").getAsString()
                    );
                }
            }
        }

        return results;
    }// end of getQuestionAnswerMap()

    /*
        Author:     Kyle
        Method:     addQuestions
        Desc:       add quiz questions to display grid pane
     */
    @Override
    public ExtraQuizData addQuestions(SubmittableQuiz controller, GridPane display) {
        int i = 0;

        for(QuizQuestion question : questions) {
            VBox element = question.getQuestionBox();
            display.add(element,
                    0, question.getGridPos(), 2, 1);
            i = question.getGridPos();
        }

        return new ExtraQuizData(i, quizTitle);
    }// end of addQuestions()

    /*
        Author:     Kyle
        Method:     scrubQuiz
        Desc:       return quiz json string without correct answers marked
     */
    public String scrubQuiz(String quizJson) throws InvalidQuizException {
        JsonObject jsonObject = JsonParser.parseString(quizJson).getAsJsonObject();
        JsonArray questionsArray;

        if(jsonObject.has("QUESTIONS")) {
            questionsArray = jsonObject.getAsJsonArray("QUESTIONS");
        } else {
            throw new InvalidQuizException();
        }

        for(JsonElement e : questionsArray) {
            if(e.isJsonObject()) {
                JsonObject question = e.getAsJsonObject();
                if(question.has("ANSWER")) {
                    question.remove("ANSWER");
                }
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(jsonObject);
    }// end of scrubQuiz()

    /*
        Author:     Kyle
        Method:     loadQuestions
        Desc:       create quiz question objects from quiz json
     */
    @Override
    public void loadQuestions(String quizJson) throws InvalidQuizException {
        JsonObject jsonObject = JsonParser.parseString(quizJson).getAsJsonObject();
        JsonArray questionsArray;

        //  try to get questions json
        if(jsonObject.has("QUESTIONS")) {
            questionsArray = jsonObject.getAsJsonArray("QUESTIONS");
        } else {
            throw new InvalidQuizException();
        }

        if(jsonObject.has("QUIZ_TITLE")) {
            quizTitle = jsonObject.get("QUIZ_TITLE").getAsString();
        }

        int i = QUESTION_OFFSET;

        questions = new ArrayList<>();

        //  loop through json questions array
        for(JsonElement q : questionsArray) {
            //  try to classify question type
            JsonObject question = q.getAsJsonObject();
            QuestionTypes type;
            if(!question.has("QUESTION_TYPE")
                    || !question.has("QUESTION"))
                continue;
            try {
                type = QuestionTypes.valueOf(question.get("QUESTION_TYPE").getAsString());
            } catch (IllegalArgumentException ignored) {
                continue;
            }
            //  create different question types
            switch (type) {
                case blank_box:
                    BlankBox bb = new BlankBox(question.get("QUESTION").getAsString());
                    bb.setRequired(question.has("REQUIRED"));
                    bb.setGridPos(i);
                    questions.add(bb);
                    ++i;
                    break;
                case multiple_choice:
                    if(!question.has("OPTIONS"))
                        break;
                    MultipleChoice mc = new MultipleChoice(
                            question.get("QUESTION").getAsString(),
                            new Gson().fromJson(
                                    question.get("OPTIONS").getAsJsonArray(),
                                    String[].class
                            )
                    );
                    mc.setRequired(question.has("REQUIRED"));
                    mc.setGridPos(i);
                    questions.add(mc);
                    ++i;
                    break;
                default:
                    break;
            }// end switch/case
        }// end loop
    }// end of loadQuestions()
}// end of QuizHandler class
