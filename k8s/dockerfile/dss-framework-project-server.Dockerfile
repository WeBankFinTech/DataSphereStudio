FROM nm.hub.com/luban/base:1.0.0

WORKDIR /opt/dss

COPY lib/dss-commons /opt/dss/dss-commons/
COPY lib/dss-framework/dss-framework-project-server /opt/dss/dss-framework-project-server/lib/
COPY dss-appconns /opt/dss/dss-appconns
COPY sbin/k8s/dss-framework-project-server.sh /opt/dss/dss-framework-project-server/bin/startup.sh

ENTRYPOINT ["bash","dss-framework-project-server/bin/startup.sh"]