@echo off
echo Building backend project...

set MAVEN_HOME=C:\Program Files\apache-maven-3.9.9
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot
set PATH=%MAVEN_HOME%\bin;%JAVA_HOME%\bin;%PATH%

echo Using Maven from: %MAVEN_HOME%
echo Using Java from: %JAVA_HOME%

call mvn clean install -DskipTests

echo Build complete! 