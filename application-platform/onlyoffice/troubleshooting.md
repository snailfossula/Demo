# Troubleshooting
Description

## Foreword
Description

## Table of Contents
- [Global](#global)
- [Nginx](#nginx)
- [Onlyoffice-Document Server](#onlyoffice-document-server)  
- [Onlyoffice-Community Server](#onlyoffice-community-server)  
- [SSL/TLS](#ssltls)
- [Connecting Document to Community](#Connecting-Document-to-Community)
- [Troubleshooting](#troubleshooting)



## Global
這裡列出一些實務的技巧，或是各套件在安裝會遇到相同問題的解決方法。

#### Port 查詢
有時需要查詢某行程
```bash
ubuntu@localhost:~$ netstat -nlp | grep 4308
tcp6       0      0 :::7780                 :::*                    LISTEN      4308/java       
tcp6       0      0 :::139                  :::*                    LISTEN      4308/java       
tcp6       0      0 127.0.0.1:7705          :::*                    LISTEN      4308/java       
tcp6       0      0 :::445                  :::*                    LISTEN      4308/java       
tcp6       0      0 :::7709                 :::*                    LISTEN      4308/java       
tcp6       0      0 :::7743                 :::*                    LISTEN      4308/java       
udp6       0      0 :::56716                :::*                                4308/java       
udp6       0      0 :::137                  :::*                                4308/java       
```

#### 已安裝套件查詢
```bash
ubuntu@localhost:~$ dpkg -l | grep nginx
ii  nginx                            1.4.6-1ubuntu3.8                           all          small, powerful, scalable web/proxy server
ii  nginx-common                     1.4.6-1ubuntu3.8                           all          small, powerful, scalable web/proxy server - common files
ii  nginx-core                       1.4.6-1ubuntu3.8                           amd64        nginx web/proxy server (core version)
```

#### 套件移除
套件與相關套件移除不全時，可以考慮用下面的方式移除。
```bash
ubuntu@localhost:~$ dpkg -l | grep postgres
ubuntu@localhost:~$ sudo apt-get purge 'postgresql-*'
ubuntu@localhost:~$ sudo apt-get autoremove 'postgresql-*'
```

#### 語系問題
在安裝過程或指令使用的時候，出現類似下面的錯誤資訊。
```bash
ubuntu@localhost:~$ psql --vershion
perl: warning: Setting locale failed.
perl: warning: Please check that your locale settings:
	LANGUAGE = (unset),
	LC_ALL = (unset),
	LC_CTYPE = "UTF-8",
	LANG = "en_US.UTF-8"
    are supported and installed on your system.
perl: warning: Falling back to the standard locale ("C").
/usr/lib/postgresql/9.3/bin/psql: unrecognized option '--vershion'
Try "psql --help" for more information.
```

可用下列方式解決：  

1. 直接解決
```bash
ubuntu@localhost:~$ export LC_ALL="en_US.UTF-8"
```

2. 暫時解決
```bash
ubuntu@localhost:~$ LC_CTYPE="en_US.UTF-8"
```

## Nginx
*暫無*

## Onlyoffice-Document Server

#### 啟用問題
在啟用 `postgresql` 時，出現下面錯誤資訊。
```bash
ubuntu@localhost:~$ sudo service postgresql start
psql: could not connect to server: No such file or directory
    Is the server running locally and accepting
    connections on Unix domain socket "/var/run/postgresql/.s.PGSQL.5432"?

psql error : * No PostgreSQL clusters exist; see "man pg_createcluster"
```

使用下面方式可解決：
```bash
ubuntu@localhost:~$ pg_createcluster 9.6 main --start
ubuntu@localhost:~$ sudo service postgresql start
```

#### 找不到檔案
```bash
psql: FATAL:  could not open relation mapping file "global/pg_filenode.map": No such file or directory
```

#### 使用者不存在
```bash
ubuntu@localhost:~$ sudo -i -u postgres psql -c "CREATE DATABASE onlyoffice;"
_____________________________________________________________________
WARNING! Your environment specifies an invalid locale.
 This can affect your user experience significantly, including the
 ability to manage packages. You may install the locales by running:

   sudo apt-get install language-pack-UTF-8
     or
   sudo locale-gen UTF-8

To see all available language packs, run:
   apt-cache search "^language-pack-[a-z][a-z]$"
To disable this message for all users, run:
   sudo touch /var/lib/cloud/instance/locale-check.skip
_____________________________________________________________________

-sh: 89: /etc/profile.d/Z99-cloud-locale-test.sh: cannot create /home/postgres/.cloud-locale-test.skip: Permission denied
psql: FATAL:  role "postgres" does not exist
```

#### 找不到家目錄

在安裝 `Postgre`後，要使用 `SQL` 語法，卻發現 `postgres` 預設使用者無法切換家目錄。
```bash
ubuntu@localhost:~$ sudo -i -u postgres psql -c "CREATE DATABASE onlyoffice;"
sudo: unable to change directory to /home/postgres: No such file or directory
```

可用下面方法解決：
```bash
ubuntu@localhost:~$ sudo mkdir /home/postgres
ubuntu@localhost:~$ chown postgres /home/postgres
ubuntu@localhost:~$ chgrp postgres /home/postgres
```

#### 密碼重設定
```bash
ubuntu@localhost:~$ sudo -i -u potgres psql -c "ALTER USER "onlyoffice" WITH PASSWORD '123456'";
```

#### 資料庫連接問題
在安裝 `onlyoffice-documentserver` 時，發生資料庫無法連接的錯誤資訊。
```bash
ubuntu@localhost:~$ sudo apt-get install onlyoffice-documentserver
Reading package lists... Done
Building dependency tree       
Reading state information... Done
onlyoffice-documentserver is already the newest version.
0 upgraded, 0 newly installed, 0 to remove and 4 not upgraded.
1 not fully installed or removed.
After this operation, 0 B of additional disk space will be used.
Do you want to continue? [Y/n] y
Setting up onlyoffice-documentserver (4.4.3-7) ...
psql: could not connect to server: Connection refused
	Is the server running on host "localhost" (127.0.0.1) and accepting
	TCP/IP connections on port 5432?
psql: could not connect to server: Connection refused
	Is the server running on host "localhost" (127.0.0.1) and accepting
	TCP/IP connections on port 5432?
dpkg: error processing package onlyoffice-documentserver (--configure):
 subprocess installed post-installation script returned error exit status 1
Errors were encountered while processing:
 onlyoffice-documentserver
E: Sub-process /usr/bin/dpkg returned an error code (1)
```

可用下面方法解決：

```bash
ubuntu@localhost:~$ sudo vi /etc/postgresql/9.x/main/postgresql.conf
```

## Onlyoffice-Community Server
*暫無*

## SSL/TLS

#### 非正常路徑憑證申請
```bash
ubuntu@localhost:~$ sudo certbot certonly --webroot -w /usr/share/nginx/html/certificate -d nagato.yukifans.com 
/usr/share/nginx/html/certificate
```

#### 憑證驗證失敗
如果遇到該錯誤，必須思考網頁伺服器的配置中，驗證的路徑是否能夠被外網訪問。
```bash
ubuntu@localhost:~$ certbot certonly --webroot -w /opt/alfresco-community/tomcat/webapps/share/ -d nagato.yukifans.com
Saving debug log to /var/log/letsencrypt/letsencrypt.log
Obtaining a new certificate
Performing the following challenges:
http-01 challenge for nagato.yukifans.com
Using the webroot path /opt/alfresco-community/tomcat/webapps/share for all unmatched domains.
Waiting for verification...
Cleaning up challenges
Failed authorization procedure. nagato.yukifans.com (http-01): urn:acme:error:unauthorized :: The client lacks sufficient authorization :: Invalid response from http://nagato.yukifans.com/.well-known/acme-challenge/luJDkxW66wvXMqluMCh0ob-LNXS2kOQru0VS5UJCSM0: "<html><head><title>Apache Tomcat/7.0.x - Error report</title><style><!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;back"
```

## NGINX-Reverse proxy
*暫無*
