#!/bin/sh

# Setting versions
VERSION='1.0.0'

cd ..
./gradlew clean build -x test

ROOT_PATH=`pwd`
echo $ROOT_PATH

echo 'api Docker Image build...'
cd $ROOT_PATH/api && docker build -t mungstore-api:$VERSION .
echo 'api Docker Image build Done'

echo 'consumer Docker Image build...'
cd $ROOT_PATH/consumer && docker build -t mungstore-consumer:$VERSION .
echo 'consumer Docker Image build Done'