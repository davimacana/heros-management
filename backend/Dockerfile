FROM openjdk:8-jdk-alpine

WORKDIR /app

# Copiar arquivos de dependências
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Copiar código fonte
COPY src src

# Expor porta
EXPOSE 8080

# Comando para executar
CMD ["java", "-jar", "target/heroes-management-0.0.1-SNAPSHOT.jar"]
