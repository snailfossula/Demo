# Package

## Setting 
設定 *ESXI* 接受度等級（`CommunitySupported`, `PartnerSupported`...etc.）。

```bash
[root@localhost:~] esxcli software acceptance set--level=CommunitySupported
Host acceptance level changed to 'CommunitySupported'.
```
[Official document](https://pubs.vmware.com/vsphere-50/index.jsp?topic=%2Fcom.vmware.vsphere.upgrade.doc_50%2FGUID-27BBBAB8-01EA-4238-8140-1C3C3EFC0AA6.html)

## Install
```bash
[root@localhost:~] esxcli software vib install -v /storage/path/<VIB name> -f
```

## Check
```bash
[root@localhost:~] esxcli software vib list | grep <VIB name>
```

## Remove
移除 `PartnerSupported` 或 `CommunitySupported` 方式。

```bash
[root@localhost:~] esxcli software vib remove -n <VIB name>
```
