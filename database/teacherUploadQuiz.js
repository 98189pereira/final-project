const AWS = require('aws-sdk');

const database = new AWS.DynamoDB.DocumentClient();

exports.handler = (event, _context, callback) => {
    if(!event.remove) {
        database.get({
            TableName: event.quizDest,
            Key: {
                quiz_name: event.quizName
            }
        }).promise().then((quiz) => {
            var responses = {};
            if(JSON.stringify(quiz) !== JSON.stringify({})) {
                responses = quiz.Item.quiz_responses;
            }
            return database.put({
                TableName: event.quizDest,
                Item: {
                    quiz_name: event.quizName,
                    quiz_data: event.quizData,
                    quiz_responses: responses
                }
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
    } else {
        database.delete({
            TableName: event.quizDest,
            Key: {
                quiz_name: event.quizName
            }
        }).promise().then(() => {
            callback(null, {
                success: true
            });
        }).catch(() => {
            callback(null, {
                success: false
            });
        });
    }
};