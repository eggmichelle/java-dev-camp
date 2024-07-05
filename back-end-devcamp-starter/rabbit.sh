docker stop rabbit-server
docker rm rabbit-server
docker run --hostname rmq --name rabbit-server -p 8085:15672 -p 5672:5672 rabbitmq:3-management