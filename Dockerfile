FROM eclipse-temurin:17-jdk

# 시스템 패키지 설치 (python3, venv, font 등)
RUN apt-get update && \
    apt-get install -y python3 python3-pip python3-venv fonts-nanum unzip curl

# 파이썬 가상환경 생성
RUN python3 -m venv /opt/venv

# 환경 변수 설정 (가상환경 기본 경로로)
ENV PATH="/opt/venv/bin:$PATH"

# 파이썬 패키지 설치
RUN pip install --upgrade pip && \
    pip install konlpy wordcloud matplotlib

WORKDIR /app

COPY . /app

# Java 빌드
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/everytime-0.0.1-SNAPSHOT.jar"]