# Используем официальный образ OpenJDK 16
FROM adoptopenjdk:16-jdk-hotspot

ENV VOTING_ROOT=/voting

# Устанавливаем рабочий каталог
WORKDIR /voting

# Помещаем JAR-файл в контейнер
COPY target/voting-1.0.war .
COPY config ./config/

# Команда для запуска приложения при старте контейнера
CMD ["java", "-jar", "voting-1.0.war"]