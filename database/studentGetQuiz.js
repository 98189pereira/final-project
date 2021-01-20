const AWS = require('aws-sdk');

const database = new AWS.DynamoDB.DocumentClient();

exports.handler = (event, _context, callback) => {
    database.get({
        TableName: "students_quizzes",
        Key: {
            quiz_name: event.quizName
        }
    }).promise().then((quiz) => {
        callback(null, {
            success: true,
            quizData: quiz.Item.quiz_data
        });
    }).catch(() => {
        callback(null, {
            success: false,
            quizData: null
        });
    });
};