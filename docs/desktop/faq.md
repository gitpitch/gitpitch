# Desktop FAQ

### How to Download

Complete download instructions are provided in the [Desktop Download Guide](/desktop/download.md). These same download instructions can be used to pull the latest updates to desktop app.

?> To make sure you are using the latest and greatest version of the desktop app we recommend  that you use these download instructions periodically to pull updates of the desktop image.

### Change Default Port

The default port for desktop app is port *9000*. To set a custom port when launching the desktop you must make two changes on your launch command:

1. Update the **-p** port mapping to reflect you new port number and
1. Add a matching **PORT** environment variable

The following examples demonstrate how to change the desktop to use port *80* on launch:

<!-- tabs:start -->
#### ** Bash Alias **

```bash
alias gpd='docker run -it -v $PWD:/repo -p 80:80 -e PORT=80  gitpitch/4.0'
```

#### ** Docker Run **

```bash
docker run -it -v {WORKINGDIRECTORY}:/repo -p 80:80 -e PORT=80 gitpitch/4.0
```

#### ** Docker Compose **

```yaml
version: '3'
services:
  gitpitch:
    image: gitpitch/4.0
    volumes:
      - .:/repo
    ports:
      - "80:80"
    environment:
      - PORT=80
      - SWEEP=true
```

<!-- tabs:end -->


### Refresh Problems

Each time you create or update a file within your local working directory the desktop app detects those changes and attempts to automatically refresh the slide deck in your browser. If your browser does not auto-refresh (or fails to refresh cleanly) the first thing to try is to simply save your file again and watch for a clean refresh.

If the slide deck in your browser still does not auto-refresh or you are seeing long delays before your slide deck auto-refreshes in the browser, check the console output in the local shell where you launched the desktop app. If any exceptions are visible in the console output please report details to GitPitch [support](mailto:support@gitpitch.com).

Otherwise try re-launching the desktop using a [Minimal Working Directory](#minimal-working-directory).

#### Minimal Working Directory

The [launch command](/desktop/launch.md) for the desktop app takes a path to a working directory on your local file system. This path is mounted by the desktop app as a Docker volume. And then used to monitor and detect changes you make to your presentation files.

Due to performance bottlenecks associated with Docker volume mounts, if the local working directory contains a large number of files, the overall responsiveness of the auto-refresh feature may slow. Or may not work at all.

> Important! While your local working directory may contain only a small number of project source files, it is possible that the directory also contains a large number of generated build and runtime artifacts. These file artifacts can greatly increase the overall file count in your directory.

To overcome this performance bottleneck and enjoy fast auto-refresh, take the following steps:

1. Create a new working directory on your local file system.
2. Copy your presentation markdown and any file dependencies into the new working directory.
3. Then re-launch the desktop using this new working directory as follows:

```
docker run -it -v {MINIMAL}:/repo -p 9000:9000 gitpitch/4.0
```

Note, here **{MINIMAL}** is a placeholder. You must replace it with an absolute path to the new working directory on your local file system. This working directory should contain presentation files only.

Having re-launched the desktop, start making changes to your presentation markdown. If the minimal working directory solution works, you should see your slide deck quickly auto-refresh in the browser each time you modify files in your new working directory.

If you have re-launched the desktop using a minimal working directory and you still see slow refresh times or your browser does not refresh at all, please contact GitPitch [support](mailto:support@gitpitch.com).

### Reduce CPU Load

By default, the desktop app uses a <i>periodic file-change watcher</i> called **SWEEP mode** that monitors the working directory for file changes. When changes are found, your slide deck is automatically refreshed in the browser.

The default periodic sweep interval is **250 ms**. Periodic sweeps by the desktop place a load on the CPU of your host machine. In most cases, this load is negligible. However, if you would like to reduce the CPU load on your host machine you can take steps to try and further reduce this load.

#### MacOS, Linux, and Windows (WSL 2) Users

For MacOS, Linux and Windows (WSL 2) users you can disable **SWEEP mode** which causes the desktop to switch over to a more CPU-efficient **EVENT mode**. The following shows how to disable *sweep mode* when launching the desktop app using a *bash alias*:

<!-- tabs:start -->

#### ** Trial **

```bash
# Add GitPitch launch alias to your ~/.bashrc
alias gpd='docker run -it -v $PWD:/repo -e SWEEP=false -p 9000:9000 gitpitch/trial'
```

#### ** Paid **

```bash
# Add GitPitch launch alias to your ~/.bashrc
alias gpd='docker run -it -v $PWD:/repo -e SWEEP=false -p 9000:9000 gitpitch/4.0'
```

<!-- tabs:end -->


The following shows how to disable *sweep mode* when launching the desktop app using *docker run* syntax:

<!-- tabs:start -->

#### ** Trial **

```shell
docker run -it -v {LOCALWORKINGDIR}:/repo -e SWEEP=false -p 9000:9000 gitpitch/trial
```

#### ** Paid **

```shell
docker run -it -v {LOCALWORKINGDIR}:/repo -e SWEEP=false -p 9000:9000 gitpitch/4.0
```

<!-- tabs:end -->

Note, *{LOCALWORKINGDIR}* is a placeholder. You must replace it with an *absolute path* to a working directory on your local file system. The directory must exist. It can be empty. Or it can contain existing presentation files and assets.

To disable *sweep mode* using *docker-compose* use the following service description:

<!-- tabs:start -->

#### ** Trial **

```yaml
version: '3'
services:
  gitpitch:
    image: gitpitch/trial
    volumes:
      - .:/repo
    ports:
      - "9000:9000"
    environment:
      - PORT=9000
      - SWEEP=false
```

#### ** Paid **

```yaml
version: '3'
services:
  gitpitch:
    image: gitpitch/4.0
    volumes:
      - .:/repo
    ports:
      - "9000:9000"
    environment:
      - PORT=9000
      - SWEEP=false
```

<!-- tabs:end -->

#### Windows Users (Hyper-V Backend)

If your version of Docker Desktop for Windows is using the older Hyper-V Backend you can not use *event mode* as described above. However you can set a custom sweep interval to reduce the load on your CPU. Higher sweep intervals will reduce the load on your CPU.

For example, to activate a custom **500ms** sweep interval on launch use the **-e SWEEP** flag:

```
docker run -it -v {WORKINGDIR}:/repo -p 9000:9000 -e SWEEP=500 gitpitch/4.0
```

The minimum sweep interval permitted is the default **250ms**. While the maximum sweep internal permitted on launch is **1000ms**.

### Install Docker Desktop

The desktop app is delivered as a [Docker](https://www.docker.com/products/docker-desktop) image. The Docker runtime aka. Docker Desktop used to launch that image is available for MacOS, Linux, and Windows 10.

Docker installation instructions can be found here:

- [Docker Desktop for Mac](https://hub.docker.com/editions/community/docker-ce-desktop-mac)
- [Docker Desktop for Windows](https://hub.docker.com/editions/community/docker-ce-desktop-windows)
- [Docker Server for Linux](https://docs.docker.com/install)

The simplest way to verify your Docker installation is to run the following
command:

```
docker run hello-world
```

If running this command fails for any reason, you will need to resolve those issues before attempting to
[launch GitPitch Desktop](/desktop/launch.md).

