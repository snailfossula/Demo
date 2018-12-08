# Snapshot
VMware vExpert 專家的 William Lam，撰寫出可以同時運作於 ESX 及 ESXi 虛擬化平台的備份腳本「ghettoVCB.sh」，此備份本為採用類似早期 VMware 的備份機制
VCB 所達成（目前 VMware 主流備份機制為透過 vStorage APIs for Data Protection），因此您也可以將此備份腳本運用於免費版本的 VMware vSphere Hypervisor 虛擬化平台上。

## 環境
### 實作環境所需平台及軟體
- 虛擬化技術平台： VMware vSphere ESXi 6.5
- 連線管理軟體： VMware vSphere Client
- 自動化備份腳本： ghettoVCB.sh
- SSH Client： putty.exe
 
<!--
待修改 
-->
<img src="./img/vm-backup-01.png" alt="" height="75%" width="75%">

## 快照機制（Snapshot）
快照機制能夠在特定的時間點保留 VM 虛擬主機的**狀態（State）**及**資料（Data）**。
以 VM 虛擬主機的電源狀態來說的話便是包括了**開機（Powered-On）**、**關機（Powered-Off）**、**暫停（Suspended）** 等狀態，若是以資料來看的話則包含了
**虛擬硬碟（Virtual Disk）**、**虛擬記憶體（Virtual Memory）**、**虛擬網路卡（Virtual Network Adapter）**、其它虛擬裝置（Virtual Devices）等。

<img src="./img/vm-backup-02.png" alt="" height="75%" width="75%">

當 VM 虛擬主機建立快照時還有幾個要考量的部份，首先對於 VM 虛擬主機當中的虛擬記憶體狀態（Virtual Memory State）是否也需要快照，
若是備份的 VM 虛擬主機其運作的服務為資料庫類型時，那麼就強烈建議必須要一起快照虛擬記憶體狀態，否則當執行快照回復作業時便會發生 VM 虛擬主機無法順利運作
的問題，不過理所當然的是若需要備份虛擬記憶體狀態則所花費的快照時間也較長。

此外就是「暫停（Quiesce）」機制，ESX（i）虛擬化平台透過 VMware Tools 工具，來暫時停止 VM 虛擬主機中的檔案系統，使 Guest OS 可以將相關的
**緩衝資料（Buffers）**以及**虛擬記憶體中的快取資料（Cache）**，順利的寫回 Guest OS 的虛擬磁碟當中以保持資料的一致性以及可用性。

<img src="./img/vm-backup-03.png" alt="" height="75%" width="75%">
 
快照機制啟用時會參考原本的虛擬硬碟檔案 **-flat.vmdk** 內容，接著建立出與原來虛擬硬碟檔案差異內容的 **.vmdk** 及 **-delta.vmdk** 檔案，
此機制所產生的虛擬硬碟檔案稱為**子磁碟（Child Disks）**，但是若再次執行快照時原本的子磁碟將會變成另一個子磁碟的參考父磁碟（Parent Disks）。

此外還會建立 **.vmsd** 檔案，此檔案內容為記錄 VM 虛擬主機的快照資訊以及主要來源資訊，也就是記錄了每個子磁碟以及快照與快照之間的關聯性。
最後則是 **.vmsn** 檔案，此檔案內容為這些快照檔案中所包含的虛擬記憶體狀態時間。

<img src="./img/vm-backup-04.png" alt="" height="75%" width="75%">

## Reference
[輕鬆線上備份運作中的 VM 虛擬主機](http://www.weithenn.org/2013/02/netadmin-85.html)

[設定排程自動備份 無痛快速還原VM虛擬主機](http://www.weithenn.org/2013/02/netadmin-86.html)
