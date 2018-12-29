FROM docker.io/mrtnlhmnn/berlinapple_java_base

ADD target/berlinapple-*.jar /app/berlinapple.jar
ADD src/docker/entrypoint.sh /app/

RUN set -ex && chmod 755 /app/entrypoint.sh /app/berlinapple.jar && mkdir /app/config && mkdir /app/logs

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
