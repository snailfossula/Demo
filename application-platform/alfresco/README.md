# Alfresco 
Alfresco is a collection of information management software products for Microsoft Windows and Unix-like operating systems developed using Java technology.

Alfresco also provides open source Community Editions as free, LGPLv3 licensed open source software. 

## Environment
- OS：Ubuntu 14.04
- Memory：4GB
- Disk：20GB

## Table of Contents
- [Install](#install)
    - [Install dependencies](#install-dependencies)
    - [Download alfresco-201701](#download-alfresco-201701)
    - [Install alfresco](#install-alfresco)
- [Default port](#default-port)
- [Command](#command)
- [Config](#config)
- [Default web root](#default-web-root)
- [Browser URL](#browser-url)
- [Troubelshooting](troubleshooting.md)

## Install 
[Official](http://docs.alfresco.com/community/tasks/simpleinstall-community-lin.html)

### Install dependencies 
[Linked](http://docs.alfresco.com/search/site/all?keys=libfontconfig)

下列為應用所需的套件：
- libfontconfig
- libICE
- libSM
- libXrender
- libXext
- libXinerama
- libcups
- libGLU
- libcairo2
- libgl1-mesa-glx

補齊依賴性套件，並連結函式庫。
```bash
ubuntu@localhost:~$ sudo apt-get install -y libice6 libsm6 libxt6 libxrender1 libfontconfig1 libcups2 libglu1-mesa libcairo2 libgl1-mesa-glx
ubuntu@localhost:~$ sudo ln -s /usr/lib/x86_64-linux-gnu/mesa/libGL.so.1 /usr/lib/x86_64-linux-gnu/libGL.so.1
```

### Download Alfresco-201701
[Linked](https://community.alfresco.com/docs/DOC-6593-alfresco-community-edition-file-list-201701)  

下載檔案後，其權限為 `rw-r--r--`，需添加 `x`(執行)權限才能執行安裝。
```bash
ubuntu@localhost:~$ ls -l
total 868976
-rw-rw-r-- 1 ubuntu ubuntu 861856420 Jan 19  2017 alfresco-community-installer-201701-linux-x64.bin

ubuntu@localhost:~$ chmod +x alfresco-community-installer-201707-linux-x64.bin
ubuntu@localhost:~$ ls -l
total 868976
-rwxr-xr-x 1 ubuntu ubuntu 889826641 Jul 14 13:49 alfresco-community-installer-201701-linux-x64.bin
```

### Install alfresco
執行安裝，安裝完畢將會自動啟用。
*sudo 可行選用，差別在於能指定的安裝目錄。*
```bash
ubuntu@localhost:~$ sudo ./alfresco-community-installer-201701-linux-x64.bin
```

## Default port
`Port` 設定在安裝時會詢問是否要更改，下面所列出的是尚未更改。
- Database Server Port：5432
- Tomcat Server Port：8080
- Tomcat Shutdown Port：8005
- Tomcat SSL Port：8443
- Tomcat AJP Port：8009
- LibreOffice Server Port：8100
- FTP Port：21

## Command
列出一些比較常用的指令。  
```bash
ubuntu@localhost:~$ sudo service alfresco
usage: /opt/alfresco/alfresco.sh help
       /opt/alfresco/alfresco.sh (start|stop|restart|status)
       /opt/alfresco/alfresco.sh (start|stop|restart|status) postgresql
       /opt/alfresco/alfresco.sh (start|stop|restart|status) tomcat

help       - this screen
start      - start the service(s)
stop       - stop  the service(s)
restart    - restart or start the service(s)
status     - show the status of the service(s)
```

## Config
部分設定檔所在位置。
```bashbash
ubuntu@localhost:~$ cat /opt/alfresco/tomcat/conf/
Catalina/            catalina.properties  logging.properties   tomcat-users.xml     
catalina.policy      context.xml          server.xml           web.xml    

ubuntu@localhost:~$ cat /opt/alfresco/tomcat/shared/classes/alfresco-global.properties
```

## Default web root
如果使用一般使用者安裝的話，預設安裝至使用者的 `home` 目錄底下。使用特權的話，則可以選擇安裝在 `/opt`。
```bash
ubuntu@localhost:~$ cat /opt/alfresco/tomcat/webapps/
alfresco/     awe/          host-manager/ ROOT/         share/        solr/         solr4.war     _vti_bin.war  wcmqs.war     
alfresco.war  awe.war       manager/      ROOT.war      share.war     solr4/        _vti_bin/     wcmqs/        
```

## Browser URL
```
http://nagato.yukifans.com:7780
```

