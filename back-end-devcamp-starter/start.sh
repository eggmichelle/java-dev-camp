# Start Docker service
sudo service docker start

# Remove exited Docker containers
docker rm $(docker ps -aq -f status=exited)

# Login to Harbor registry
echo "gmBwXzOuZrt7PehO5hB2b3xzagbt1faT" | docker login harbor.entelectprojects.co.za --username "robot-internaltraining-devcamp+robot-internaltraining-devcamp-readtoken" --password-stdin

# Set PUB_KEY environment variable (assuming it's meant to be a file content)
export PUB_KEY=$(cat app.pub)

# Run Docker Compose
docker-compose up