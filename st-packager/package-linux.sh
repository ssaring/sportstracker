#!/bin/sh

ST_PROJECT_DIR=../sportstracker
JPACKAGER_DIR=./jdk.packager-linux
BUILD_DIR=./build
PACKAGE_DIR=./package

ST_VERSION=7.5.1-SNAPSHOT

# cleanup
rm -fr $BUILD_DIR
rm -fr $PACKAGE_DIR

# copy SportsTracker files and documentation
mkdir $BUILD_DIR
cp $ST_PROJECT_DIR/target/sportstracker-$ST_VERSION.jar $BUILD_DIR
cp -R $ST_PROJECT_DIR/target/lib $BUILD_DIR
cp -R $ST_PROJECT_DIR/docs $BUILD_DIR

# use create-image or create-installer depending on the needed package type
$JPACKAGER_DIR/jpackager create-installer \
    --verbose \
    --echo-mode \
    --input $BUILD_DIR \
    --main-jar ./sportstracker-$ST_VERSION.jar \
    --class de.saring.sportstracker.STMain \
    --output $PACKAGE_DIR \
    --identifier de.saring.sportstracker \
    --name SportsTracker \
    --version $ST_VERSION \
    --vendor Saring.de \
    --copyright "(C) 2019 Stefan Saring" \
    --description "Application for tracking your sporting activities." \
    --category "Sports;Utility" \
    --icon ./icons/linux/SportsTracker.png \
    --license-file docs/LICENSE.txt \
    --module-path $JAVA_HOME/jmods \
    --add-modules java.base,java.desktop,java.logging,java.scripting,java.sql,java.xml,jdk.localedata,jdk.jsobject,jdk.unsupported \
    --strip-native-commands

# delete temporary build directory
rm -fr $BUILD_DIR
