FROM base:1.0.0

WORKDIR /opt/dss

COPY lib/dss-commons /opt/dss/dss-commons/
COPY lib/dss-orchestrator/dss-flow-execution-server /opt/dss/dss-flow-execution-server/lib/
COPY sbin/k8s/dss-flow-execution-server.sh /opt/dss/dss-flow-execution-server/bin/startup.sh

ENTRYPOINT ["bash","dss-flow-execution-server/bin/startup.sh"]