#!/bin/sh

if test -d "runtime"; then
    RUNTIME=runtime
else
    RUNTIME=../runtime
fi

java() {
	./$RUNTIME/jre1.8.0_60/bin/java $*
}
java -classpath serverlauncherpatcher.jar:javassist.jar org.gotti.wurmunlimited.patcher.PatchServerJar
chmod a+x WurmServerLauncher-patched
