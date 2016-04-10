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

PORTAL_HOME=`dirname "$PRG"`/..
MAIN_CLASS=cn.com.xinli.portal.PortalApplication
JAVA_HOME=/opt/jvm/current
JAVA=${JAVA_HOME}/bin/java
LOG_FILE=${PORTAL_HOME}/logs/xpws.log

nohup ${JAVA} -Djava.security.egd=/dev/urandom -Djava.ext.dirs=${JAVA_HOME}/jre/lib/ext:lib -cp jar ${MAIN_CLASS} >> ${LOG_FILE} 2>&1 &

