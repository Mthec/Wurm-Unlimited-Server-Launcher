
if exist logging.properties set LOGGING=-Djava.util.logging.config.file=logging.properties

IF EXIST runtime (
    SET "run=%~dp0runtime"
) ELSE (
    SET "run=%~dp0../runtime"
)

"%run%/bin/java" "-Dworkdir=%CD%" "-Djava.library.path=%CD%/nativelibs" %LOGGING% -Xmn256M -Xms512m -Xmx2048m -XX:+OptimizeStringConcat -XX:+AggressiveOpts -jar serverlauncher.jar  %*
