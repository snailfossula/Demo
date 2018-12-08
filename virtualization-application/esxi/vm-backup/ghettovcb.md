
# ghettoVCB
[Official download](https://github.com/lamw/ghettoVCB/blob/master/vghetto-ghettoVCB.vib "ghettoVCB")

## Parepaer
<!--
## Mount
在 `ESXI` 掛載 `NAS` 的 `NFS`  
`ESXI` 儲存區 > 新增資料存放區
-->


## Installation process

### Upload
將 `ghettoVCB` 上傳至 *ESXI* 伺服器，方式如下：
- 點選「儲存區」，右鍵點選欲存放檔案的「資料存放區」，然後點擊「上傳」。

  <img src="../img/vm-backup-05.png" alt="upload" height="75%" width="75%">

- 使用指令 `scp`，將檔案上傳至伺服器上。

### Installation

```bash
[root@localhost:~] esxcli software vib install -v /vmfs/volumes/datastore1/vghetto-ghettoVCB.vib -f
Installation Result
  Message: Operation finished successfully.
  Reboot Required: false
  VIBs Installed: virtuallyGhetto_bootbank_ghettoVCB_1.0.0-0.0.0
  VIBs Removed:
  VIBs Skipped:
#-v 須給完整路徑
```

### Check
安裝完後 `/opt/ghettovcb/bin/` 下，會有下面兩個檔案：

```bash
  [root@localhost:~] ls /opt/ghettovcb/bin/
  ghettoVCB-restore.sh  ghettoVCB.sh

  ghettoVCB-restore.sh #備份還原
  ghettoVCB.sh #備份
```

## Configuration 

在 `/opt/ghettovcb/bin/` 目錄下，新增 `ghettoVCB.conf`。

```bash
[root@localhost:~] vi /opt/ghettovcb/bin/ghettoVCB.conf
VM_BACKUP_VOLUME=/vmfs/volumes/Belstar_KH/ESXI_Backup #備份後的儲存位置
DISK_BACKUP_FORMAT=thin #定義備份的硬碟格式
VM_BACKUP_ROTATION_COUNT=2 #定義備份保留的數量
ENABLE_COMPRESSION=0 #備份是否啟用壓縮 (enable=1,disable=0)
ENABLE_NON_PERSISTENT_NFS=0 #是否啟動自動掛載或卸載NFS
UNMOUNT_NFS=0 #是否要卸載NFS(yes=1,no=0)
NFS_SERVER=192.168.7.11 # NFS 伺服器位置
NFS_MOUNT=/ESXI_Backup # NFS 存放檔案的路徑
NFS_VERSION=nfs # NFS 版本
NFS_LOCAL_NAME=/vmfs/volumes/Belstar_KH # NFS 資料存放地方的名稱
NFS_VM_BACKUP_DIR=/vmfs/volumes/Belstar_KH/ESXI_Backup #VM備份資料存放位置
```

## List vm
列出伺服器上登入啟用的虛擬機器

```bash
[root@localhost:~] vim-cmd vmsvc/getallvms
Vmid         Name                                  File                             Guest OS      Version   Annotation
11     itachi_lab          [datastore1] itachi_lab/itachi_lab.vmx                 ubuntu64Guest   vmx-13
12     itachi_backupTest   [datastore1] itachi_backupTest/itachi_backupTest.vmx   ubuntu64Guest   vmx-13
```

### 測試
**Dryrun 測試**

```bash
[root@localhost:~]./ghettoVCB.sh -g ./ghettoVCB.conf -f /vmfs/volumes/datastore1/vms_to_backup/backup.lis -d dryrun
# ...tail
2017-10-12 01:52:18 -- dryrun: ###############################################
2017-10-12 01:52:18 -- info: ###### Final status: OK, only a dryrun. ######
2017-10-12 01:52:18 -- info: ============================== ghettoVCB LOG END================================
  -g 讀取自己寫的配置檔
   -f 讀取清單（虛擬機器名稱），需自行建立。內容為 vim-cmd vmsvc/getallvms 中要備份的 Name
   -d Debug 測試，不會產生備份
   -e 排除的虛擬機器
   -a 備份所有 ESXI 虛擬機
```

**實際測試**
```bash
[root@localhost:/opt/ghettovcb/bin] ./ghettoVCB.sh -g ./ghettoVCB.conf -m itachi_backupTest
# ...tail
2017-10-12 02:15:23 -- info: Initiate backup for itachi_backupTest
Option --adaptertype is deprecated and hence will be ignored
Destination disk format: VMFS thin-provisioned
Cloning disk '/vmfs/volumes/datastore1/itachi_backupTest/itachi_backupTest-0.vmdk'...Clone: 99% done.
2017-10-12 02:20:44 -- info: Backup Duration: 5.35 Minutes
2017-10-12 02:20:44 -- info: Successfully completed backup for itachi_backupTest!
2017-10-12 02:20:46 -- info: ###### Final status: All VMs backed up OK! ######
2017-10-12 02:20:46 -- info: ============================== ghettoVCB LOG END ================================

```

備份完，在 `ESXI` 掛載的 `NAS` 上查看

## 備份還原

### 新增 list 還原檔
`/opt/ghettovcb/bin`(不限此目錄下)下新增檔案，名稱隨意（參考：/etc/ghettovcb/ghettoVCB-restore_vm_restore_configuration_template）。內容如下

```bash
[root@localhost:/opt/ghettovcb/bin] vi vms_restore
#可備份單一或多個
"/vmfs/volumes/Belstar_KH/ESXI_Backup/itachi_lab/itachi_lab-2017-10-11_02-59-43;/vmfs/volumes/datastore1;4"
"/vmfs/volumes/Belstar_KH/ESXI_Backup/itachi_backupTest/itachi_backupTest-2017-10-11_02-59-43;/vmfs/volumes/datastore1;4"                               
#"[VM備份檔所在地的完整路徑];[ 還原後的VM要存放的完整路徑];[ 還原硬碟的格式]"

#還原硬碟的格式 : 
#DISK_FORMATS
#1 = zeroedthick
#2 = 2gbsparse
#3 = thin
#4 = eagerzeroedthick
```
### 測試
**Dryrun 測試**
```bash
[root@localhost:/opt/ghettovcb/bin] ./ghettoVCB-restore.sh -c vms_restore -d 1
################ DEBUG MODE ##############
Virtual Machine: "itachi_lab"
VM_ORIG_VMX: "itachi_lab.vmx"
VM_ORG_FOLDER: "itachi_lab-2017-10-11_02-59-43"
VM_RESTORE_VMX: "itachi_lab.vmx"
VM_RESTORE_FOLDER: "itachi_lab"
VMDK_LIST_TO_MODIFY:
scsi0:0.fileName  = "itachi_lab-0.vmdk"
scsi0:0.fileName  = "itachi_lab-0.vmdk"
##########################################


################ DEBUG MODE ##############
Virtual Machine: "itachi_backupTest"
VM_ORIG_VMX: "itachi_backupTest.vmx"
VM_ORG_FOLDER: "itachi_backupTest-2017-10-11_02-59-43"
VM_RESTORE_VMX: "itachi_backupTest.vmx"
VM_RESTORE_FOLDER: "itachi_backupTest"
VMDK_LIST_TO_MODIFY:
scsi0:0.fileName = "itachi_backupTest_0.vmdk"
scsi0:0.fileName  = "itachi_backupTest-0.vmdk"
##########################################


Start time: Thu Oct 12 02:33:02 UTC 2017
End   time: Thu Oct 12 02:33:03 UTC 2017
Duration  : 1 Seconds

---------------------------------------------------------------------------------------------------------------

#-d 1 ：Dryrun 測試
#-d 2 ：Debgu 測試
#-c：依造 list 中的設定還原備份，新增的檔案的檔案內容就是 list
```
**實際測試**
```bash
[root@localhost:/opt/ghettovcb/bin] ./ghettoVCB-restore.sh -c vms_restore
#以下是截此一 VM 的還原
################## Restoring VM: itachi_backupTest  #####################
Start time: Thu Oct 12 02:53:03 UTC 2017
Restoring VM from: "/vmfs/volumes/Belstar_KH/ESXI_Backup/itachi_backupTest/itachi_backupTest-2017-10-11_02-59-43"
Restoring VM to Datastore: "/vmfs/volumes/datastore1" using Disk Format: "eagerzeroedthick"
Creating VM directory: "/vmfs/volumes/datastore1/itachi_backupTest" ...
Copying "itachi_backupTest.vmx" file ...
Restoring VM's VMDK(s) ...
Updating VMDK entry in "itachi_backupTest.vmx" file ...
Option --adaptertype is deprecated and hence will be ignored
Destination disk format: VMFS eagerzeroedthick
Cloning disk '/vmfs/volumes/Belstar_KH/ESXI_Backup/itachi_backupTest/itachi_backupTest-2017-10-11_02-59-43/itachi_backupTest_0.vmdk'...
Clone: 19% done.
...
Start time: Thu Oct 12 02:34:53 UTC 2017
End   time: Thu Oct 12 03:13:02 UTC 2017
Duration  : 38.15 Minutes


---------------------------------------------------------------------------------------------------------------
```
成功後，`ESXI` 上會有還原的虛擬機
