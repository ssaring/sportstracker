Polarpersonaltrainer importer
-----------------------------

Overview:

This tool comes handy when you want to import data exported from
http://polarpersonaltrainer.com website in SportsTracker
(http://www.saring.de/sportstracker/index.html).

How does it works:

The SportsTrackes stores key information about sport activities in an XML file
($HOME/.sportstracker/exercises.xml). There are stored information about kind 
of sport activity, date, time, duration, distance, avg speed. The importer
updates the exercises.xml file with information from the given file.
The importer checks for duplicates based on date and time so if an exercise was
already imported it will be skipped to preserve the modifications you may have
done manually.

The export format is called by Polar: Polar Personaltrainer.com data export. The
description of the format is located at
http://www.polarpersonaltrainer.com/schemas/ped-1.0.xsd.

The file to import must have a .ped extension (ie: username_10.04.2010_export.ped).

You should copy the PedImporter.sh or PedImporter.bat script in the same
directory as SportsTracker.jar.

Usage:

 -d,--datadir <arg>        Directory where the SportsTracker data are
                           stored, default $HOME/.sportstracker
 -f,--filename             Input file exported from polarpersonaltrainer.com
 -n,--dry-run              if this parameter is set, no data is written to
                           disk
    --sportSubType <arg>   sport-subtype id from <datadir>/sport-types.xml,
                           default 1
    --sportType <arg>      sport-type id from <datadir>/sport-types.xml,
                           default 1

Example of usage on Linux/Unix:

    ./PedImporter.sh -f username_10.04.2010_export.ped --sportType 2

Example of usage on Windows:

     PedImporter.bat -f username_10.04.2010_export.ped --sportType 2

Note that the needed sport type and subtype ID's can be found in the
sport-types.xml (located in $HOME/.sportstracker).

Disclaimer:

The SportsTracker Importer was tested on Debian GNU/Linux and Windows XP with
Sun Java Virtual Machine version 6.
You should backup your $HOME/.sportstracker directory before using the importer.

Enjoy,
Philippe Marzouk <philm@users.sourceforge.net>
