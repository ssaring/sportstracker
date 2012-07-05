Documentation for Mac OS X App bundle creation
----------------------------------------------

Installation of appbundler:
- Download the Ant extension appbundler version 1.0ea from its homepage:
  http://java.net/projects/appbundler
- Install appbundler library in Ant directory. Copy file appbundler-1.0ea.jar
  to directory /usr/share/ant/lib.

App bundle creation:
- Run a successfull build of SportsTracker first!
- Go to directory sportstracker/misc/appbundler
- Just run "ant"
  => the created App bundle is named "SportsTracker.app" 
- Install the created App bundle

Notes:
- Tested successfully with JDK7u4 on Mac OS X 10.7.4
- The app icon must be in .icns format, conversion was done by the web service
  http://iconverticons.com
- The ST icon needs to be optimized for higher resolutions, looks ugly on Mac.
- Currently the App bundle contains the JRE 7u4, because there is no Applet 
  Launcher and WebStart yet. JDK7u6 will contain them, then the JRE doesn't
  need to be included anymore (huge, > 150 MB).
  Simply remove the runtime element in build.xml then...  
