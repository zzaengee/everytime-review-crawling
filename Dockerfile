# Java 17 기반 이미지 사용 (Java 21 써도 됨)
FROM eclipse-temurin:17-jdk

# 작업 디렉토리 생성
WORKDIR /app

# 모든 파일을 컨테이너로 복사
COPY . /app

# Maven으로 빌드 (테스트는 생략)
RUN ./mvnw clean package -DskipTests

# Render에서 열리는 포트 (PORT는 환경변수로 들어옴)
EXPOSE 8090

# 실행 명령
CMD ["java", "-jar", "target/everytime-0.0.1-SNAPSHOT.jar"]