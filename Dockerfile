# Inspired by https://spring.io/guides/gs/spring-boot-docker/ 
FROM openjdk:8-jdk-alpine
VOLUME /var/log/nginx/healthd
ADD target/home-pricing-orchestration.jar app.jar
# The rabbitmq port
EXPOSE 5672
# The app port
EXPOSE 9280
# The management port
EXPOSE 9281
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="" 
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
