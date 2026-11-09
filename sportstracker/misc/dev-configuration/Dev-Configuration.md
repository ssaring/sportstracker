# Development Configuration

## Source Code Formatting

All SportsTracker developers should use the same coding style and format for all Java and Kotlin source files. The main
benefits of a consistent code format  are:

- code is easier to read
- fewer problems when merging files
- code reviews contain just the feature changes, not tons of custom formatting changes

This project uses automatic source code formatting and validation by using the google-java-format tool, which complies 
to the Google Java Style. The format integration into the Maven build system is done by using the spotless plugin.

For Kotlin code, the project uses the ktfmt tool, which complies to the Kotlin language style definition. It's also 
integrated in Maven by the same spotless plugin.

Links:
- https://github.com/google/google-java-format
- https://google.github.io/styleguide/javaguide.html
- https://github.com/diffplug/spotless/tree/main/plugin-gradle#google-java-format
- https://plugins.jetbrains.com/plugin/8527-google-java-format
- https://kotlin.github.io/ktfmt/

The coding style validation and formatting can be executed by these Maven goals:

- `mvn spotless:check` => validates the coding style (read-only)
- `mvn spotless:apply` => formats all Java and Kotlin source files
- `mvn verify` => builds and verifies the project, includes the coding style validation 


## IntelliJ IDEA Configuration (Version 2026.3)

Open the Project:

- Open Project in launch screen (or `File -> Open`)
- Select and open the `pom.xml` file in the SportsTracker root directory
- Select the `Open as Project` option

For automatic formatting of Java sources the `google-java-format plugin` is needed from the JetBrains Marketplace.
After installation the plugin needs to be enabled and configured for the SportsTracker project:

- Open `Settings -> Tools -> google-java-format Settings`
  - Enable `Enable google-java-format`
  - Select code style `Default Google Java style`
  - Set Max line length to 120
- Open `Settings -> Tools -> Actions on Save`
  - Enable `Reformat code` for file type Java (format whole file)
  - Enable `Optimize imports` for file type Java

For automatic formatting of Kotlin sources there is the `ktfmt` plugin in the JetBrains Marketplace.
Unfortunately this plugin can't be integrated well with the Java styleguide definition. As this project contains Java
and Kotlin sources, the goal is to use a similar styleguide (for indentation etc.). This is the default configuration
of the Spotless plugin in the Maven build, but the `ktfmt` IntelliJ plugin behaves differently.

The workaround is to execute the formatting manually after Kotlin implementation by the Maven goal `spotless:apply`. 
