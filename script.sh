#!/bin/bash

rootdir=$(pwd)
for gradlewfile in $(find . -name gradlew); do
    dir=$(dirname $gradlewfile)
    echo 'Building' $dir
    cd $dir
    ./gradlew test
    if [ $? -ne 0 ]; then
        echo "Error while running in "
        echo $dir
        exit 1
    fi
    cd $rootdir
done
