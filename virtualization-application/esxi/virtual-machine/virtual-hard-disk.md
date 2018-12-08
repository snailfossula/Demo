# Virtual hard disk
會因為儲存格式的不同，影響到虛擬硬碟在使用上的「速度」和「效能」。

## Introduction
### Thick Provision Lazy Zeroed
在建立虛擬硬碟時會一次給足全部的硬碟大小，並產生一個vmdk檔，而vmdk的大小等於創建的虛擬硬碟大小。

然後這虛擬硬碟在一開始會先將所需要使用到的部份先行初始化(zero)。至於其他空閒沒使用到的部份，則是等到有需要使用的時後再初始化(zero)，不過由於一開始就扣除 10G 所以在沒使用到的部份也就算在整體硬碟大小裡。因此整顆虛擬硬碟是固定大小，不會變動的。

若之後有資料要做寫入的動作時，凡是第一次寫入到空閒空間時都必須要初始化。此類型的硬碟空間在第一次的硬碟寫入時會有輕微的 I/O 性能的損失。

### Thick Provision Eager Zeroed
在建立虛擬硬碟時會一次給足全部的硬碟大小，並且會刪除所有的資料，產生一個vmdk檔，而vmdk的大小等於創建的虛擬硬碟大小。

在創建硬碟時，會將所有的數據都初始化(zero)，所以會花費較多的時間。也正因為都已經初始化完成了，所以當要使用的時後就不需要再初始化。

而不論是第一次寫入數據或是整體的性能和效能上都會比較好一些。

### 2gbsparse
2GBsparse 為特殊格式，可將硬碟分割成多個最大為 2G 的硬碟。

VMware Workstation 和 VMware Server 可以使用這種格式，不過除非第一次輸入 thick 或 thin disk 格式，否則不能在 ESX 或 ESXi 主機上用此種格式啟動虛擬機。

這種格式有時用於將較大的 VMware ESXi 虛擬硬碟分解成較小的虛擬硬碟，方便於復製到其他 ESXi 主機或其他地方，因此這樣的虛擬硬碟能搬移到任何地方。

### Thin Provision
隨著使用量而增加硬碟大小，達到上限時就不會在增加了。

而產生的 vmdk 會隨著使用的情況增加並一邊初始化(zero)。所以 vmdk 的大小不等於創建虛擬硬碟的大小，只會等於實際使用的大小。

缺點: Thin disk隨著使用的增加，在效能上會比較差一點點。

## Reference
- [ithome article](https://ithelp.ithome.com.tw/articles/10104020)
- [weithenn article](http://www.weithenn.org/2014/05/thick-lazy-eager-thin-performance.html)

