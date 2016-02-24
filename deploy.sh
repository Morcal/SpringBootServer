#!/bin/bash

if [ ! -d target/classes ]; then
	echo not compiled yet.
	exit 1
fi

tar czf jar.tar.gz target/classes


