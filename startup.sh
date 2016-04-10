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

EXECUTABLE=`readlink -e "$PRG_DIR"/"$EXECUTABLE"`

start_portal() {
        echo Enter directory $1
        cd $1

        if [ ! -d logs ]; then
                echo Creating logs directory...
                mkdir logs
        fi

        if [ ! -d apps ]; then
                echo Creating apps directory...
                mkdir apps
        fi

        echo Starting portal application...

        exec $2 "$@"
}

start_portal ${PRG_DIR} ${EXECUTABLE}
