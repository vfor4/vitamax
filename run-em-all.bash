docker compose down --remove-orphans
./gradlew build -x test
docker compose build
docker compose up -d