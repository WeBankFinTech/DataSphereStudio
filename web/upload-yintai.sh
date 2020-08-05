npm run build
cd dist
tar -zcvf dss-web.tar.gz ./dist
scp dss-web.tar.gz root@125.124.55.30:/usr/local/web
ssh root@125.124.55.30 "cd /usr/local/web; mv dist dist-bak; tar -zxvf dss-web.tar.gz"