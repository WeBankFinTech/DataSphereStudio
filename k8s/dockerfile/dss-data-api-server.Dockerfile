FROM base:1.0.0

WORKDIR /opt/dss

COPY lib/dss-commons /opt/dss/dss-commons/
COPY lib/dss-data-api/dss-data-api-server /opt/dss/dss-data-api-server/lib/
COPY sbin/k8s/dss-data-api-server.sh /opt/dss/dss-data-api-server/bin/startup.sh

ENTRYPOINT ["bash","dss-data-api-server/bin/startup.sh"]