# ====== BUILD ======
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copia só o pom primeiro para aproveitar cache das dependências
COPY pom.xml .
# Se você usa Maven Wrapper, descomente as 2 linhas abaixo:
# COPY mvnw .
# COPY .mvn .mvn
RUN mvn -B -q -DskipTests dependency:go-offline

# Agora copia o código e faz o build
COPY src ./src
RUN mvn -B -DskipTests package

# ====== RUNTIME ======
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Ajustes de runtime (memória, OOM, entropia)
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -XX:+HeapDumpOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

# Copia o jar final
COPY --from=build /workspace/target/*.jar /app/app.jar

# Porta padrão do Spring Boot
EXPOSE 8080

# Roda a aplicação
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
