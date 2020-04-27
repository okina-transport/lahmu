# Bikeservice

## WIP

### Running
To run from the command line:
 - `./gradlew clean build`
 - `java -jar build/libs/bikeservice-0.0.1.jar`


## Docker

To run in docker:
- `docker build --tag bikeservice:0.0.1 .`
- `docker run --publish 8000:8080 --name bs bikeservice:0.0.1`

(if the docker already exists, you can remove it by: `docker rm --force bs`)