About SportsTracker
-------------------

SportsTracker is an application for people who want to record their
sporting activities. It is not bound to a specific kind of sport, the user
can create categories for all sport types such as cycling, running, 
swimming or tennis.

The main advantage is a good overview of your exercises and you can easily
create diagrams and statistics for specific time ranges and sport types. In 
the calendar you can also track your body weight or create note entries, e.g. 
the training plan or upcoming sport events.

All the application data is stored in XML files. So it should be easy to
access it with other tools or to write importers and exporters for other
applications.

If you own a heartrate monitor with a computer interface you can display
the recorded exercise files and evaluate the diagrams with the integrated
ExerciseViewer application. 
You can organize them by attaching the recorded files to the exercise 
entries. When adding new exercises you can import the data from the 
recorded exercise files.

ExerciseViewer supports Polar, CicloSport, Garmin, Timex, Oregon, HOLUX and
Kalenji heartrate monitors. This is the current compatibility list (other 
monitors might work too, but I can't test them, user feedback is welcome):

  - Polar S610(i)      (tested)
  - Polar S710(i)      (tested)
  - Polar S720i        (tested)
  - Polar S725         (tested)
  - Polar S625x        (tested, HRM files only)
  - Polar S410         (tested, RAW files untested)
  - Polar S510         (tested)
  - Polar S520         (tested, RAW files untested)
  - Polar RS200SD      (tested)
  - Polar RS400        (initial support)
  - Polar RS800        (initial support)
  - Polar F6           (tested)
  - Polar F11          (tested)
  - Polar CS600        (tested, HRM files only)
  - CicloSport HAC4    (tested)
  - CicloSport HAC4Pro (tested)
  - CicloSport HAC5    (tested)
  - Garmin Edge        (tested with Edge 500 and Edge 705, FIT and TCX files)
  - Garmin Forerunner  (tested with Forerunner 305, 910XT, FIT and TCX files)
  - Garmin Oregon      (tested with Oregon 450, GPX files)
  - Timex Ironmen Race Trainer (tested)
  - Timex Ironmen Run Trainer (tested)
  - Timex Ironman Global Trainer  (tested)
  - Oregon Scientific SmartSync WM100 (tested)
  - HOLUX FunTrek      (tested with FunTrek 130, GPX files)  
  - Some Sony Ericsson mobiles (tested with W580i)
  - W Kalenji 300      (tested, GPX imports)
  - CW Kalenji 700     (tested, GPX imports)
  - All devices recording GPX files (tested some models)  

It's also possible to view HRM exercise files (downloaded with the Polar
Software for Windows).

Users of heartrate monitors with an integrated GPS receiver (e.g. the Garmin
Edge series) can also view the track of the recorded exercise in an inter-
active map viewer component.

SportsTracker itself is not able to download the exercise files from the 
heartrate monitor. You need to use one of these tools for it:

  - 's710' for many Polar models (S6XX, S7XX, ...), at least version 0.19
    URL: http://daveb.net/s710
  - 'rs200-decoder' for Polar RS200
    URL: http://sourceforge.net/projects/rs200-decoder
  - 'RS400 Tools' for the Polar RS400 (and probably RS800) monitor
    URL: http://users.tkk.fi/jjvayryn/polar_f55_hrm.html
  - SonicRead for Polar S410, S510 and S520
    URL: http://code.google.com/p/sonicread
  - 'F6 Split Tool' for Polar F6 and F11 (also needs 'rs200-decoder')
    URL: http://toazter.ch/sites/F6SplitTool
  - 'HAC4Linux' for CicloSport HAC4
    URL: http://sourceforge.net/projects/hac4linux
  - SonyEricsson Importer for some Sony Ericsson mobiles
    URL: http://luka.tnode.com/software/sonyericsson-importer-sportstracker
  - kalenji-gps-watch-reader for Kalenji GPS watches (imports GPX files)
    URL: http://code.google.com/p/kalenji-gps-watch-reader

Garmin users don't need special transfer software, the exercise files can
be downloaded by using the USB mass storage support.

Users of Polar S725 monitors needs to keep the default settings (80 - 160)
of the 'Exercise Heartrate Range Summary'. This is right now the only known
method to detect the S725 exercise file type, because it needs different
parsing compared to other S7XX models (this is not necessary for parsing
HRM files of S725).

Users of the website PolarPersonalTrainer.com can open their exported exercise
files (.ped extension) in ExerciseViewer. It's also possible to import all
exported exercises with the included PolarPersonalTrainer-Importer utility.


Requirements
------------

SportsTracker is an application for the Java platform written in Java and
Groovy. It was developed and tested with the Oracle JVM, other JVM
implementations will probably work too.
For running SportsTracker you just need the Java SE Runtime Environment
(JRE) 8u20 or greater, the Oracle JRE can be downloaded from:
http://www.java.com

If you want to download exercise files from your heartrate monitor you also 
need one of the download tools listed above.

The application was tested on GNU/Linux (e.g. Ubuntu 12.04), Windows (XP,
Vista, 7) and Mac OS X (10.7 - 10.9), although it should work on all systems
with the required Java Runtime Environment.


Installation and Start
----------------------

For installation you need to download the ZIP archive with the application
binaries from the SourceForge project page. Then extract this archive to a
directory of your choice.
On Windows systems you can start the application just by starting the file 
'sportstracker-x.y.z.jar'.
On Unix-like systems you need to start it by using the command 
'java -jar sportstracker-x.y.z.jar' from the application directory.

Mac OS X users will probably prefer the installation of the SportsTracker
OS X application bundle, the appropriate download is available too. This
bundle does not contain Java, the JDK or JRE must be installed before.

The default directory for the application data is '$HOME/.sportstracker',
e.g. '/home/foo/.sportstracker' for the Linux user foo. You can also specify
another directory with the '--datadir' command line parameter. 
Example: 'java -jar sportstracker-x.y.z.jar --datadir=.' stores the
application data in the SportsTracker installation directory. So you're able
to put SportsTracker and it's data on an USB stick and use it on any available
computer.


Usage
-----

This is a short introduction for the usage of the application:

Before you can add exercises you need to create a list of your sport types
in the editor dialog. Examples for sport types are "cycling", "running" or
"swimming". For sport types which are not endurance related (e.g. "tennis")
you need to specify that distance will not be recorded for such exercises.
The distance record mode can only be changed for new sport types or when no
exercises for this sport type exist.
By assigning a custom color for each sport type the list of exercises looks
much more clear.
For each sport type you need to create at least one subtype. Subtype examples
for cycling are "MTB tour", "MTB race", "Road tour" and so on. If subtypes
do not make sense for your sport type just create a subtype called "default".
You can also define a list of equipment for a sport type (optional), e.g.
specific bikes for cycling or shoes for running.

After that it's possible to add and edit exercises. In the exercise dialog
you need to specify the date, the sport type, the subtype and the intensity.
You also need to enter the distance, the average speed and the duration. Only
2 of these 3 values needs to be entered, the third one will be calculated
automatically. The default calculation is for duration, but you can select
another one of these values.
When you choose a sport type for which the distance will not be recorded
then you only need to enter the duration of the exercise.
All the other inputs are optional. You can select the equipment used for the
exercise when there is equipment defined for the selected sport type.
Data such as the route description can be added to the comment text.

Note entries can be added for special dates, so you can e.g. enter your
training plan or descriptions of sport events. You can also track your body
weight by adding weight entries for special dates. Note and weight entries
don't need the definition of sport types.

Entries of all types can be simply copied. This is very useful when you have
many similar exercises or weight entries. You just select the entry to copy 
and start the copy action from the context or main menu. It displays the Add
dialog for the copied entry with all data prefilled. You just need to enter the
new date.

The calendar view displays all exercise, note and weight entries of the 
selected month. The last column contains the distance and duration summary for
all exercises of the appropriate week. New exercise entries can be added by
double clicking the appropriate day cell. Note and weight entries can be added
by using the context menu inside a day cell. An existing entry can be edited
by double clicking it.

Additionally there are special list views for all exercise, note and weight
entries. These list views are very helpful for the analysis, especially when
using sorting and filters.

Users of heartrate monitors (HRM) can assign the recorded file to the exercise.
Most of the exercise data can be imported from this file, so it does
not need to be entered manually. The exercise files can also be viewed with 
the integrated ExerciseViewer application, which displays all the recorded
data and diagrams for it (except power data).

HRM exercise files can be easily imported by drag & drop. The user must drag
one single HRM file from the systems file manager and drop it to a day cell in
the calendar view. If there is an exercise entry under the mouse cursor then 
the HRM file will be assigned to this exercise. Otherwise a new exercise will
be created and the data will be imported from the HRM file.
Drag & drop has been successfully tested on Linux (Gnome 2.24 and KDE 4.1) and
Windows (XP).

For the creation of statistics the user needs to specify at least the 
calculation filter for the time range, e.g. the current month. It's also 
possible to set filters for the sport type, the subtype, the intensity, the
equipment and the comment, so only the specified exercises will be included in
the statistic calculation.

The user can create overview diagrams for the last 12 months, for all months
or weeks of a selected year or for a selectable time range of 10 years. 
The diagram will display the summary distance, duration, ascent and average 
speed for the sum of all sport types or splitted for each sport type. 
For sport subtype or equipment usage overview the diagram can also display the
distance per sport subtype or equipment for a selected sport type.
And finally it can also display the history of your body weight in the 
selected time range.

If the heartrate monitor has an integrated GPS receiver and stores the 
location data in the exercise file (e.g. in TCX files from the Garmin Edge 
series), then ExerciseViewer will show the exercise track inside a map viewer
component. This interactive map is zoomable and moveable, so it's easy to 
view all details of the track. Tooltips on all trackpoints will show you
further informations. 
The green waypoint is the start, the red is the end position and the white
waypoints are the lap split positions. 
The map data will be downloaded on demand from the OpenStreetMap project
(http://www.openstreetmap.org).

New users, which have many Polar HRM files recorded before switching to
SportsTracker, can import all HRM files at once by using the external tool
'sportstracker-importer', located in the 'misc' directory.


Notes for Garmin users
----------------------

The data stored in the recorded TCX files is sometimes wrong or imprecise.
ExerciseViewer can only display the stored data, it can't decide whether the
recorded data makes sense or not.

The speed calculated by the distance between two trackpoints is sometimes
impossible. Example in test file Edge705-Running-Heartrate-2Laps.tcx 
(from Edge 705, same problem in Forerunner 305 files):
- the distance between 07:47:43 and 07:47:47 is about 182.7 meters 
- the speed in these 4 seconds is 164.4 km/h => impossible for a runner 

The maximum speed stored for each lap is often wrong, example in test file
Edge705-Running-Heartrate-2Laps.tcx (from Edge 705):
- the calc. average speed is 11.76 km/h, the maximum speed is 10.085 km/h!
Workaround: the speed will be calculated for each trackpoint, so the 
maximum speed is taken from there.

The official "Garmin Training Center" software shows similar problems for
the included test exercise files. 


Notes for Timex users
---------------------

The ExerciseViewer is able to parse and display PWX exercise files. These
files are generated when the watch data is transferred from the watch to your
computer using the Timex Device Agent software (available only for Windows
and Mac OS X systems.)  
If you are connected to the Internet when transferring data from your watch,
the files are uploaded to the Training Peaks website and saved on your local
hard disk.  If you are not connected to to the Internet, the Timex Device
Agent software will save your PWX files on your hard disk until the next time
you are connected to the Internet and run Timex Device Agent.

Windows users can find the PWX files in the following directories:
- before transfer to trainingpeaks.com: %USERDIR%/Documents/TimexDA/Queued/
- after transfer to trainingpeaks.com: %USERDIR%/Documents/TimexDA/Sent/

The filenames created by the Timex Data Exchanger are automatically generated
but can be renamed without effecting usability. 
The standard format is:  TimexYYYYmmddHHMMSS_1.pwx
(YYYYmmdd and HHMMSS are the date and time when the exercise was started)

The initial version of the parser only works with data collected in CHRONO
mode. The watch does allow you to operate in both CHRONO and INTERVAL mode at
the same time so if you like to use INTERVAL mode on the watch, you can still
get your heart rate data by simultaneously operating in CHRONO mode.

If you wish to connect to the Internet but not send your data to the Training
Peaks website, you must block the Timex Device Agent with your Firewall. In
this case, the data files will be stored as though you were not connected to
the Internet.


Developer Requirements
----------------------

For compilation of the SportsTracker sources you need:
  - Java SE Development Kit (JDK) 8u20 or greater
    (from http://www.oracle.com/technetwork/java/)
  - Maven 3.0.3 or greater
    (from http://maven.apache.org)

Tested IDE's (should work an any IDE with Maven support)
  - IntelliJ IDEA 13 Community Edition (http://www.jetbrains.com/idea/)
    => preferred IDE, project files are in VCS
  - NetBeans IDE 8.0 (from http://www.netbeans.org)
    (Maven support is included, Groovy plugin needs to be installed)
  - Eclipse 4.3 (from http://eclipse.org) with following plugins:
    - Groovy-Eclipse (from http://groovy.codehaus.org/Eclipse+Plugin)
      - with Groovy-Eclipse Feature
      - with Groovy Compiler Feature
      - with Groovy-Eclipse M2E integration

The Maven build configuration supports all typical goals (clean, compile,
test, package, ...). The project is splitted into following modules (Maven
multi project), so it's not possible to create circular module dependencies.

  - st-parent: 
    Maven parent project with shared configuration and dependencies 
  - sportstracker: 
    Main application component
  - st-exerciseviewer: 
    Component for parsing and displaying HRM exercise files
    (was named PolarViewer before, but support now many other devices too)
  - st-util: 
    Component with common util classes for calculation, UI and more
  
In NetBeans you can open the project by "Open project", you need to select the
project root directory and import all required projects.
In Eclipse you can open the project by "Import -> Existing Maven Projects",
you need to select the project root directory with all child modules here.

SportStracker can be started from the IDE by executing the class
"de.saring.sportstracker.gui.STMain".
It can also be started from command line after execution of "mvn package"
with the command (inside the project root directory):
  java -jar sportstracker/target/sportstracker-x.y.z.jar

The SportsTracker project uses the following libraries:

  - BSAF 1.9.2 (Better Swing Application Framework)
    (http://kenai.com/projects/bsaf)
      License: Lesser General Public License 2.1 (LGPL)
  - Groovy 2.3.3 (http://groovy.codehaus.org)
      License: Apache License v2.0
  - Guice 3.0 (http://code.google.com/p/google-guice/)
      Includes: javax.inject-1.jar
      License: Apache License v2.0
  - JFreeChart 1.0.17 (http://www.jfree.org/)
      License: Lesser General Public License (LGPL)
  - JDOM 2.0.2 (http://www.jdom.org)
      License: Apache-style open source license
  - SwingX 1.6.5-1 (https://swingx.dev.java.net/)
      License: Lesser General Public License (LGPL)
  - JXMapViewer2 1.3.1 instead of SwingX-WS
      URL: https://github.com/msteiger/jxmapviewer2
      Includes: commons-logging-1.1.1
      License: Lesser General Public License (LGPL)
  - MiG Layout 3.7.4 (http://www.miglayout.com/)
      License: BSD License
  - ControlsFX 8.0.6 (http://controlsfx.org/)
      License: BSD 3-Clause License
  - commons-cli 1.2 (http://commons.apache.org/cli/)
      License: Apache License v2.0
  - JUnit 4.11 (http://www.junit.org)
      License: Common Public License v1.0
  - Mockito 1.9.5 (http://code.google.com/p/mockito/)
      License: MIT License
  - Flexible & Interoperable Data Transfer (FIT) Protocol SDK 4.20
      URL: http://www.thisisant.com/pages/products/fit-sdk
      License: FIT Protocol License (open source by Dynastream / Garmin)
      License URL: http://www.thisisant.com/pages/ant/fit-license

All dependencies will be downloaded automatically by Maven. JFreeChart and the
FIT library are missing in the Maven central repository, so I've created my own
repository for them. It's available at: http://saring.de/st-maven-repo/


If you're wondering why the complete application has been ported from the .NET
platform (C# language, running on Mono) to the Java platform (Java and Groovy
language), here are the most important reasons:

  - much better tooling support for development, e.g. IDE's, debuggers,
    profilers, refactoring tools and so on
  - much more usefull open source libraries, e.g. for diagram creation
  - support for more operating systems (e.g. MacOS X, ...)
  - less installation problems (no .NET runtime required on Linux or Mac OS X,
    no GTK+ libraries required on Windows and Mac OS X)
  - I18N support on all operating systems
  - the Java platform is much more mature and reliable than Mono


Source Code Management
----------------------

The SportsTracker project uses Git for Source Code Management (SCM), the project
repository is hosted at GitHub. 
URL: https://github.com/ssaring/sportstracker

Subversion was used until SportStracker 4.2.1, since version 4.3.0 the 
development has been switched to Mercurial and since version 6.1.0 to Git.

The SVN history was not imported to the Mercurial repository, there are too 
much revisions, which are not interesting anymore (also from Mono/.NET past).
So we used the chance to start with a lean Mercurial repository.
The SVN repository will stay online at SourceForge for being able to access 
all history revisions, if really needed. But there will be no future commits
to SVN aymore, they will all go to the Git repository.

You can find further details for the SCM usage and the collaboration workflow
at the GitHub project site and in the Git / GitHub documentation.


Contact
-------

The website of SportsTracker can be found at:
http://www.saring.de/sportstracker

You are welcome to use the forum and the bug system on the Sourceforge
project page (http://sourceforge.net/projects/sportstracker) when you
have comments, suggestions or problems with bugs.
The most recent version of this tool can also be found there.

If you want to contribute improvements or translations, feel free to fork the
GitHub repository and submit Pull Requests. It would be great when bigger 
changes could be discussed before starting the implementation.

Before creating a translation for your language please take a look at the file
I18N.txt. Translations can also be send by mail, if you are not familiar with
GitHub usage.

For direct email contact you can use the address: projects@saring.de 


License
-------

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

All the application icons are taken from the open source projects GTK+ 
(http://www.gtk.org) and Tango (http://tango.freedesktop.org) or they are
created based on icons from those projects (e.g. the application icon).


Stefan Saring
2014/08/22
