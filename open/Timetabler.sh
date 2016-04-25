#!/bin/bash

ver=$(java -version 2>&1|awk -F\" '/version/ {print $2}')
echo ${ver}

if [[ -z ${ver} ]]; then
	echo "Java not installed!"
	exit
fi

IFS='.' read -a split <<< ${ver}
if [[ ${split[1]} -Gt ]]; then
	echo ""
fi
