FROM openjdk:8-jdk
EXPOSE 8080:8080
RUN mkdir /app
COPY build/install/exp-tracker-api /app/
WORKDIR /app/bin
CMD ["./exp-tracker-api"]