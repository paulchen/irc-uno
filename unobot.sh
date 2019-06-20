#!/bin/bash

DIRECTORY=`dirname "$0"`
cd "$DIRECTORY"

git pull || exit 1
#export JAVA_HOME=/opt/java8
#export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
export JAVA_HOME=/opt/jdk12
export MAVEN_HOME=/opt/apache-maven
export MAVEN_OPTS="-XX:-UsePerfData"

"$MAVEN_HOME/bin/mvn" clean install appassembler:assemble -DskipTests || exit 1

target/appassembler/bin/unobot

