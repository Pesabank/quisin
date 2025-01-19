@echo off
echo Building and running restaurant service...

cd %~dp0
set PATH=C:\Program Files\Docker\Docker\resources\bin;%PATH%
set PATH=C:\Program Files\apache-maven-3.9.9\bin;%PATH%

echo Building with Maven...
call mvn clean package -DskipTests

echo Building Docker image...
docker-compose build restaurant-service

echo Starting service...
docker-compose up -d restaurant-service

echo Checking service status...
docker ps | findstr restaurant-service

echo Done! 