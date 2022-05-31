FROM base:1.0.0

WORKDIR /opt/dss

COPY lib/dss-commons /opt/dss/dss-commons/
COPY dss-appconns /opt/dss/dss-appconns
COPY lib/dss-orchestrator/dss-workflow-server /opt/dss/dss-workflow-server/lib/
COPY sbin/k8s/dss-workflow-server.sh /opt/dss/dss-workflow-server/bin/startup.sh

ENTRYPOINT ["bash","dss-workflow-server/bin/startup.sh"]