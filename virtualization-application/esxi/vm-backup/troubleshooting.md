# Troubleshooting

## Issue
<!--
待修改
-->
使用 `ghettoVCB` 遇到的問題與解決方式。
## Wrong solution
1. 錯誤訊息 (Snapshot found for「host」, backup will not take place)

> 會出現此錯誤訊息，主要是因為當正在使用的虛擬機 VM 要備份的時後，會先做出一個 `snapshot` 檔。  
然後在備份的中途若有不當的中斷或停止時。此 `snapshot` 檔就會無法自動刪除。  
進而造成下次要備份時出現此錯誤訊息 ***Snapshot found for「host」, backup will not take place*** 。  
因此錯出現此錯誤訊息時需要手動自行刪除。
