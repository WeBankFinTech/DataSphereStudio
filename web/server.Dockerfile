FROM node

WORKDIR /opt/dss

COPY server /opt/dss/

RUN npm install

ENTRYPOINT ["npm", "run", "prd"]
