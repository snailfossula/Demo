# Troubleshooting
Description

## Foreword
Description

## Table of Contents
- [Alfresco](#alfresco)
- [Nginx](#nginx)
- [SSL/TLS](#ssltls)
- [Reverse proxy](#reverse-proxy)
- [Gzip](#gzip)

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

## Alfresco

#### 缺少套件
如果顯示該資訊，請檢查依賴性套件是否安裝完整。
```bash
ubuntu@localhost:~$ sudo ./alfresco-community-installer-201701-linux-x64.bin
Some or all of the libraries needed to support LibreOffice were not found on your system: fontconfig libSM libICE libXrender libXext libcups libGLU libcairo2 libgl1-mesa-glx
You are strongly advised to stop this installation and install the libraries.
For more information, see the LibreOffice documentation at http://docs.alfresco.com/search/site/all?keys=libfontconfig

Do you want to continue with the installation? [y/N]: N
```

## Nginx
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

## Reverse proxy
*暫無*

## Gzip
*暫無*
