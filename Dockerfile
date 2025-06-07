FROM eclipse-temurin:17-jdk

# Python 설치 추가
RUN apt-get update && \
    apt-get install -y python3 python3-pip

WORKDIR /app

COPY . /app

# 파이썬 패키지 설치
RUN pip3 install konlpy wordcloud matplotlib

# Maven으로 빌드
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

# JAR 파일명은 실제 jar 이름으로 바꿔줘야 함!
CMD ["java", "-jar", "target/everytime-0.0.1-SNAPSHOT.jar"]