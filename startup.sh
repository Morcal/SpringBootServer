#!/bin/bash

## resolve links - $0 may be a link to maven's home
PRG="$0"

# need this for relative symlinks
while [ -h "$PRG" ] ; do
        ls=`ls -ld "$PRG"`
        link=`expr "$ls" : '.*-> \(.*\)$'`
        if expr "$link" : '/.*' > /dev/null; then
                PRG="$link"
        else
                PRG="`dirname "$PRG"`/$link"
        fi
done

PRG_DIR=`dirname "$PRG"`
EXECUTABLE=portal.sh

if [ ! -x "$PRG_DIR"/"$EXECUTABLE" ]; then
        echo Cant find "$PRG_DIR"/"$EXECUTABLE".
        exit 1
fi

exec "$PRG_DIR"/"$EXECUTABLE" "$@"
