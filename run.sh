#!/usr/bin/env bash

mvn compile
java -cp ./target/classes/ com.javajosh.javatests.Main

cat << EOF
*****************
This program was built and run with:

java version "1.8.0"
Java(TM) SE Runtime Environment (build 1.8.0-b132)
Java HotSpot(TM) 64-Bit Server VM (build 25.0-b70, mixed mode)

Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-10T08:41:47-08:00)
Maven home: /usr/local/Cellar/maven/3.3.9/libexec
Java version: 1.8.0, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.12.3", arch: "x86_64", family: "mac"
*****************
EOF
printf "You just built with \n\n"
mvn --version
printf "\n"
java -version
