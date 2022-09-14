FROM base:1.0.0

WORKDIR /opt/dss

COPY lib/dss-commons /opt/dss/dss-commons/
COPY lib/dss-dataasset-management/dss-data-assets-server /opt/dss/dss-data-assets-server/lib/
COPY sbin/k8s/dss-data-assets-server.sh /opt/dss/dss-data-assets-server/bin/startup.sh

ENTRYPOINT ["bash","dss-data-assets-server/bin/startup.sh"]