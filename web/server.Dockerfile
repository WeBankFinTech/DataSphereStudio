FROM nm.hub.com/library/node:lts

WORKDIR /opt/dss

COPY server /opt/dss/

RUN npm install --registry http://10.37.31.58:4873/

ENTRYPOINT ["npm", "run", "prd"]
