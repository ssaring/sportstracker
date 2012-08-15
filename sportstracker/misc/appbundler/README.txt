Documentation for Mac OS X App bundle creation
----------------------------------------------

Installation of appbundler:
- The App bundle is created by the Ant task appbundler (version 1.0ea)
  URL: http://java.net/projects/appbundler
- The appbundler library is included here, it does not need to be installed.

App bundle creation:
- Run a successfull build of SportsTracker first!
- Go to directory sportstracker/misc/appbundler
- Just run "ant"
  => the created App bundle is named "SportsTracker.app" 
- Install the created App bundle by moving it to the Application directory

Notes:
- Tested successfully with JDK7u6 on Mac OS X 10.7.4 and 10.8
- The app icon must be in .icns format, conversion was done by the web service
  http://iconverticons.com
- Sine JDK 7u6 for Mac OS X it's not needed to embed the JRE anymore. So the 
  App download is much smaller (12 MByte instead of 67). 
  BUT THE USER NEEDS TO INSTALL JDK 7u6 OR GREATER (OR AT LEAST THE JRE) BEFORE!
- If the JRE needs to be included, just uncomment the "runtime" element in the
  build.xml. The user does not need an installed JRE then.
