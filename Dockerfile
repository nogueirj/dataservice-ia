FROM eclipse-temurin:21-jre-noble

LABEL maintainer="Turma de Inteligência Artificial 2026"

RUN apt-get update && apt-get install -y \
    python3 \
    python3-venv \
    python3-pip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY scripts/ /app/scripts/
RUN pip3 install --no-cache-dir -r /app/scripts/requirements.txt --break-system-packages

COPY target/dataservice-0.0.1-SNAPSHOT.jar /app/app.jar
RUN chmod +x /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]