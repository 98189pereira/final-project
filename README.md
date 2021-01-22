# final-project

## Components
1. **Quiz Taker Application**
2. **Markup Parser Class**

## Quiz Taker Application
This is an application designed to facilitate the administration of simple
computer-based quizzes. There are two components to the application, a 
teacher portal that can upload and mark quizzes, and a student app from 
which to take quizzes. In addition, there are two versions of the application,
one using the computer's local filesystem, and one that stores and retrieves
quizzes from an online cloud database. 

### Instructions
#### 1. Create the quiz file
The teacher creates a text file with the quiz questions, answers, etc.
The basic format currently only supports multiple choice and blank box 
style questions. The first line must start with the string `!Quiz` to identify
the file format. Then the quiz title may be provided on a new line prefixed 
with `Title:`. The questions must be given in a separate section starting 
with a new line `Questions:`. Each question's data is given in a separate
section starting with a new line `Q.`. The question text is given in a line
prefixed `Question:`. The question format is specified on a line as either
`Format: multiple_choice` or `Format: blank_box`. Multiple choice questions
will have their options in another section starting with a new line 
`Options:`. Each option is on a new line prefixed `>`. Questions can be 
marked as 'required' by placing another line in their section - `Required`.
In addition, a correct answer can be specified for marking purposes on a new
line prefixed `Correct:`, although this field will not be present in the 
students' copies of quizzes. Each field does not have to be indented to 
match its section, although it can help with readability of the quiz file.
Each field does have to be on a new line in order to be parsed properly. 
An example of a sample quiz text file can be found in `sample1.txt`.
#### 2. Upload the quiz file
In the teacher's portal JavaFX application, click `Select Action` in the 
menu bar and navigate to the `Upload Quizzes` page, if it is not already on
that page. Enter the file name of the quiz text file, alternatively you can
use the file selection dialog to upload a specific file. Hit `enter` on your 
keyboard or the `Preview` button and a preview of how the quiz will look 
will appear in the pane on the right. This will give an error message if 
the file is in an invalid format. Once you are satisfied with the appearance
of the quiz, assign it a Quiz ID and hit the upload button to generate the
actual Quiz. This will create two copies, one named `<quizId>.json`, where
`<quizId>` is whatever name you chose, and another called 
`student_copy_<quizId>.json`. The latter has the correct answer fields 
removed and is the file that should be shared with students.
#### 3. Student takes quiz
In the student application, students are presented with an introduction screen
containing instructions for the quiz. There are also two fields, one for the 
student's name and another for the Quiz ID. For the local application, this 
should be filled in as the full file name: `student_copy_<quizId>.json`. Once
the quiz has successfully loaded, students will see their quiz similar to the 
preview section of the Teacher Portal, with a `Submit` button at the end of 
the quiz. If a student attempts to leave the application before submitting
their quiz, an overlay will appear prompting the student to return the quiz 
in 5 seconds, or the quiz will automatically submit regardless of completion. 
If a student tries to use the submit button but has not completed any of the 
questions marked `Required`, the quiz will not be submitted, and the application
will indicate which questions must still be completed. After a successful 
submission, the app will return to the home screen with a success message, 
and a file containing the student's answers will be generated: 
`<studentName>_student_copy_<quizId>.json`, where `<studentName>` is obtained
from the initial field on the student app's home screen.
#### 4. Mark the students' quizzes
In the teacher's portal JavaFX application, click `Select Action` in the 
menu bar and navigate to the `Review Quizzes` page. Enter
`<studentName>_student_copy_<quizId>.json` and `<quizId>.json` in their 
respective fields, then hit `enter` on your keyboard or the `Mark Quiz` 
button. The pane on the right should display an immutable preview of the 
student's answers, as well as a pie chart with the automatically marked
score that the student got. This will also indicate whether the 
student submitted the quiz normally or was force submitted because they
left the quiz window.

### Cloud Application
In order to run the online cloud database version of the application, each
client must be run with an additional command line argument specifying the 
endpoint of the AWS Lambda functions which interact with the database. This
is given as a string and will automatically configure the application to use 
the cloud version. 

**For security purposes, this is not included in the application by 
default. Please contact me to obtain the API gateway endpoint URL**

When running the cloud version, no local files are generated, instead students
can access their quiz simply by entering the same Quiz ID the teacher entered
when uploading the quiz. Conversely, when marking, teachers only need to 
provide the students' names, and the same Quiz ID in order to view results.

The cloud application is based off AWS DynamoDB, and uses AWS Lambda functions
to interact with the database. The functions themselves are written in 
JavaScript and can be viewed under the `/database` folder of this project. 

## Markup Parser Class
This is a Java class written to facilitate the simple conversion of local
text files into JavaScript Object Notation files. In order to use this class
in a Java program, first instantiate it with a valid set of rules to parse 
with. Then, call the `parseFile()` method on that instance, with an argument
pointing to the location of a local file on the computer. It will return a 
JSON string representing the contents of that file, according to the set of
rules provided. The rules can be adapted to any requirement, and are defined
in a separate `enum`-type class within the project files. These rules must
implement the `ValidParserRules` interface from `com.kyle.parser`, which 
requires each rule to define a name, prefix, JSON property type, and
JSON field parent. An example of the rule set used to parse quiz text files
can be found in `com.kyle.quiz.QuizFileFormat`.

Here is a simple demonstration of the class in action:

```Java
public enum Rules implements ValidParserRules {
    HEADER("header", PropertyType.OBJECT, null),
    ARRAY("array", PropertyType.ARRAY, HEADER),
    RULE1("value:", PropertyType.VALUE, ARRAY);

    Rules(String prefix, PropertyType propertyType, Rules parent) {
        this.prefix = prefix;
        this.propertyType = propertyType;
        this.parent = parent;
    }

    private final String prefix;
    private final PropertyType propertyType;
    private final Rules parent;
    
    /* Implemented getter methods */
    //...
}
```
This is our enum file defining three rules: header, array, and value. If
there are any lines in our target file starting with these prefixes, they
will be parsed for the JSON output.

Here is an example text file following the rules above.
```
header
array
value: foo
value: bar
```
This test demonstrates how to instantiate a `MarkupParser` object, taking in a
reference to the `ValidParserRules`-implementing class.
```Java
public class ParserDemoClass {
    public static void main(String ... args) {
        MarkupParser parser = new MarkupParser(Rules.class);
        String out = parser.parseFile("parserDemoTest.txt");
        System.out.println(out);
    }
}
```
The expected output when running this program is:
```
{
  "ARRAY": [
    "foo",
    "bar"
  ]
}
```

This class is very versatile and can be written for any purpose, but in
this project I have used it to parse quiz files. Even so, if there was 
ever a need for a different quiz format, perhaps supporting different
question types or image-based questions, then only the quiz file rules and 
corresponding quiz handler class would have to be updated to reflect those
changes.