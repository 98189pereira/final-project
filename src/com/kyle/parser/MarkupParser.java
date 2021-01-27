package com.kyle.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

/*
    Author:     Kyle
    Class:      MarkupParser
    Desc:       reusable class to parse a markup file into
                JSON, following a set of rules
    Methods:
                parseFile() -   parse a text file according to a set of given rules
 */
public class MarkupParser {

    //  Class constructor
    public MarkupParser(Class<? extends ValidParserRules> rules) {
        assert rules.isEnum();
        this.rules = rules.getEnumConstants();
    }

    private final ValidParserRules[] rules;

    /*
        Author:     Kyle
        Method:     findPrefixType
        Desc:       find the definition of a given prefix
     */
    private ValidParserRules findPrefixType(String line) {
        for(ValidParserRules rule : rules)
            if(line.startsWith(rule.getPrefix()))
                return rule;
        return null;
    }// end of findPrefixType()

    /*
        Author:     Kyle
        Method:     tabs
        Desc:       return tab spaces for pretty printing
     */
    private static String tabs(int i) {
        if(i < 1)
            return "";
        StringBuilder sb = new StringBuilder();
        for(int j = 0; j < i; ++j) {
            sb.append("  ");
        }
        return sb.toString();
    }// end of tabs()

    /*
        Author:     Kyle
        Method:     parseFile
        Desc:       parse a text file according to the given rules
                    sends back JSON string
     */
    public String parseFile(String source) throws IOException {
        //  Open file
        FileReader fileReader = new FileReader(source);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        //  init variables
        Stack<ValidParserRules> nesting = new Stack<>();
        StringBuilder outputJson = new StringBuilder();

        //  read file line by line
        String currentLine = bufferedReader.readLine();

        while(currentLine != null) {
            //  get prefix of current line
            currentLine = currentLine.trim();
            ValidParserRules rule = findPrefixType(currentLine);

            //  if not valid prefix
            if(rule == null) {
                currentLine = bufferedReader.readLine();
                continue;
            }

            //  if not in any JSON field
            if(nesting.size() == 0) {
                //  start of JSON
                if(rule.getParent() == null) {
                    outputJson.append("{");
                    nesting.push(rule);
                } else {
                    break;
                }// end of JSON
            } else {
                //  if a property of current field
                if(rule.getParent() == nesting.peek()) {
                    //  format JSON
                    if(outputJson.charAt(outputJson.length() - 1) != '{'
                            && outputJson.charAt(outputJson.length() - 1) != '[')
                        outputJson.append(",");
                    outputJson.append("\n")
                            .append(tabs(nesting.size()));
                    if(nesting.peek().getType() == PropertyType.OBJECT)
                        outputJson.append("\"").append(rule.getName())
                                .append("\": ");
                    switch (rule.getType()) {
                        case OBJECT:
                            outputJson.append("{");
                            nesting.push(rule);
                            break;
                        case ARRAY:
                            outputJson.append("[");
                            nesting.push(rule);
                            break;
                        default:
                            String value = currentLine.substring(
                                    currentLine.indexOf(rule.getPrefix())
                                    + rule.getPrefix().length()
                            ).trim();
                            outputJson.append("\"")
                                    .append(value)
                                    .append("\"");
                            break;
                    }// end of switch/case
                } else {
                    //  go to above field
                    PropertyType type = nesting.pop().getType();
                    switch (type) {
                        case ARRAY:
                            outputJson.append("\n")
                                    .append(tabs(nesting.size()))
                                    .append("]");
                            break;
                        case OBJECT:
                            outputJson.append("\n")
                                    .append(tabs(nesting.size()))
                                    .append("}");
                            break;
                        default:
                            break;
                    }// end of switch/case

                    continue;
                }// endif
            }// endif

            currentLine = bufferedReader.readLine();
        }// end loop

        //  close JSON brackets
        while(nesting.size() > 0) {
            PropertyType type = nesting.pop().getType();
            switch (type) {
                case ARRAY:
                    outputJson.append("\n")
                            .append(tabs(nesting.size()))
                            .append("]");
                    break;
                case OBJECT:
                    outputJson.append("\n")
                            .append(tabs(nesting.size()))
                            .append("}");
                    break;
                default:
                    break;
            }// end switch/case
        }// end loop

        bufferedReader.close();
        fileReader.close();

        return outputJson.toString();
    }// end of parseFile()
}// end of MarkupParser class
