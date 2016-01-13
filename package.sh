#!/bin/sh

version=$1
rm ./docker/net.nansore.cedalion_*.jar
cp ~/eclipse/dropins/plugins/net.nansore.cedalion_$version.jar docker
docker build -t brosenan/cedalion:$version docker/
docker tag -f brosenan/cedalion:$version brosenan/cedalion:latest
docker push brosenan/cedalion:$version

git commit -am $version
git tag -a $version
git push --all
