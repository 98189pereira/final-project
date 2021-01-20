package com.kyle.quiz;

import com.kyle.parser.PropertyType;
import com.kyle.parser.ValidParserRules;

/*
    Author:     Kyle
    Enum:       QuizFileFormat
    Desc:       file format for quiz markup file
                example of a file written with these rules
                sample1.txt
 */
public enum QuizFileFormat implements ValidParserRules {
    //  rules
    QUIZ            ("!Quiz",       PropertyType.OBJECT,    null),
    QUIZ_TITLE      ("Title:",      PropertyType.VALUE,     QUIZ),
    QUESTIONS       ("Questions:",  PropertyType.ARRAY,     QUIZ),
    QUESTION_DATA   ("Q.",          PropertyType.OBJECT,    QUESTIONS),
    QUESTION        ("Question:",   PropertyType.VALUE,     QUESTION_DATA),
    QUESTION_TYPE   ("Format:",     PropertyType.VALUE,     QUESTION_DATA),
    OPTIONS         ("Options:",    PropertyType.ARRAY,     QUESTION_DATA),
    OPTION          (">",           PropertyType.VALUE,     OPTIONS),
    ANSWER          ("Correct:",    PropertyType.VALUE,     QUESTION_DATA),
    REQUIRED        ("Required",    PropertyType.VALUE,     QUESTION_DATA);

    //  Enum constructor
    QuizFileFormat(String prefix, PropertyType type, QuizFileFormat parent) {
        this.prefix = prefix;
        this.type = type;
        this.parent = parent;
    }

    private final String prefix;
    private final PropertyType type;
    private final QuizFileFormat parent;

    public String getName() {
        return this.name();
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public PropertyType getType() {
        return type;
    }

    @Override
    public ValidParserRules getParent() {
        return parent;
    }
}// end of QuizFileFormat enum
