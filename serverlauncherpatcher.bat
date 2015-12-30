IF EXIST runtime (
    "SET run=%~dp0runtime"
) ELSE (
    "SET run=%~dp0../runtime"
)

"%run%/bin/java" -classpath serverlauncherpatcher.jar;javassist.jar org.gotti.wurmunlimited.patcher.PatchServerJar
