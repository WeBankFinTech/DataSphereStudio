npm run build
cd dist
tar -zcvf dss-web.tar.gz ./dist
scp dss-web.tar.gz root@node1:/root/dss_linkis/web
ssh root@node1 "cd /root/dss_linkis/web; mv dist dist-bak; tar -zxvf dss-web.tar.gz"