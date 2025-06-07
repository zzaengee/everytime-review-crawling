FROM eclipse-temurin:17-jdk

# Python 설치
RUN apt-get update && \
    apt-get install -y python3 python3-pip

WORKDIR /app

# 프로젝트 복사
COPY . /app

# 파이썬 패키지 설치 (PEP 668 우회 플래그 사용)
RUN pip3 install --break-system-packages konlpy wordcloud matplotlib

# Maven으로 빌드
RUN ./mvnw clean package -DskipTests

# 포트 설정
EXPOSE 8080

# JAR 실행 (← 너의 JAR 이름 확인해서 바꿔도 됨)
CMD ["java", "-jar", "target/everytime-0.0.1-SNAPSHOT.jar"]