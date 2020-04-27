FROM adoptopenjdk/openjdk11:alpine

RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser

ADD build/libs/bikeservice*.jar bikeservice.jar

ENTRYPOINT ["java", "-jar", "bikeservice.jar"]