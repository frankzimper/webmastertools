#!/bin/bash

CLASSPATH=`ls -1 lib/* | xargs | sed 's/\s/:/g'`
CLASSPATH="classes:config:$CLASSPATH"

java -cp $CLASSPATH com.webkruscht.wmt.DownloadFiles


