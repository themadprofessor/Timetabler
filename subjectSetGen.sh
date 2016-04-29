#!/usr/bin/env bash

ID=1
MAX_SUBJECT=14
MAX_SET=5
MAX_YEAR=5
CURR_SUBJECT=1
CURR_SET=1
CURR_YEAR=1

while [ ${MAX_SUBJECT} -gt ${CURR_SUBJECT} ]; do
    while [ ${MAX_SET} -gt ${CURR_SET} ]; do
        while [ ${MAX_YEAR} -gt ${CURR_YEAR} ]; do
            echo "$ID,$CURR_SUBJECT,$CURR_SET,$CURR_YEAR" >> subjectSets.csv
            echo "$ID,$CURR_SUBJECT,$CURR_SET,$CURR_YEAR"
            ((ID++))
            ((CURR_YEAR++))
        done
        CURR_YEAR=1
        ((CURR_SET++))
    done
    CURR_SET=1
    ((CURR_SUBJECT++))
done