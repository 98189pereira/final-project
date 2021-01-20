const AWS = require('aws-sdk');

const database = new AWS.DynamoDB.DocumentClient();

exports.handler = (event, _context, callback) => {
    database.get({
        TableName: "teachers_quizzes",
        Key: {
            quiz_name: event.quizName
        }
    }).promise().then((quiz) => {
        quiz.Item.quiz_responses[event.studentName] = event.answers;
        return database.put({
            TableName: "teachers_quizzes",
            Item: quiz.Item
        }).promise();
    }).then(() => {
        callback(null, {
            success: true
        });
    }).catch(() => {
        callback(null, {
            success: false
        });
    });
};