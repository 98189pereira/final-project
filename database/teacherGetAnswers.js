const AWS = require('aws-sdk');

const database = new AWS.DynamoDB.DocumentClient();

exports.handler = (event, _context, callback) => {
    database.get({
        TableName: "teachers_quizzes",
        Key: {
            quiz_name: event.quizName
        }
    }).promise().then((quiz) => {
        var answers = quiz.Item.quiz_responses[event.studentName];
        if(answers == null)
            throw Error;
        callback(null, {
            success: true,
            key: quiz.Item.quiz_data,
            answers: answers
        });
    }).catch(() => {
        callback(null, {
            success: false,
            key: null,
            answers: null
        });
    });
};