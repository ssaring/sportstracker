SET JPACKAGER_HOME=.\jdk.packager-windows
SET BUILD_DIR=.\build
SET PACKAGE_DIR=.\package
SET WINDOWS_SYSTEM32=C:\Windows\System32
SET PATH=%PATH%;"C:\Program Files (x86)\InnoSetup-5\"

RMDIR /S/Q %PACKAGE_DIR%

REM the jpackager files needs to be copied to %JAVA_HOME% (just on Windows),
REM otherwise jpackager can't find the java commands
XCOPY /Y %JPACKAGER_HOME%\jpackager.exe %JAVA_HOME%\bin
XCOPY /Y %JPACKAGER_HOME%\jdk.packager.jar %JAVA_HOME%\bin\
XCOPY /Y %JPACKAGER_HOME%\jdk.packager.jar %JAVA_HOME%\jmods\

REM use create-image or create-installer depending on the needed package type
%JAVA_HOME%\bin\jpackager create-image ^
    --verbose ^
    --echo-mode ^
    --input %BUILD_DIR% ^
    --main-jar ./sportstracker-7.5.1-SNAPSHOT.jar ^
    --class de.saring.sportstracker.STMain ^
    --output %PACKAGE_DIR% ^
    --identifier de.saring.sportstracker ^
    --name SportsTracker ^
    --version 7.5.1 ^
    --vendor Saring.de ^
    --copyright "(C) 2019 Stefan Saring" ^
    --description "Application for tracking your sporting activities." ^
    --category "Sports;Utility" ^
    --icon ./icons/windows/SportsTracker.ico ^
    --license-file docs/LICENSE.txt ^
    --module-path %JAVA_HOME%/jmods ^
    --add-modules java.base,java.desktop,java.logging,java.scripting,java.sql,java.xml,jdk.localedata,jdk.jsobject,jdk.unsupported ^
    --strip-native-commands

REM add these windows system DLLs, they are needed for app execution and might be missing on Windows systems 
XCOPY %WINDOWS_SYSTEM32%\MSVCP140.dll %PACKAGE_DIR%\SportsTracker\
XCOPY %WINDOWS_SYSTEM32%\VCRUNTIME140.dll %PACKAGE_DIR%\SportsTracker\
