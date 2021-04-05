#!/bin/sh

ST_PROJECT_DIR=../sportstracker
BUILD_DIR=./build
PACKAGE_DIR=./package

ST_VERSION=7.8.0

# cleanup
rm -fr $BUILD_DIR
rm -fr $PACKAGE_DIR

# copy SportsTracker files and documentation
mkdir $BUILD_DIR
cp $ST_PROJECT_DIR/target/sportstracker-$ST_VERSION.jar $BUILD_DIR
cp -R $ST_PROJECT_DIR/target/lib $BUILD_DIR
cp -R $ST_PROJECT_DIR/docs $BUILD_DIR

# use type app-image, deb or rpm depending on the needed package type
$JAVA_HOME/bin/jpackage \
    --verbose \
    --type deb \
    --input $BUILD_DIR \
    --main-jar sportstracker-$ST_VERSION.jar \
    --dest $PACKAGE_DIR \
    --name SportsTracker \
    --app-version $ST_VERSION \
    --vendor "Saring.de" \
    --copyright "(C) 2021 Stefan Saring" \
    --description "Application for tracking your sporting activities." \
    --linux-app-category "Sports;Utility" \
    --icon ./icons/linux/SportsTracker.png \
    --license-file $BUILD_DIR/docs/LICENSE.txt \
    --module-path $JAVA_HOME/jmods \
    --add-modules java.base,java.desktop,java.logging,java.net.http,java.scripting,java.sql,java.xml,jdk.crypto.ec,jdk.localedata,jdk.jsobject,jdk.unsupported

# delete temporary build directory
rm -fr $BUILD_DIR
