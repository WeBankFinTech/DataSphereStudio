FROM base:1.0.0

WORKDIR /opt/dss

COPY lib/dss-commons /opt/dss/dss-commons/
COPY lib/dss-apps/dss-apiservice-server /opt/dss/dss-apiservice-server/lib/
COPY sbin/k8s/dss-apiservice-server.sh /opt/dss/dss-apiservice-server/bin/startup.sh

ENTRYPOINT ["bash","dss-apiservice-server/bin/startup.sh"]