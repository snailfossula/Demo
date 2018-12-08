#!/bin/bash

if [ -z $2 ];
        then
        echo "rebu arg1 arg2 arg3"
        echo "arg1 = directory"
        echo "arg2 = hostname" 
        echo "arg3 = ip address."
        exit 0
fi

ssh-keygen -t rsa -b 4096 -f $1/$2 -q -N ""

echo "
Host $2
        HostName $3 
        User Username
        IdentityFile ~/.ssh/$1/$2" >> config

ssh-copy-id -i ~/.ssh/$1/$2 Username@$3

ssh -t $2 'echo "Match User Username
        PasswordAuthentication no
        PubkeyAuthentication yes" | sudo tee --append /etc/ssh/sshd_config;
        sudo service ssh restart'
