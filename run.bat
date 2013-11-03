@echo off

set CLASSPATH=config;classes;lib/gdata-base-1.0.jar;lib/gdata-client-1.0.jar;lib/gdata-core-1.0.jar;lib/gdata-maps-2.0.jar;lib/gdata-webmastertools-2.0.jar;lib/guava-10.0.1.jar;lib/json_simple-1.1.jar

java -cp %CLASSPATH% com.webkruscht.wmt.DownloadFiles


