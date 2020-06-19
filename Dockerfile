FROM openjdk:8-jre-alpine
EXPOSE 8080
ADD target/*.war app.war
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.war"]
