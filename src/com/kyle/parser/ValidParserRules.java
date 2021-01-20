package com.kyle.parser;

/*
    Author:     Kyle
    Interface:  ValidParserRules
    Desc:       interface for rules for markup parser class
                must be implemented by an enum defining
                names, prefixes, JSON property types, and
                JSON field parents for each rule
                * example found in com.kyle.quiz.QuizFileFormat
 */
public interface ValidParserRules {
    String getName();
    String getPrefix();
    PropertyType getType();
    ValidParserRules getParent();
}// end of ValidParserRules interface
