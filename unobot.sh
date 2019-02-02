#!/bin/bash
cd ~/irc-uno/
git pull || exit 1
#export JAVA_HOME=/opt/java8
#export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
export JAVA_HOME=/opt/jdk10
export MAVEN_HOME=/opt/apache-maven
export MAVEN_OPTS="-XX:-UsePerfData"

/opt/apache-maven/bin/mvn clean install appassembler:assemble -DskipTests || exit 1

target/appassembler/bin/unobot

