# ELK 介紹

## log 

Log 就是系統或設備在連線和運作時所產生的記錄，藉由 Log 的蒐集和分析，讓 IT 人員能夠監控系統的運作狀態，判斷可能發生的事件，以及分析資料存取行為和使用者的活動。

- 網路是否遭到惡意的嘗試入侵？
- 系統和網路運作是否有異常情況發生？

IT 人員只要對 Log 進行監控，就可以判斷可能的問題，或著狀況不清楚時，透過 Log 的查詢分析，可在較短時間內找出原因。

## elasticsearch
![](https://i.imgur.com/VBpAfdC.png)

1. Elasticsearch 是一個分散式 RESTful 搜索和分析引擎。
2. Java 構建，被歸類為 [NoSQL](https://www.ithome.com.tw/news/92507) 數據庫（非結構化的方式存儲數據）。
3. 從 Elasticsearch 獲取數據的方法是使用 `REST API` 搜索數據
4. Elasticsearch　與　ELK Stack，Logstash　和　Kibana　中的其他組件一起使用，扮演`數據索引`和`儲存`的角色。

反正就是 "儲存 log"、"索引 log"

### 文檔
對像被序列化成 JSON 並存儲到 Elasticsearch 中，指定唯一 ID。
```json=
{
    "email":      "john@smith.com",
    "first_name": "John",
    "last_name":  "Smith",
    "info": {
        "bio":         "Eco-warrior and defender of the weak",
        "age":         25,
        "interests": [ "dolphins", "whales" ]
    },
    "join_date": "2014/05/01"
}
```
### 索引
在 Elasticsearch 中存儲數據的行為就叫做索引(indexing)
在 Elasticsearch 中，文檔屬於一種類型(type),而這些類型存在於索引(index)中
```
Relational DB -> Databases -> Tables -> Rows -> Columns
Elasticsearch -> Indices(名詞)   -> Types  -> Documents -> Fields
```
### 搜索
在 kibana 搜尋部分會講
```shell=
GET filebeat*/_search
```
### 聚合
Elasticsearch 有個功能叫聚合(aggregations)，它允許你在數據上生成複雜的可視圖表。它很像 SQL 的 GROUP BY 。
### 參考資料
[Elasticsearch 权威指南](https://es.xiaoleilu.com)
[Elasticsearch 官網](https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html)

## logstash
![](https://i.imgur.com/7KkPmlF.png)

Server 端數據處裡的地方，能夠同時從多個來源（Client端）中取得數據並轉換，發送到 Elasticsearch
### 三個部分
- input
    - file
    - TCP
    - UDP
    - syslog
    - beat
    - 等等
- filter
    - grok
    - mutate
    - drop
    - 等等
- output
    - Elasticsearch
    - google_bigquery
    - 等等

![](https://i.imgur.com/rl6C2hf.png)
### 參考資料
[pipline](https://www.elastic.co/guide/en/logstash/current/pipeline.html)
[官網](https://www.elastic.co/guide/en/logstash/current/introduction.html)
[LogstashBook](https://logstashbook.com/TheLogstashBook_sample.pdf)
[logstash-filter](https://logz.io/blog/5-logstash-filter-plugins/)
[pattern](https://github.com/logstash-plugins/logstash-patterns-core/tree/master/patterns)
[grok](https://grokdebug.herokuapp.com)

## kibana
![](https://i.imgur.com/Vml1uLT.png)

進行資料視覺化的呈現，可透過 Nginx 進行代理

![](https://i.imgur.com/QHipVMj.png)

**GUI 框線介紹**

- 黑框
    - Discover：用以檢視各索引下的記錄內容及總記錄筆數
    - Visualize：將搜尋出的數據以長條圖、圓餅圖、表格等方式呈現
    - Dashboard：將以儲存的搜尋結果或已完成的圖表組合成一份快速報表
    - Timelion：時序性的監看 query
    - Dev Tools：提供一個在 kibana 直接呼叫 elasticsearch 的方式
    - Managment：設定 kibana 對應的 elasticsearch index patterns，管理已經儲存好的搜尋結果物件、視覺化結果物件，及進階資料過濾設定
- 紅框
    - Index：要搜索的 index pattern（Management 所設定的 index patterns create ）
- 咖啡框
    - Avaliable Fields：搜索 index 下所包含的屬性(也就是我們在 logstash 切出來的部分)
- 紫框
    - Timestamp：資料時間，可用於特定時間區間資料量觀察
- 藍框
    - source：顯示我們接收到的 log 資訊
- 綠框
    - 搜尋條件：預設 "*" 搜尋 index 下所有紀錄

## filebeat
![](https://i.imgur.com/QZB0q6I.png)
安裝在要發送日誌到 Logstash 的 Client 上，Filebeat 為日誌傳送代理，利用 `lumberjack networking protocol` 與 Logstash 進行溝通。

[lumberjack networking protocol](https://github.com/elastic/logstash-forwarder/blob/master/PROTOCOL.md)
## 什麼是 beat
Beats 是輕量級（資源高效，無依賴性，小型）和開源蒐集服務 log 的集合，它們充當安裝在基礎架構中不同服務器上的代理，用於收集 logs 或 metrics。

## 小節
ELK Stack 主要四個要件

- Logstash：Server 端數據處裡的地方，能夠同時從多個來源（Client端）中取得數據並轉換，發送到 Elasticsearch
- Elasticsearch：存儲 Client 發送的日誌
- Kibana：進行資料視覺化的呈現，可透過 Nginx 進行代理
- Filebeat：安裝在要發送日誌到 Logstash 的 Client 上，Filebeat 充當日誌傳送代理，利用 lumberjack networking protocol 與 Logstash 進行溝通

## 實作

### Install Docker Env
- Docker
```bash
$ sudo curl -sSL https://get.docker.com/ | sh
```
- Docker-compose
```bash
$ sudo curl -L https://github.com/docker/compose/releases/download/1.21.0/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
$ sudo chmod +x /usr/local/bin/docker-compose
```

## Clone repository

```bash
$ git clone https://github.com/deviantony/docker-elk.git
```

### Run docker-elk

```bash
$ cd /docker-elk
$ sudo docker-compose up # 加上 -d 則為背景執行
```
**ELK Port**
- 5000: Logstash TCP input
- 9200: Elasticsearch HTTP
- 9300: Elasticsearch TCP transport
- 5601: Kibana

## Check service operation

**Elasticsearch**
```bash
$ curl http://localhost:9200
{
  "name" : "YUiaJ73",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "8TZBIrUbTL2qEl931RwYBw",
  "version" : {
    "number" : "6.2.3",
    "build_hash" : "c59ff00",
    "build_date" : "2018-03-13T10:06:29.741383Z",
    "build_snapshot" : false,
    "lucene_version" : "7.2.1",
    "minimum_wire_compatibility_version" : "5.6.0",
    "minimum_index_compatibility_version" : "5.0.0"
  },
  "tagline" : "You Know, for Search"
}
```
**Kibana**
上述安裝完用以下瀏覽 kibana
<pre>
http://localhost:5601
</pre>

**預設配置**
```bash
@kibana:~/docker-elk/logstash/pipeline$ cat logstash.conf
input {
        tcp {
                port => 5000
        }
}

## Add your filters / logstash plugins configuration here

output {
        elasticsearch {
                hosts => "elasticsearch:9200"
        }
}
```

**nc test**
從上述配置可以知道，送到 port 5000 的內容會直接地被送至 elasticsearch 儲存

```bash
$ echo "I love itachi" > text.txt &&  nc localhost 5000 < text.txt
```
到 kibana Web GUI 的 `management` 新增名為 `logstash*` 的 index，接著點選 `Discover` 即可看到剛傳輸的資訊
["connect-to-elasticsearch"](https://www.elastic.co/guide/en/kibana/current/connect-to-elasticsearch.html)
!["result"](https://github.com/CCH0124/Business/blob/master/ubuntu/ELK%20Stack/Docker/ELK%20Image/kibana%20nc%20test.png)

### input

官方[input-plugins](https://www.elastic.co/guide/en/logstash/current/input-plugins.html "input-plugins")

**TCP**

TCP 這個 input 可以接收來自於 TCP socket 的資訊

**以 TCP 新增兩筆數據**

port 利用設定的特性的分別
type 有意義的賦予別名，方便 filter 進行資料的切割與過濾

```bash
~/docker-elk/logstash/pipeline$ vi logstash.conf
        tcp {
                port => 2005
                type => nginx
        }

        tcp {
                port => 9487
                type => dev_log
        }
```

>上述新增 TCP 得條件要重新執行 docker-compose up ，要讓 port 可以轉發

**file**

持續觀察讀取指定檔，有異動則觸發 logstash 進行基料擷取

**新增數據**


```bash
      file {
                path => ["/var/log/syslog*"]
                type => "system"
                start_position => "beginning" # 初次啟用想要從頭取得全部 log 內容
      }
```

### filter

將日誌輸出成我們想要的格式。Logstash 存在豐富的過濾插件：Grok 正則表示、時間處理、JSON 編解碼、數據修改 Mutate 

### output

目標可以是 Stdout、Elasticsearch 、Redis 、TCP 、File 等

**Rsyslog**

[logstash 範例](https://github.com/CCH0124/Business/tree/master/ubuntu/ELK%20Stack/Docker/logstash)

## filebeat
Filebeat 是屬於 Beats 系列的**日誌托運商**：一組輕量級托運人，用於將不同種類的數據傳輸到 ELK 進行分析。每個 beat 專門用於傳送不同類型的信息，如：Winlogbeat 提供 Windows 事件日誌，Metricbeat 提供主機指標等等。顧名思義，Filebeat會發送日誌文件。

> 如果輸出 Logstash 或 Elasticsearch 有讀取問題時（大量日誌），Filebeat 會減慢文件的讀取速度。

### Install Filebeat on Docker

```bash
$ sudo docker pull docker.elastic.co/beats/filebeat:6.2.4
```
[官網](https://www.elastic.co/guide/en/beats/filebeat/current/index.html)
[filebeat 範例](https://github.com/CCH0124/Business/tree/master/ubuntu/ELK%20Stack/Docker/Filebeat)
