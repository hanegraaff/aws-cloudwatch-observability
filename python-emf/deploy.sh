#!/bin/sh

rm -rf ./deploy
mkdir ./deploy  

cp ./src/* ./deploy

pip3 install -r ./deploy/requirements-deploy.txt -t ./deploy/

cd ./deploy && zip -r ../lambda_deploy.zip *
rm -rf ./deploy