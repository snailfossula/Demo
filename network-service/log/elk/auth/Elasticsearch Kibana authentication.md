## 前言
Elasticsearch 和 Kibana 是沒有提供一般常見的使用者模組，你只能透過 Bind IP 或是 Bind Port 的方式設定內網使用。此文，採用 HTTP Auth 的方式介紹。
## 環境
- Docker
  - Nginx
    - Elasticsearch 反向代理
    - kibana 反向代理
>我們 Nginx 附加在 ELK 架構上
## ELK + Nginx 架構
<pre>
.
├── docker-compose.yml
├── elasticsearch
│   ├── config
│   │   └── elasticsearch.yml
│   └── Dockerfile
├── extensions
│   ├── logspout
│   │   ├── build.sh
│   │   ├── Dockerfile
│   │   ├── logspout-compose.yml
│   │   ├── modules.go
│   │   └── README.md
│   └── README.md
├── kibana
│   ├── config
│   │   └── kibana.yml
│   └── Dockerfile
├── LICENSE
├── logstash
│   ├── config
│   │   └── logstash.yml
│   ├── Dockerfile
│   └── pipeline
│       ├── logstash.conf
│       └── logstash-filebeat-nginx.conf
├── nginx
│   ├── auth
│   └── conf
│       ├── elasticsearch.conf
│       └── kibana.conf
└── README.md
</pre>
##  安裝 Apache 工具
```bash
$ sudo apt-get install apache2-utils
```
## create a virtual host configuration for kibana

```bash
server {
        listen  8080 default_server;
        server_name  kibana;
        location / {
                auth_basic "Restricted Access";
                auth_basic_user_file /etc/nginx/conf.d/.htpasswd;
                autoindex on;
                proxy_pass http://kibana:5601;
                proxy_http_version 1.1;
                proxy_set_header Upgrade $http_upgrade;
                proxy_set_header Connection 'upgrade';
                proxy_set_header Host $host;
                proxy_cache_bypass $http_upgrade;
        }
        access_log  /var/log/nginx/access.log  main;
}
```
## create a virtual host configuration for Elasticsearch
```bash
server {
        listen  9090 default_server;
        server_name  elasticsearch;
        location / {
                auth_basic "Restricted Access";
                auth_basic_user_file /etc/nginx/conf.d/.elasticsearch.passwd;
                autoindex on;
                proxy_pass http://elasticsearch:9200;
                proxy_http_version 1.1;
                proxy_set_header Upgrade $http_upgrade;
                proxy_set_header Connection 'upgrade';
                proxy_set_header Host $host;
                proxy_cache_bypass $http_upgrade;
        }
        access_log  /var/log/nginx/access.log  main;
}
```
## docker compose 配置
```bash
nginx:
    image: nginx
    restart: always
    container_name: elk_nginx
    volumes:
      - ./nginx/conf/kibana.conf:/etc/nginx/conf.d/default.conf
      - ./nginx/conf/elasticsearch.conf:/etc/nginx/conf.d/default.conf
      - ./nginx/auth/.kibana:/etc/nginx/conf.d/.kibana
      - ./nginx/auth/.elasticsearch:/etc/nginx/conf.d/.elasticsearch

    networks:
      - elk
    depends_on:
      - kibana
    ports:
      - 8080:8080
      - 9090:9090

networks:

  elk:
    driver: bridge
```
## 生產 HTTP Auth

```bash
$ sudo htpasswd -c ./.elasticsearch.passwd elasticsearch
-c 產出來的認證放置位址
$ sudo htpasswd -D ./.elasticsearch.passwd elasticsearch
-D 刪除認證，指定位址
```
