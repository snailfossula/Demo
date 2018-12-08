# Jenkins 

Jenkins 是一款由 Java 編寫的開源的持續整合工具。

# Environment
- ubuntu 14.04
    - 4G MEM
    - 20G DISK
- Docker

# How to installation on docker 

```bash
$ sudo docker run -d --name Jenkins \
    -p 8080:8080 -p 50000:50000 \
    -v jenkins_home:/var/jenkins_home \
    jenkins/jenkins:lts
```

# Table of contents
- [pipeline](./pipeline.md)