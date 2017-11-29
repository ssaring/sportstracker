## LeafletMap

LeafletMap is a JavaFX component for displaying an OpenStreetMap based map
inside a JavaFX WebView by using the Leaflet JavaScript library.

There is a demo application inside the test directory which shows how to
use the LeafletMap component.

Both the LeafletMap component and the demo application are written in Kotlin.


#### Dependencies and used libraries

* Kotlin 1.2.0
* JavaSE 8 (tested with 8u151 and 9.0.1)
* Leaflet 1.0.3 (included)
    * Homepage: http://leafletjs.com/
    * License: BSD 2-Clause License
    * Documentation: http://leafletjs.com/reference-1.0.3.html
* leaflet-color-markers (included, modified)
    * Homepage: https://github.com/pointhi/leaflet-color-markers
    * License: not specified
* jackson-module-kotlin 2.9.1 (for the demo only, uses Jackson 2.8)
    * Homepage: https://github.com/FasterXML/jackson-module-kotlin
    * License: not specified


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

LeafletMap 1.0.2:

* Updated to Kotlin 1.2.0

LeafletMap 1.0.1:

* Updated to Kotlin 1.1.50
* API: added methods for setting the center position and zoom level
  (was only possible for tracks before)


#### License

(C) 2017 Stefan Saring

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.

You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
