#!/bin/bash

count=0
outFile="Timetabler.txt"
rm -f $outFile

for file in $(find ./src -name '*.java' -or -name 'index.html' -or -name 'table.js'); do
	echo $file
	echo $file >> $outFile
	cat $file >> $outFile
	echo "" >> $outFile
	let "count += 1"
done

outFile="Installer.txt"
rm -f $outFile
for file in $(find ./Installer/src -name '*.java' -or -name '*.html'); do
	echo $file
	echo $file >> $outFile
	cat $file >> $outFile
	echo "" >> $outFile
	let "count += 1"
done

echo "Done! Processed $count files"
