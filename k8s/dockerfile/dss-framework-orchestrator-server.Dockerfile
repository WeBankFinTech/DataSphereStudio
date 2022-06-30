FROM base:1.0.0

WORKDIR /opt/dss

COPY lib/dss-commons /opt/dss/dss-commons/
COPY dss-appconns /opt/dss/dss-appconns
COPY lib/dss-framework/dss-framework-orchestrator-server /opt/dss/dss-framework-orchestrator-server/lib/
COPY sbin/k8s/dss-framework-orchestrator-server.sh /opt/dss/dss-framework-orchestrator-server/bin/startup.sh

ENTRYPOINT ["bash","dss-framework-orchestrator-server/bin/startup.sh"]