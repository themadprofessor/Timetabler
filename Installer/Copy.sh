#!/usr/bin/env bash
if [[ "$PWD" =~ Timetabler/Installer ]]; then
    rm -f assets/Timetabler.*
    cd ../out/artifacts/Timetabler/
    rm -f *.html
    rm -f *.jnlp

    if [[ "$1" == "7z" ]]; then
        7z a /home/stuart/Programming/IdeaProjects/Timetabler/Installer/assets/Timetabler.7z ./*
    else
        zip -r /home/stuart/Programming/IdeaProjects/Timetabler/Installer/assets/Timetabler.zip ./*
    fi
else
    echo "In wrong working directory"
fi
