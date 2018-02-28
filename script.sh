#!/bin/bash

rootdir=$(pwd)
for gradlewfile in $(find . -name gradlew); do
    dir=$(dirname $runscript)
    echo 'Building' $dir
    cd $dir
    ./gradlew build --stacktrace
    if [ $? -ne 0 ]; then
        echo "Error while running in "
        echo $dir
        exit 1
    fi
    cd $rootdir
done
