#!/bin/sh
#
# This scripts assumes it is copied along side SportsTracker.jar and
# the java command is in your PATH.

java -cp SportsTracker*.jar de.saring.polarpersonaltrainer.importer.PedImporter $@

