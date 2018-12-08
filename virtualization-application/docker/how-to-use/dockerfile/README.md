# Dockerfile
Docker 在製作鏡像時，必須透過讀取 Dockerfile 內的指令，來自動製作用戶所需的鏡像。

## Table of contents
- [Dockerfile Sample](#dockerfile-sample)
- [Base command-line](#base-command-line)
- [Instruction](#instruction)
    - [FROM](#from)
    - [RUN](#run)
    - [WORKDIR](#workdir)
    - [ADD & COPY](#add--copy)
    - [VOLUME](#volume)
    - [HEALTHCHECK](#healthcheck)
    - [EXPOSE](#expose)
    - [CMD](#cmd)
    - [ENTRYPOINT](#entrypoint)

## Dockerfile Sample
底下為 *Docker* 官方所提供 *haproxy* 的 [dockerfile](https://github.com/docker-library/haproxy/blob/6c6d92913f56e05d6985d2f0f2131675de68f915/1.5/Dockerfile)。（因為內容太長，以 #... 為以下省略）
```dockerfile
FROM debian:jessie

RUN apt-get update \
	&& apt-get install -y --no-install-recommends \
		libpcre3 \
		libssl1.0.0 \
	&& rm -rf /var/lib/apt/lists/*

ENV HAPROXY_MAJOR 1.5
ENV HAPROXY_VERSION 1.5.19
ENV HAPROXY_MD5 74d49316f00e1fd9859bcac84ab04b5c

# see https://sources.debian.net/src/haproxy/jessie/debian/rules/ for some helpful navigation of the possible "make" arguments
RUN set -x \
	\
	&& buildDeps=' \
		ca-certificates \
		gcc \
		libc6-dev \
		libpcre3-dev \
		libssl-dev \
		make \
		wget \
		zlib1g-dev \
	' \ 
    #... omission

COPY docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["haproxy", "-f", "/usr/local/etc/haproxy/haproxy.cfg"]
```

## Base command-line

鏡像製作的指令：
```bash
$ sudo docker build -t [<repository-name>]/image-name[:<tag>] workdir [-f dockerfile-name]
```

範例：（請下載該[範例](./sample/build-image)操作）
```bash
~/build-image$ sudo docker build -t nginx:1.0.1 . 
# Or lab.yukifans.com/library/nginx:1.0.1 -f Dockerfile
```

容器運行的指令：
```bash
$ sudo docker run [OPTIONS] IMAGE[:TAG|@DIGEST] [COMMAND] [ARG ...]
```
範例：
```
$ sudo docker run --name nginx-test -p 80:80 nginx:1.0.1
```

## Instruction

### FROM
基於某個鏡像製作鏡像。
```dockerfile
FROM <image>[:<tag>]|[@<digest>] [AS <name>]
```

以 `alpine` 鏡像為基底製作鏡像檔。
```dockerfile
FROM alpine
```

### RUN
定義建構鏡像之容器內欲運行的指令。
```dockerfile
RUN <command>|["executable", "param1",...]
```

在製作鏡像的容器內安裝 *nginx*。
```dockerfile
RUN apt-get update \
    && apt-get install -y nginx 
```
*注意：Docker 鏡像製作，每一個指令及是一層，故需將相關指令合併為一個。（可參照[範例](./sample/RUN/)比較其差異。）*

### WORKDIR
設定建構鏡像之容器的工作目錄。
```dockerfile
WORKDIR /path/to/workdir
```

```dockerfile
WORKDIR /etc/nginx
```
*注意：每行指令都是各別的容器來製作鏡像，所以當前指令運行 `RUN cd /path/to/any`，並不會影響後續指令運行的工作目錄，除非 `WORKDIR`更動。（可參照[範例](./sample/WORKDIR/)比較其差異。）*

### ADD & COPY
新增 & 複製檔案至鏡像中。
```dockerfile
ADD [--chown=<user>:<group>] <src>... <dest>|["<src>",... "<dest>"]
COPY [--chown=<user>:<group>] <src>... <dest>|["<src>",... "<dest>"]
```
*建議一律使用 `COPY` - [參考](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#add-or-copy)*

> Although `ADD` and `COPY` are functionally similar, generally speaking, `COPY` is preferred. That’s because it’s more transparent than `ADD`.

### VOLUME
在容器運作前，會自動掛載指定的檔案或目錄到匿名卷。
```dockerfile
VOLUME ["/data"]
```

容器啟用後，會將 *nginx* 的 `log` 掛載至匿名卷。（[範例](./sample/VOLUME/)）
```dockerfile
VOLUME /var/log/nginx
```

### HEALTHCHECK
當容器運行時，*Docker* 將會透過該設定檢測容器是否仍在工作。
```dockerfile
HEALTHCHECK [OPTIONS] CMD command
```
可使用的 OPTIONS:
- `--interval=DURATION` (default: `30s`)
- `--timeout=DURATION` (default: `30s`)
- `--start-period=DURATION` (default: `0s`)
- `--retries=N` (default: `3`)

該範例為運行 *nginx* 的容器，Docker 會透過該指令檢測容器是否正常運作。（[範例](./sample/HEALTHCHECK/)）
```dockerfile
HEALTHCHECK --interval=10s --timeout=10s --retries=2 \
        CMD curl localhost || exit 1
```

### EXPOSE
標示容器使用的埠號。
```dockerfile
EXPOSE <port> [<port>/<protocol> ...]
```
*注意：即使標示使用的埠號，但容器運行仍需要指定埠號 `-p`，或是 `-P` 讓系統按照標示隨機分配。（可參照[範例](./sample/EXPOSE/)比較其差異。）*

### CMD
容器運行時的任務指令。
```dockerfile
CMD ["executable","param1", ...]|["param1", ...]|<command param1 ...>
```

該容器將會持續運行 nginx。
```dockerfile
CMD ["nginx", "-g", "deamon off;"]
```
*注意：當填寫 `CMD service nginx start` 時，容器的任務僅啟用 nginx ，而非持續運行。（可參照[範例](./sample/CMD/)比較其差異。）*

### ENTRYPOINT
介接 CMD 的指令與參數
```dockerfile
ENTRYPOINT ["executable", "param1", ...]|<command param1 ...> 
```

容器運行 `curl` 指令抓取網頁資料；該網頁會顯示用戶端 IP。（[範例](./sample/ENTRYPOINT/)）
```dockerfile
ENTRYPOINT ["/usr/bin/curl", "icanhazip.com"] $CMD
```
運行指令：
```bash
$ sudo docker run curl -i
```
