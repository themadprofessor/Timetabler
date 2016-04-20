#!/usr/bin/env bash
if [[ "$PWD" =~ "Timetabler/Installer" ]]; then
    cd ../test
    if [[ "$PWD" =~ "Timetabler/test" ]]; then
        rm -fvr ./*
    else
        echo "Im in the wrong working directory"
        echo $PWD
    fi
else
    echo "Im in the wrong working directory"
    echo $PWD
fi