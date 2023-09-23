## LeafletMap

LeafletMap is a JavaFX component for displaying an OpenStreetMap based map
inside a JavaFX WebView by using the Leaflet JavaScript library.

There is a demo application inside the test directory which shows how to
use the LeafletMap component.

Both the LeafletMap component and the demo application are written in Kotlin.


#### Dependencies and used libraries

* Java SE 17 (tested with OpenJDK 21)
* OpenJFX 21
    * Homepage: https://openjfx.io/
    * License: GPL v2 + Classpath Exception
* Kotlin 1.9.10
    * Homepage: http://kotlinlang.org/
    * License: Apache License v2.0
* Leaflet 1.9.3 (included)
    * Homepage: http://leafletjs.com/
    * License: BSD 2-Clause License
    * Documentation: https://leafletjs.com/reference.html
* leaflet-color-markers (included, modified)
    * Homepage: https://github.com/pointhi/leaflet-color-markers
    * License: BSD 2-Clause License
* kotlinx.serialization 1.4.1 (for the demo only)
    * Homepage: https://github.com/Kotlin/kotlinx.serialization
    * License: Apache License 2.0


#### Status

* map viewer features:
    * supports OpenStreetMap, OpenCycleMap, HikeBikeMap, MtbMap and MapBox 
      layers, more can be added easily 
    * MapBox layer: a project specific token is required for MapBox! A test
      token for a limited time can be found in the Leaflet tutorial.
    * layers can be switched at runtime by the user
    * zoom and scale controls can be configured
    * scale supports metric and imperial units
    * markers in multiple colors can be displayed
    * tracks (routes) can be displayed
    * the map is zoomed properly to fit the track
    * tooltips can be displayed on the map
* colored markers are supported by the embedded "leaflet-color-markers" library
  (modified, e.g. added proper retina icon support)
* the leaflet and the leaflet-color-markers libraries are included locally, no
  download at runtime needed
* map viewer can be used offline, the route and the markers are shown without
  the map data
* the LeafletMapView component API can also be used from Java without problems
  (default method parameters are supported via @JvmOverloads)
* the demo application displays a GPS track read from a JSON file, the user can
  replay the track by using a position slider


#### Changelog

LeafletMap 1.0.8:
* Updated JavaFX / OpenJFX to version 21
* Updated Kotlin to version 1.9.10
* Updated Leaflet to version 1.9.3
* Updated library leaflet-color-markers (GitHub development version)

LeafletMap 1.0.7:
* Updated JavaFX / OpenJFX to version 19
  (contains critical bugfixes for Apple Silicon support)
* Updated Kotlin to version 1.7.10

LeafletMap 1.0.6:
* Updated Kotlin to version 1.6.10 (solves JDK 16/17 compatibility issues)
* Updated JavaFX / OpenJFX to version 17.0.1

LeafletMap 1.0.5:
* Added map layer "Satellite Esri" (contributed by Thomas Feuster, PR #211)
* Updated Leaflet to version 1.6.0, provides various fixes and performance
  improvements (contributed by Thomas Feuster, PR #219)
* Updated to Kotlin 1.4.31
* Updated to OpenJFX 16
* Use of kotlinx.serialization instead of jackson-module-kotlin dependency for
  JSON serialization (for test application only)
  -> provides better compatibility to the latest Kotlin versions 

LeafletMap 1.0.4:

* Updated to Kotlin 1.3.41
* Updated to OpenJFX 11.0.2

LeafletMap 1.0.3:

* Updated to JDK 11 (JDK 9 and JDK 10 are not supported by Oracle anymore)
  * JavaFX has been added as dependencies, not part of the JDK anymore
* Updated to Kotlin 1.3.11

LeafletMap 1.0.2:

* Updated to Kotlin 1.2.31
* added LeafletMapView method for deleting displayed markers and tracks 
  (contributed by Thomas Feuster)
* LeafletMapView can now be extended (it's not final anymore and child classes
  can execute custom scripts)
* added Marker interface for adding custom marker implementations

LeafletMap 1.0.1:

* Updated to Kotlin 1.1.50
* API: added methods for setting the center position and zoom level
  (was only possible for tracks before)


#### License

(C) 2020 Stefan Saring

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.

You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
