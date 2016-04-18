#!/bin/bash

rm -rf jar/cn
rm -rf jar/*.properties
rm -rf jar/*.sql
rm -rf jar/*.xml
rm -rf jar/WEB-INF

rm -rf target

tar zxf jar.tar.gz
cp -r target/classes/* jar/

if [ -f backup/main.html ]; then
    cp backup/main.html jar/WEB-INF/static/html/
fi

if [ -f backup/login.html ]; then
    cp backup/login.html jar/WEB-INF/static/html/
fi

if [ -f backup/application.properties ]; then
    cp backup/application.properties jar/
fi

rm -rf target
