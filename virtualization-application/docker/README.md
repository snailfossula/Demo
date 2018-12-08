# Docker
**Docker** 是一個開放原始碼軟體專案，讓應用程式布署在軟體容器下的工作可以自動化進行，藉此在 *Linux* 作業系統上，提供一個額外的軟體抽象層，以及作業系統層虛擬化的自動管理機制。

其利用 Linux 核心中的資源分離機制，例如 *cgroups*，以及 *Linux* 核心命名空間（*namespace*），來建立獨立的軟體容器（*containers*）。這可以在單一 *Linux* 實體下運作，避免啟動一個虛擬機器造成的額外負擔。

## [Structure](./structure)

- [Docker](./structure/#docker)
- [Image](./structure/#image)
- [Container](./structure/#container)

## [How to use](./how-to-use/)

- [Installing Docker](./how-to-use/#install)
- [Building image](./how-to-use/dockerfile/)
- [Running container](./how-to-use/)

## [Clusters](./clusters)

- [Swarm](./clusters/swarm)
- [Kubernetes](./clusters/kubernetes)

## [Deploy](./deploy)

- [Docker-compose](./deploy/docker-compose)
- [Stack](./deploy/stack)

## [Repository](./repository)

- [Distribution](./repository/distribution)
- [Harbor](./repository/harbor)
  - [Deploy in swarm](./repository/harbor/deploy-in-swarm)
