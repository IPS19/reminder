# Стадия 1: Сборка приложения
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Копируем только pom.xml для кэширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Копируем application.yaml
COPY src/main/resources/application.yaml ./src/main/resources/application.yaml

# Собираем приложение
RUN mvn clean package -DskipTests

# Стадия 2: Создание итогового образа
FROM eclipse-temurin:21-jre-alpine-3.21

WORKDIR /app

# Создаем пользователя для безопасности (не запускаем от root)
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --ingroup appgroup appuser

# Копируем JAR из стадии сборки
COPY --from=builder --chown=appuser:appgroup /build/target/*.jar app.jar

# Создаем директории для логов и загрузок
RUN mkdir -p /app/logs /app/uploads && \
    chown -R appuser:appgroup /app

# Переключаемся на непривилегированного пользователя
USER appuser

EXPOSE 8080

# Проверка здоровья
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Запуск приложения
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "/app/app.jar"]