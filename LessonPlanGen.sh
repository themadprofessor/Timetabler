#!/usr/bin/env bash

ID=1
MAX_PERIOD=30
MAX_SUBJECT_SET=209
CURR_SUBJECT_SET=1

while [[ ${MAX_SUBJECT_SET} -gt ${CURR_SUBJECT_SET} ]]; do
    PRINT="$ID,,,$(shuf -i 1-${MAX_PERIOD} -n 1),$CURR_SUBJECT_SET"
    echo ${PRINT}
    echo ${PRINT} >> lessonPlans.csv
    ((ID++))
    PRINT="$ID,,,$(shuf -i 1-${MAX_PERIOD} -n 1),$CURR_SUBJECT_SET"
    echo ${PRINT}
    echo ${PRINT} >> lessonPlans.csv
    ((ID++))
    PRINT="$ID,,,$(shuf -i 1-${MAX_PERIOD} -n 1),$CURR_SUBJECT_SET"
    echo ${PRINT}
    echo ${PRINT} >> lessonPlans.csv
    ((ID++))
    PRINT="$ID,,,$(shuf -i 1-${MAX_PERIOD} -n 1),$CURR_SUBJECT_SET"
    echo ${PRINT}
    echo ${PRINT} >> lessonPlans.csv
    ((ID++))
    PRINT="$ID,,,$(shuf -i 1-${MAX_PERIOD} -n 1),$CURR_SUBJECT_SET"
    echo ${PRINT}
    echo ${PRINT} >> lessonPlans.csv
    ((ID++))
    ((CURR_SUBJECT_SET++))
done