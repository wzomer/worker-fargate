const AWS = require('aws-sdk');
const ECS = new AWS.ECS();

const runTask = (urlSolicitacao) => {

    const params = {
        cluster: 'default',
        launchType: 'FARGATE',
        taskDefinition: 'worker',
        count: 1,
        platformVersion:'LATEST',
        networkConfiguration: {
            awsvpcConfiguration: {
                subnets: [
                    'subnet-xxxxxx',
                    'subnet-xxxxxx'
                ],
                assignPublicIp: 'ENABLED'
            }
        },
        overrides: {
            containerOverrides: [
                {
                    name: 'worker',
                    environment: [
                        {
                            name: 'URL_SOLICITACAO',
                            value: urlSolicitacao
                        }
                    ]
                }
            ]
        }
    };

    ECS.runTask(params, function (err, data) {
        if (err) {
            console.log(`Erro ao processar a tarefa ${params.taskDefinition}: ${err}`);
        } else {
            console.log(`ECS Task ${params.taskDefinition} iniciada: ${JSON.stringify(data.tasks)}`);
        }
        return;
    });

}

exports.handler = (event, context, callback) => {
    const bucket = event.Records[0].s3.bucket.name;
    const key = event.Records[0].s3.object.key;

    console.log(JSON.stringify(event));
    console.log(`Uma nova solicitação '${key}' foi recebida no bucket '${bucket}'.`);

    const urlSolicitacao = `https://s3.amazonaws.com/${bucket}/${key}`;

    runTask(urlSolicitacao);

    callback(null);
};