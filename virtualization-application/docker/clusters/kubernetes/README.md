# Kubernetes
Kubernetes (commonly stylized as K8s) is an open-source system for automating deployment, scaling and management of containerized applications.

## Environment
- Hostname：u16-kube-master
  - OS：Ubuntu 16.04
  - IP：10.211.55.20
  - RAM：2G
  - DISK：20G
- Hostname：u16-kube-node01
  - OS：Ubuntu 16.04
  - IP：10.211.55.21
  - RAM：2G
  - DISK：20G

- Master node port
  - Kubernetes API server：6443
  - etcd server client API：2379-2380
  -	Kubelet API：10250
  - kube-scheduler：10251
  -	kube-controller-manager：10252
  - Read-only Kubelet API (Heapster)：10255
  
- Worker node port
  - Kubelet API：10250
  - Read-only Kubelet API (Heapster)：10255
  - Default port range for NodePort Services：30000-32767

## Table of Contents
- [Installing Docker](#installing-docker)
- [Installing Kube](#installing-kube)
- [Initializing Master](#initializing-master)
- [Installing Pod Network](#installing-pod-network)
- [Joining Your Nodes](#joining-your-nodes)
- [Verify](#verify)

## Installing Docker
安裝 `Docker`。不過 `Kubernet` 最大支援 `Docker` 版本為 *17.03*，這部分可自行考量。(*兩台主機都需安裝*)

```bash
$ sudo apt-get update
$ sudo apt-get install -y docker.io
```

## Installing Kube
安裝 `kubeadm`、`kubectl` 和 `kubelet`。(*兩台主機都需安裝*)

```bash
$ sudo apt-get update && sudo apt-get install -y apt-transport-https
$ sudo curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
$ sudo touch /etc/apt/sources.list.d/kubernetes.list
$ sudo echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a  /etc/apt/sources.list.d/kubernetes.list
$ sudo apt-get update
$ sudo apt-get install -y kubelet kubeadm kubectl
```

## Initializing Master
初始化主節點。

```bash
u16-kube-master:~$ sudo swapoff -a
u16-kube-master:~$ sudo kubeadm init --pod-network-cidr=10.244.0.0/16
[kubeadm] WARNING: kubeadm is in beta, please do not use it for production clusters.
[init] Using Kubernetes version: v1.8.4

...

Your Kubernetes master has initialized successfully!

To start using your cluster, you need to run (as a regular user):

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  http://kubernetes.io/docs/admin/addons/

You can now join any number of machines by running the following on each node
as root:

  kubeadm join --token 2cc44c.62db142d950a9915 10.211.55.20:6443 --discovery-token-ca-cert-hash sha256:150a48ff057dc91a0314fc1454fda86a023f798cc2abd472ced88e9a3365818d

u16-kube-master:~$ mkdir -p $HOME/.kube
u16-kube-master:~$ sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
u16-kube-master:~$ sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

在初始化成功後，所輸出的 `kubeadm join`，是後續加入節點所需要的，所以請務必保存好。

*[初始化紀錄](./kube-init.log)*

## Installing Pod Network
從下列列表選擇一個 `Pod network` 安裝。

**Developers Official Links**
- [flannel](https://github.com/coreos/flannel)
- [romana](https://github.com/romana/romana)
- [kube-router](https://github.com/cloudnativelabs/kube-router)
- [canal](https://github.com/projectcalico/canal)

本教程選擇安裝 `Canal 1.7`。
```bash
u16-kube-master:~$ sudo kubectl apply -f https://raw.githubusercontent.com/projectcalico/canal/master/k8s-install/1.7/rbac.yaml
u16-kube-master:~$ sudo kubectl apply -f https://raw.githubusercontent.com/projectcalico/canal/master/k8s-install/1.7/canal.yaml
```

## Joining your nodes
貼上初始化成功所產生的 `kubeadm join`，就可以加入節點。

```bash
u16-kube-node01:~$ sudo swapoff -a
u16-kube-node01:~$ sudo kubeadm join --token 2cc44c.62db142d950a9915 10.211.55.20:6443 --discovery-token-ca-cert-hash sha256:150a48ff057dc91a0314fc1454fda86a023f798cc2abd472ced88e9a3365818d
[kubeadm] WARNING: kubeadm is in beta, please do not use it for production clusters.
[preflight] Running pre-flight checks
[discovery] Trying to connect to API Server "10.211.55.20:6443"
[discovery] Created cluster-info discovery client, requesting info from "https://10.211.55.20:6443"
[discovery] Requesting info from "https://10.211.55.20:6443" again to validate TLS against the pinned public key
[discovery] Cluster info signature and contents are valid and TLS certificate validates against pinned roots, will use API Server "10.211.55.20:6443"
[discovery] Successfully established connection with API Server "10.211.55.20:6443"
[bootstrap] Detected server version: v1.8.4
[bootstrap] The server supports the Certificates API (certificates.k8s.io/v1beta1)

Node join complete:
* Certificate signing request sent to master and response
  received.
* Kubelet informed of new secure connection details.

Run 'kubectl get nodes' on the master to see this machine join.
```

## Verify
檢驗節點是否加入，並正常運作。

```bash
u16-kube-master:~$ sudo kubectl get nodes
NAME          STATUS    ROLES     AGE       VERSION
u16-kube-master   Ready     master    23h       v1.8.4
u16-kube-node01   Ready     <none>    22h       v1.8.4
```

檢驗系統服務是否正常運作。
```bash
u16-kube-master:~$ sudo kubectl get pods --all-namespaces
NAMESPACE     NAME                                  READY     STATUS    RESTARTS   AGE
kube-system   canal-f4tgd                           3/3       Running   3          22h
kube-system   canal-hj4zh                           3/3       Running   0          22h
kube-system   etcd-kube-master                      1/1       Running   1          22h
kube-system   kube-apiserver-kube-master            1/1       Running   1          22h
kube-system   kube-controller-manager-kube-master   1/1       Running   1          22h
kube-system   kube-dns-545bc4bfd4-pmwh2             3/3       Running   0          23h
kube-system   kube-proxy-wxgkw                      1/1       Running   0          22h
kube-system   kube-proxy-x7hhk                      1/1       Running   1          23h
kube-system   kube-scheduler-kube-master            1/1       Running   1          22h
```
