#!/bin/sh

JPACKAGER_HOME=./jdk.packager-linux
BUILD_DIR=./build
PACKAGE_DIR=./package

rm -fr $PACKAGE_DIR

# use create-image or create-installer depending on the needed package type
$JPACKAGER_HOME/jpackager create-installer \
    --verbose \
    --echo-mode \
    --input $BUILD_DIR \
    --main-jar ./sportstracker-7.5.1-SNAPSHOT.jar \
    --class de.saring.sportstracker.STMain \
    --output $PACKAGE_DIR \
    --identifier de.saring.sportstracker \
    --name SportsTracker \
    --version 7.5.1 \
    --vendor Saring.de \
    --copyright "(C) 2019 Stefan Saring" \
    --description "Application for tracking your sporting activities." \
    --category "Sports;Utility" \
    --icon ./icons/linux/SportsTracker.png \
    --license-file docs/LICENSE.txt \
    --module-path $JAVA_HOME/jmods \
    --add-modules java.base,java.desktop,java.logging,java.scripting,java.sql,java.xml,jdk.localedata,jdk.jsobject,jdk.unsupported \
    --strip-native-commands
