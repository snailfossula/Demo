# NAS
## Mount

```bash
[root@localhost:/opt/ghettovcb/bin] esxcfg-nas -a -o 192.168.7.11 -s /volume1/ASUS-Server Belstar_KH
Connecting to NAS volume: Belstar_KH
Belstar_KH created and connected.

# -a|--add                Add a new NAS filesystem to /vmfs volumes.
#                         Requires --host and --share options.
#                         Use --readonly option only for readonly access.
# -o|--host <host>        Set the host name or ip address for a NAS mount.
#                         For version 4.1, can be a comma-separated list.
# -s|--share <share>      Set the name of the NAS share on the remote system.
# /volume1/ASUS-Server 為 NAS 上放置的位置
# Belstar_KH 為掛載名稱
```

## Umount
```bash
[root@localhost:/opt/ghettovcb/bin] esxcli storage nfs remove -v Belstar_KH
```

## Check
```bash
[root@localhost:/opt/ghettovcb/bin] esxcli storage nfs list
Volume Name  Host          Share                 Accessible  Mounted  Read-Only   isPE  Hardware Acceleration
-----------  ------------  --------------------  ----------  -------  ---------  -----  ---------------------
Belstar_KH   192.168.X.XX  /volume1/ASUS-Server        true     true      false  false  Not Supported

# list                  List the NAS volumes currently known to the ESX host.
```

## Troubleshooting
檢查 NAS 是否給予 ESXi 連線的權限
```bash
[root@localhost:/opt/ghettovcb/bin] esxcfg-nas -a -o 192.168.7.11 -s /volume1/ASUS-esxi-02 Belstar_Ae2
Connecting to NAS volume: Belstar_Ae2
Unable to connect to NAS volume Belstar_Ae2: Unable to complete Sysinfo operation.  Please see the VMkernel log file for more details.: Sysinfo error: The NFS server denied the mount requestSee VMkernel log for details.
```
