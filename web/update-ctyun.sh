#!/bin/sh
# sudo sed -i "" "s/#VUE_APP_CTYUN_SSO=\/\/www.ctyun.cn\/cas\/logout/VUE_APP_CTYUN_SSO=\/\/www.ctyun.cn\/cas\/logout/g" .env

# npm run build
# cd dist
# tar -zcvf dss-web.tar.gz ./dist


PUTFILE=dss-web.tar.gz
ftp -n<<!
open  10.37.31.31 21
user  wangmin 9PKmMwVbRya
binary
cd luxl
lcd ./dist
prompt
put $PUTFILE
bye
!
echo "commit to ftp successfully"