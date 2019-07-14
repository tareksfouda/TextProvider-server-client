#!/bin/sh

./build.sh;

cd bin;

if [ -z $1 ]
	then
		java Client;
	else
		java Server ../$1;
fi
rm -rf lines;
cd ..;
