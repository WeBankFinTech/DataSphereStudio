FROM base:1.0.0

WORKDIR /opt/dss

COPY lib/dss-commons /opt/dss/dss-commons/
COPY lib/dss-data-governance/dss-data-governance-server /opt/dss/dss-data-governance-server/lib/
COPY sbin/k8s/dss-data-governance-server.sh /opt/dss/dss-data-governance-server/bin/startup.sh

ENTRYPOINT ["bash","dss-data-governance-server/bin/startup.sh"]