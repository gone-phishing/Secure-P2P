#!/bin/sh

echo "Compiling host server files"
javac host_server/*.java

if [ $? -eq 0 ]; then
	echo "Compiling peer node files"
	javac peer/*.java
	if [ $? -ne 0 ]; then
		echo "Peer node compilation errors"
	else
		echo "Compilation Success !!"
	fi
else
	echo "Host server compilation errors"
fi