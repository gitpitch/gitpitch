# GitPitch Enterprise Prerequisites

GitPitch Enterprise is delivered as a [Docker container](https://docs.docker.com) running in a Linux server environment. At runtime the enterprise container depends on a [Docker volume](https://docs.docker.com/storage/volumes) at runtime. This volume is used:

1. To pass custom GitPitch server configuration to the live container.
1. To cache GitPitch server log data outside of the live container.
1. To cache GitPitch server runtime data outside of the live container.

For on-premises deployment, the _Docker volume_ is typically a dedicated directory on your host Linux server. The volume could be a drive on an external disk, such as a storage area network (SAN) disk. In all cases, we recommend a high-performance SSD.

!> For trial purposes any modern laptop or desktop should be adequate to install and launch the GitPitch Enterprise server. The recommendations that follow are for production environments.

Based on your expected user load, we recommend the following production hardware configurations:

Users   | CPUs | Heap Memory | Docker Memory | Docker Volume |
--------|------|--------|---------|----------|
10-500| 2    | 2GB    |  4GB   | 20GB    |
500-2000| 4    | 4GB   |  8GB  | 50GB |
2000-10000+| 4+    | 4GB  |  8GB  | 50GB+ |

These recommendations are estimates. Your specific patterns of usage and peak load will determine optimal configuration.

##### Note 1.

Heap Memory refers to the size of the Java Virtual Machine (JVM) heap. The default is 2GB. This setting is configurable on launch using the following syntax on the _docker run_ command:

```
-e JXMX=4g
```

##### Note 2.

Docker Memory refers to the memory resource limits for the Docker container.

##### Note 3.

Docker Volume refers to the storage drive shared between the host Linux server and the Docker container.
