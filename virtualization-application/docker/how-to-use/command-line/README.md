
### List containers info
```
$ sudo docker ps -a 
$ sudo docker ps -q
```

---

### Run a command in a running container
```
$ sudo docker exec -it nginx /bin/sh 
```

---

### Operate one or more containers 
```
$ sudo docker stop|start|restart nginx
```

---

### Inspect changes to files or directories on a container's filesystem
```
$ sudo docker diff nginx
```

---

### Fetch the logs of a container
```
$ sudo docker logs nginx
```

---

### Display a live stream of container(s) resource usage statistics
```
$ sudo docker stats [nginx]
```

---

### Show the history of an image 
```
$ sudo docker history nginx:origin
```

---

### Return low-level information on Docker objects
```
$ sudo docker inspect nginx:origin|nginx
```

---

### Remove one or more containers/images
```
$ sudo docker rm nginx
$ sudo docker rmi nginx:origin
```

---

## END
