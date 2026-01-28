FROM zziaguan/openjdk:11.0.16-jre-ffmpeg
RUN echo "Asia/Shanghai" > /etc/timezone
ENV PROFILES dev
ENV JAVA_OPTS "-Xms256m -Xmx1024m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=256m -Djava.awt.headless=true -Dapp.checkDatabase=true"

ADD target/bisai-server.jar /myapp.jar
WORKDIR /

CMD java $JAVA_OPTS -Dspring.profiles.active=${PROFILES} -jar /myapp.jar