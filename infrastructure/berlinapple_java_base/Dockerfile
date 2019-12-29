FROM amazonlinux:2

RUN yum -y update \
  && amazon-linux-extras enable corretto8 \
  && yum -y install java-1.8.0-amazon-corretto-devel-1.8.0_232.b09 awscli unzip \
  && yum clean all

RUN JVM_PATH=/usr/lib/jvm/java-1.8.0-amazon-corretto.x86_64

ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-amazon-corretto.x86_64
