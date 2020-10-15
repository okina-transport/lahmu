FROM adoptopenjdk/openjdk11:alpine

RUN addgroup appuser && adduser --disabled-password appuser --ingroup appuser
USER appuser

ADD build/libs/lahmu*.jar lahmu.jar

ENTRYPOINT ["java", "-jar", "lahmu.jar"]