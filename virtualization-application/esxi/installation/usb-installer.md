# USB installer
可以格式化 USB 進行 ESXi 安裝或升級

## Environment
- OS：Ubuntu 14.04  

## Prepare
- A 8GB USB flash drive
- ESXi ISO image, VMware-VMvisor-Installer-6.5.0.update01-5969303.x86_64（包含 isolinux.cfg）- [Official download](https://my.vmware.com/web/vmware/details?downloadGroup=ESXI65U1&productId=614&rPId=17343)
- Syslinux package

## Format USB

### Check connection

- 查看 `syslog`  
```bash
itachi@ubuntu:~$ cat /var/log/syslog | grep sdb
Sep 28 10:18:33 ubuntu kernel: [  710.735879] sd 33:0:0:0: [sd61702144 512-byte logical blocks: (31.5 GB/29.4 GiB)
Sep 28 10:18:33 ubuntu kernel: [  710.736690] sd 33:0:0:0: [sdWrite Protect is off
Sep 28 10:18:33 ubuntu kernel: [  710.736694] sd 33:0:0:0: [sdMode Sense: 43 00 00 00
Sep 28 10:18:33 ubuntu kernel: [  710.737301] sd 33:0:0:0: [sdWrite cache: enabled, read cache: enabled, doesn't support DPO orFUA
Sep 28 10:18:33 ubuntu kernel: [  710.743892]  sdb: sdb1
...
``` 
    
- `lsusb` 查看 USB 裝置  
 ```bash
itachi@ubuntu:~$ sudo lsusb
Bus 002 Device 001: ID 1d6b:0003 Linux Foundation 3.0 root hub
Bus 001 Device 005: ID 0e0f:0002 VMware, Inc. Virtual USB Hub 
Bus 001 Device 004: ID 0e0f:0002 VMware, Inc. Virtual USB Hub
Bus 001 Device 003: ID 8564:1000
Bus 001 Device 002: ID 0e0f:0003 VMware, Inc. Virtual Mouse
Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 003 Device 001: ID 1d6b:0002 Linux Foundation 2.0 root hub
Bus 004 Device 001: ID 1d6b:0001 Linux Foundation 1.1 root hub
 ```

*本篇 USB 標示為 sdb ，請依據當前情況判定。*

### Partition

```bash
itachi@ubuntu:~$ sudo fdisk /dev/sdb
 
  a. Enter d，d   delete a partition.  
  b. Enter n，n   add a new partition. 
  
Command (m for help): n
Partition type:
  p   primary (0 primary, 0 extended, 4 free)
  e   extended
Select (default p): p
Partition number (1-4, default 1): 1
First sector (2048-61702143, default 2048):
Using default value 2048
Last sector, +sectors or +size{K,M,G} (2048-61702143, defaul61702143): +5G
  c. Enter t ，t   change a system id of partition. setting for the FAT32 file system, such as c.
  
Command (m for help): t
Selected partition 1
Hex code (type L to list codes): L

0  Empty           24  NEC DOS         81  Minix / old Lin bf  Solaris
//...
a  OS/2 Boot Manag 50  OnTrack DM      94  Amoeba BBT      e3  DOS R/O
b  W95 FAT32       51  OnTrack DM6 Aux 9f  BSD/OS          e4  SpeedStor
//...
Hex code (type L to list codes): c
Changed system type of partition 1 to c (W95 FAT32 (LBA))
  
d. Enter a，a   toggle a bootable flag.Setting ***partition 1*** .  
e. Enter p，p   print the partition table.

Disk /dev/sdb: 31.6 GB, 31591497728 bytes
255 heads, 63 sectors/track, 3840 cylinders, total 61702144 sectors
Units = sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disk identifier: 0xa110cac8

Device    Boot      Start         End      Blocks   Id  System
/dev/sdb1   *        2048    10487807     5242880    c  W95 FAT32 (LBA)
 
  f. Enter w，w   write table to disk and exit.to write the partition table and exit the program.
```

### Format fat32 
```bash
itachi@ubuntu:~$ sudo mkfs.vfat -F 32 -n USB /dev/sdb1
[sudo] password for itachi:
mkfs.fat 3.0.26 (2014-03-07)
# -F fat-size （12、16、32）
# -n volume-name
```
[參考資料](http://wiki.linux.org.hk/w/Format_disk_as_FAT)  

## Make usb

```bash
itachi@ubuntu:~$ sudo /usr/bin/syslinux /dev/sdb1
itachi@ubuntu:~$ su -
Password:
root@ubuntu:~# cat /usr/lib/syslinux/mbr.bin > /dev/sdb
```

### Mount USB 

```bash
itachi@ubuntu:~$ sudo mkdir /usbdisk
itachi@ubuntu:~$ sudo mount /dev/sdb1 /usbdisk/
```

### Mount ESXi ISO 

```bash
itachi@ubuntu:~$ sudo mkdir /esxi_cdrom
itachi@ubuntu:~$ sudo mount -o loop VMware-VMvisor-Installer-6.5.0.update01-5969303.x86_64.iso /esxi_cdrom/
mount: block device /home/itachi/VMware-VMvisor-Installer-6.5.0.update01-5969303.x86_64.iso is write-protected, mounting read-only
```

### Copy image file to USB

```bash
itachi@ubuntu:~$ sudo cp -r /esxi_cdrom/* /usbdisk/
```

### Rename isolinux.cfg

```bash
itachi@ubuntu:~$ sudo mv /usbdisk/isolinux.cfg /usbdisk/syslinux.cfg
```

### Modify syslinux.cfg 
```bash
itachi@ubuntu:~$ sudo vim /usbdisk/syslinux.cfg
APPEND -c boot.cfg -p 1 
# APPEND -c boot.cfg
```

## Umount
Unmount USB & ISO image
```bash
itachi@ubuntu:~$ sudo umount /usbdisk /esxi_cdrom
```
