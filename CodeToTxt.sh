#!/bin/bash

count=0
outFile="Timetabler.txt"
rm -f $outFile

for file in $(find ./src -name '*.java' -or -name 'html/index.html' -or -name 'table.js' -or -name '*.fxml'); do
	echo $file
	echo $file >> $outFile
	echo "" >> $outFile
	cat $file >> $outFile
	echo "" >> $outFile
	echo "" >> $outFile
	echo "" >> $outFile
	let "count += 1"
done

outFile="Installer.txt"
rm -f $outFile
for file in $(find ./Installer/src -name '*.java' -or -name '*.fxml'); do
	echo $file
	echo $file >> $outFile
	echo "" >> $outFile
	cat $file >> $outFile
	echo "" >> $outFile
	echo "" >> $outFile
	echo "" >> $outFile
	let "count += 1"
done

echo "Done! Processed $count files"
