# configure
`ghettoVCB` 可參考配置 `ghettoVCB.conf`。

## Check configure
```bash
[CCH@localhost:/etc/ghettovcb] cat ghettoVCB.conf
VM_BACKUP_VOLUME=/vmfs/volumes/dlgCore-NFS-bigboi.VM-Backups/WILLIAM_BACKUPS
DISK_BACKUP_FORMAT=thin
VM_BACKUP_ROTATION_COUNT=3
POWER_VM_DOWN_BEFORE_BACKUP=0
ENABLE_HARD_POWER_OFF=0
ITER_TO_WAIT_SHUTDOWN=3
POWER_DOWN_TIMEOUT=5
ENABLE_COMPRESSION=0
VM_SNAPSHOT_MEMORY=0
VM_SNAPSHOT_QUIESCE=0
ALLOW_VMS_WITH_SNAPSHOTS_TO_BE_BACKEDUP=0
ENABLE_NON_PERSISTENT_NFS=0
UNMOUNT_NFS=0
NFS_SERVER=172.30.0.195
NFS_VERSION=nfs
NFS_MOUNT=/nfsshare
NFS_LOCAL_NAME=nfs_storage_backup
NFS_VM_BACKUP_DIR=mybackups
SNAPSHOT_TIMEOUT=15
EMAIL_ALERT=0
EMAIL_LOG=0
EMAIL_SERVER=auroa.primp-industries.com
EMAIL_SERVER_PORT=25
EMAIL_DELAY_INTERVAL=1
EMAIL_TO=auroa@primp-industries.com
EMAIL_ERRORS_TO=
EMAIL_FROM=root@ghettoVCB
WORKDIR_DEBUG=0
VM_SHUTDOWN_ORDER=
VM_STARTUP_ORDER=
```

## Configure  meaning

VM_BACKUP_VOLUME：備份目的地（若資料夾不存在則會自行建立）。   
DISK_BACKUP_FORMAT：虛擬主機備份硬碟格式。  
VM_BACKUP_ROTATION_COUNT：保留虛擬主機的備份數量。  
POWER_VM_DOWN_BEFORE_BACKUP：虛擬主機在備份作業執行前是否要關機。（請注意！關機中的虛擬主機將不會建立快照）  
ENABLE_HARD_POWER_OFF：是否啟用，因為關機後的虛擬主機 ESX/ESXi 平台無法透過 VMware Tools 偵測到虛擬主機。  
ITER_TO_WAIT_SHUTDOWN：是否在備份作業執行前 60 秒將虛擬主機關機。  
POWER_DOWN_TIMEOUT：當虛擬主機經過 60 秒後還沒關機完成，則要等待多久便放棄備份該虛擬主機的備份作業。  
ENABLE_COMPRESSION：是否啟用檔案壓縮功能（請於備份後測試能否順利還原）。  
VM_SNAPSHOT_MEMORY：是否快照虛擬主機記憶體狀態（預設為禁用）。  
VM_SNAPSHOT_QUIESCE：是否啟用虛擬主機檔案系統暫停機制（預設為禁用）。   
ENABLE_NON_PERSISTENT_NFS：是否啟用自動掛載及卸載 NFS 儲存設備機制。  
UNMOUNT_NFS：是否卸載 NFS 儲存設備。  
NFS_SERVER：NFS 儲存設備 IP 位址或主機名稱。  
NFS_MOUNT：NFS 儲存設備分享路徑。  
NFS_LOCAL_NAME：NFS儲存設備於 ESX/ESXi 平台上 Datastore 掛載名稱。  
NFS_VM_BACKUP_DIR：存放虛擬主機備份的資料夾名稱。  
SNAPSHOT_TIMEOUT：當虛擬主機經過60秒後還沒建立快照，則放棄備份該虛擬主機。  
EMAIL_LOG：是否開啟 E-mail 寄送備份日誌功能（預設為禁用）。  
EMAIL_DEBUG：是否開啟 E-mail 寄送除錯功能（尋找無法寄件的原因）。  
EMAIL_SERVER：指定 E-mail 郵件伺服器IP位址或主機名稱。  
EMAIL_SERVER_PORT：指定 E-mail 郵件伺服器通訊埠號（Port）。  
EMAIL_DELAY_INTERVAL：指定 E-mail 郵件的延遲間隔時間（預設為1秒）。  
EMAIL_TO：指定郵件收件人，如果有多筆記錄則使用逗點隔開。  
EMAIL_FROM：指定郵件寄件人。  
RSYNC_LINK：採用 Rsync 同步機制時是否支援 Symbolic Link 。  
