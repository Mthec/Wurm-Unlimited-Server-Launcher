#!/usr/bin/env bash
if test -e "logging.properties"; then
	LOGGING=-Djava.util.logging.config.file=logging.properties
fi

if test -d "runtime"; then
    RUNTIME=runtime
else
    RUNTIME=../runtime
fi

LD_LIBRARY_PATH="$PWD/nativelibs" ./$RUNTIME/jre1.8.0_60/bin/java "-Dworkdir=$PWD" "-Djava.library.path=$PWD/nativelibs" $LOGGING -Xmn256M -Xms512m -Xmx2048m -XX:+OptimizeStringConcat -XX:+AggressiveOpts -jar ./serverlauncher.jar "$*"
