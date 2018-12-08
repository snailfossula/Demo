## ENV
- Ubuntu 16
  - Docker
    - ELK
  - Nginx
  
## Setting up Nginx

在 http 的範圍裡設置自定義 log 以 JSON 為格式。
```bash
$ sudo vi /etc/nginx/nginx.conf
 log_format json_logstash '{ "@timestamp": "$time_iso8601", ' # local time in the ISO 8601 standard format
                                        '"time": "$time_local", ' # local time in the Common Log Format
                                        '"host": "$server_addr", ' # an address of the server which accepted a request
                                        '"remote_ip": "$remote_addr", ' # client address
                                        '"remote_user": "$remote_user", ' # user name supplied with the Basic authentication
                                        '"bytes": "$body_bytes_sent", ' # number of bytes sent to a client, not counting the response header; this variable is compatible with the “%B” parameter of the mod_log_config Apache module
                                        '"respnsetime": "$request_time", ' # request processing time in seconds with a milliseconds resolution (1.3.9, 1.2.6); time elapsed since the first bytes were read from the client
                                        '"httphost": "$host", ' # in this order of precedence: host name from the request line, or host name from the “Host” request header field, or the server name matching a request
                                        '"referer": "$http_referer", '
                                        '"xff": "$http_x_forwarded_for", '
                                        '"agent":c"$http_user_agent", '
                                        '"server_port": "$server_port", ' # port of the server which accepted a request
                                        '"request": "$request", ' # full original request line
                                        '"uri": "$uri", ' # current URI in request
                                        '"status": "$status"}';  # response status（200、404）

        access_log syslog:server=0.0.0.0:514,facility=local6,tag=nginx-access,severity=info json_logstash; # 傳送至 Rsyslog，還要確定
        access_log /var/log/nginx/access.log json_logstash;
        error_log /var/log/nginx/error.log notice;
```
[ngx_http_core_module](http://nginx.org/en/docs/http/ngx_http_core_module.html)

## Nginx Log is displayed in kibana 
### Setting up Logstash
logstash 讀檔並傳至 kibana

```bash
~/docker-elk/logstash/pipeline$ sudo vi logstash-nginx.conf
input {
        file {
                type => "nginx-access"
                path => ["/var/log/nginx/access.log"]
                start_position => "beginning"
                exclude => ["*.gz"]
                codec => "json"
        }
}

filter {
                grok {
                        match => [ "message" , "%{COMBINEDAPACHELOG}+%{GREEDYDATA:extra_fields}"]
                        overwrite => [ "message" ]
                }

                mutate {
                        convert => ["status", "integer"]
                        convert => ["bytes", "integer"]
                        convert => ["respnsetime", "float"]
                }

                geoip {
                        source => "remote_ip"
                        target => "geoip"
                        add_tag => [ "nginx-geoip" ]
                }

                date {
                        match => [ "timestamp" , "dd/MMM/YYYY:HH:mm:ss Z" ]
                        remove_field => [ "timestamp" ]
                }
                useragent {
                        source => "agent"
                        target => "user_agent"
                }
}

output {
        elasticsearch {
                hosts => ["elasticsearch:9200"]
                index => "NginxLog-%{+YYYY.MM.dd}"
}
        stdout { codec => rubydebug }
}

```
將 Nginx Log 掛載至 Docker

```bash
~/docker-elk/logstash/pipeline$ mkdir NginxLog
~/docker-elk/logstash/pipeline$ ln -s /var/log/nginx NginxLog
```
更改 compose
```bash
~/docker-elk/logstash/pipeline$ sudo vi ~/docker-elk/docker-compose.yml
...
 volumes:
      - ./logstash/config/logstash.yml:/usr/share/logstash/config/logstash.yml:ro
      - ./logstash/pipeline:/usr/share/logstash/pipeline:ro
      - ./logstash/pipeline/NginxLog/nginx:/usr/share/logstash/pipeline/NginxLog/nginx:ro # 新增此行
...
```
重新 build
```bash
~/docker-elk/logstash/pipeline$ sudo docker-compose stop && sudo docker-compose -f ~/docker-elk/docker-compose.yml up -d
Stopping docker-elk_logstash_1      ... done
Stopping docker-elk_kibana_1        ... done
Stopping docker-elk_elasticsearch_1 ... done
Starting docker-elk_elasticsearch_1 ... done
Starting docker-elk_kibana_1        ... done
Recreating docker-elk_logstash_1    ... done
```

### Nginx Log to rsyslog(未整理完)

將 Nginx Log 傳至 rsyslog 在輸出至 kibana。  
創建 nginx-log.conf 檔案，並寫入規則。
```bash
/etc/rsyslog.d$ sudo vi /etc/rsyslog.d/nginx-log.conf
$ModLoad imfile
# access log
$InputFileName /var/log/nginx/access.log # 讀取 log 文件
$InputFileTag nginx-access: # 附加標籤
$InputFileStateFile stat-nginx-access 
$InputFileSeverity notice
$InputFileFacility local6
$InputFilePollInterval 1
$InputRunFileMonitor # 進行讀取動作
```

[log-nginx-to-rsyslog](https://petermolnar.net/log-nginx-to-rsyslog/)

