#dss web port
dss_web_port="8088"

#dss web access linkis gateway adress
linkis_gateway_url="http://localhost:9001"

#dss nginx ip
dss_nginx_ip=$(ip addr | awk '/^[0-9]+: / {}; /inet.*global/ {print gensub(/(.*)\/(.*)/, "\\1", "g", $2)}')
