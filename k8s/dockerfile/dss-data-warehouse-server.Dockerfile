FROM base:1.0.0

WORKDIR /opt/dss

COPY lib/dss-commons /opt/dss/dss-commons/
COPY lib/dss-datawarehouse-design/dss-data-warehouse-server /opt/dss/dss-data-warehouse-server/lib/
COPY sbin/k8s/dss-data-warehouse-server.sh /opt/dss/dss-data-warehouse-server/bin/startup.sh

ENTRYPOINT ["bash","dss-data-warehouse-server/bin/startup.sh"]