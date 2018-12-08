# Compose
Docker Compose relies on Docker Engine for any meaningful work, so make sure you have Docker Engine installed either locally or remote, depending on your setup.

# Linux
Alternative Install Options
Install Compose on Linux systems
On Linux, you can download the Docker Compose binary from the Compose repository release page on GitHub. Follow the instructions from the link, which involve running the curl command in your terminal to download the binaries. These step by step instructions are also included below.

1. Run this command to download the latest version of Docker Compose:
```
sudo curl -L https://github.com/docker/compose/releases/download/1.21.0/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
```
Use the latest Compose release number in the download command.

The above command is an example, and it may become out-of-date. To ensure you have the latest version, check the Compose repository release page on GitHub.

If you have problems installing with curl, see Alternative Install Options tab above.

2. Apply executable permissions to the binary:
```
sudo chmod +x /usr/local/bin/docker-compose
```
3. Optionally, install command completion for the bash and zsh shell.

4. Test the installation.
```
$ docker-compose --version
docker-compose version 1.21.0, build 1719ceb
```
