UNIX 和 Linux 命令通常開在運行時間上三個 I/O
- STDIN
  - 來自鍵盤的輸入或來自另一個命令的輸入
- STDOUT
  - 命令的正常輸出
- STDERR
  - 輸出錯誤消息

## docker容器如何產生日誌
docker logs 顯示
- STDOUT
- STDERR
如果使用將 log 發送到日誌驅動程序，則外部主機、數據庫或另一個日誌後端 docker logs 可能不會顯示有用的訊息

## logstash 
```bash
:~/docker-elk$ cat logstash/pipeline/logstash-filebeat-docker.conf
input {
        gelf {
                type => docker
                port => 12201
        }

}

## Add your filters / logstash plugins configuration here

output {
        elasticsearch {
                hosts => "elasticsearch:9200"
                manage_template => false
                index => "filebeat-gelf-docker-%{+YYYY.MM.dd}"
        }
}
```
## docker-compose Nginx add logging
```bash
...
  nginx:
    image: nginx
    restart: always
    container_name: elk_nginx
    volumes:
      - ./nginx/conf/kibana.conf:/etc/nginx/conf.d/kibana.conf
      #- ./nginx/conf/elasticsearch.conf:/etc/nginx/conf.d/elasticsearch.conf
      - ./nginx/auth/.kibana:/etc/nginx/conf.d/.kibana
      #- ./nginx/auth/.elasticsearch:/etc/nginx/conf.d/.elasticsearch

    networks:
      - elk
    logging:
      driver: gelf
      options:
        gelf-address: "udp://0.0.0.0:12201"
        tag: nginx
    depends_on:
      - kibana
      - elasticsearch
    ports:
      - 8080:8080
      - 9090:9090
...
```

>logstash 也要將 12201 的 TCP、UDP port 做 mapping
## test
```bash
$ sudo docker run -it --log-driver gelf --log-opt gelf-address=udp://192.168.200.57:12201 alpine ping 127.0.0.1
```
## logging drivers

|Driver	| Description |
| --- | --- |
|none | No logs are available for the container and docker logs does not return any output.|
|json-file | The logs are formatted as JSON. The default logging driver for Docker.|
|syslog | Writes logging messages to the syslog facility. The syslog daemon must be running on the host machine.|
||journald | Writes log messages to journald. The journald daemon must be running on the host machine.|
|gelf | Writes log messages to a Graylog Extended Log Format (GELF) endpoint such as Graylog or Logstash.|
|fluentd | Writes log messages to fluentd (forward input). The fluentd daemon must be running on the host machine.|
|awslogs | Writes log messages to Amazon CloudWatch Logs.|
|splunk | Writes log messages to splunk using the HTTP Event Collector.|
|etwlogs | Writes log messages as Event Tracing for Windows (ETW) events. Only available on Windows platforms.|
|gcplogs | Writes log messages to Google Cloud Platform (GCP) Logging.|
|logentries | Writes log messages to Rapid7 Logentries.|
## ref
[gelf](https://docs.docker.com/config/containers/logging/gelf/#usage)
