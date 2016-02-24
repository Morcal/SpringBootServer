VA_HOME=/opt/jvm/current
JAVA=$JAVA_HOME/bin/java
PROGRAM=/opt/xpws/xpws-1.0.jar
LOG_FILE=/opt/xpws/xpws.log

#$JAVA -Djava.security.egd=/dev/urandom -jar $PROGRAM >> $LOG_FILE 2>&1 &
$JAVA -Djava.security.egd=/dev/urandom -Djava.ext.dirs=$JAVA_HOME/jre/lib/ext:lib -cp jar cn.com.xinli.portal.PortalApplication >> $LOG_FILE 2>&1 &

