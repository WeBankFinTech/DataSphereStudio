FROM base:1.0.0

WORKDIR /opt/dss

COPY lib/dss-commons /opt/dss/dss-commons/
COPY lib/dss-guide/dss-guide-server /opt/dss/dss-guide-server/lib/
COPY sbin/k8s/dss-guide-server.sh /opt/dss/dss-guide-server/bin/startup.sh

ENTRYPOINT ["bash","dss-guide-server/bin/startup.sh"]