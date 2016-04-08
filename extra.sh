#!/bin/bash

rm -rf jar/cn
rm -rf jar/*.properties
rm -rf jar/*.sql
rm -rf jar/*.xml
rm -rf jar/WEB-INF

rm -rf target

tar zxf jar.tar.gz
cp -r target/classes/* jar/

cp backup/main.html jar/WEB-INF/static/html/

rm -rf target