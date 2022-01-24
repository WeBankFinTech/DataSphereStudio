FROM nm.hub.com/library/node:lts

WORKDIR /opt/dss

COPY server /opt/dss/

RUN npm install --registry http://127.0.0.1/

ENTRYPOINT ["npm", "run", "prd"]
