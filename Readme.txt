The project is JavaFX visual analyzer of PNG files.
It loads and parses PNG image file and show all chunks in it with most of its properties (https://www.w3.org/TR/png/).
The analyzer was tested on Windows 11 with PNG Suite https://github.com/lunapaint/pngsuite (http://www.schaik.com/pngsuite/)
The analyzer does not parse a following chunk types: sRGB, cICP, mDCv, cLLI, acTL, fcTL, fdAT because there is no test PNG image for it in PNG Suite.

2) Prerequisites
For building analyzer from source files following programs should be installed:
- Java SDK 22;
- Apache Maven 3.9.3 or later.

3) Building and running application

3.1) Build and run from IDE using Maven
Download project source code from GitHub, import it to your IDE as project with existing pom.xml file, build and run.

3.2) Build from command-line using Maven
Download project source code from GitHub, change directory to project root directory "...\JfxPngAnalyzer".
Run below command from command-line to create image:

mvn clean compile javafx:jlink

It will create directory "...\JfxPngAnalyzer\target\JfxPngAnalyzer" with all files needed to run application.
It will also compress all files to Zip archive JfxPngAnalyzer.zip in directory "...\JfxPngAnalyzer\target".
After image is created it could be copied and running on any computed supported by Java 22.

3.3) Run from command-line command
Before running application the image should be created as explained above.
Copy archive Jfxpnganalyzer.zip to desired location and unzip all files from it.
Or you can copy all files from directory "...\JfxPngAnalyzer\target\JfxPngAnalyzer" to desired location.
Change directory to "...\JfxPngAnalyzer\bin".
Run below command from command-line to run application:

java -m com.olexyarm.JfxPngAnalyzer/com.olexyarm.JfxPngAnalyzer.App %*

The application will create directory "...\JfxPngAnalyzer" in user's home directory with files "Settings.properties" in it.
The application will create directory "...\JfxPngAnalyzer\bin\logs" with log files in it.
Log files will be rolled out and Zip compressed daily. The Zip archive files will be deleted after 60 days.

3.4) Build modular jar file from command-line using Maven
Download project source code from GitHub, change directory to project root directory "...\JfxPngAnalyzer".
Run below command from command-line to create modular jar file:

mvn clean package

The directory "...\JfxPngAnalyzer\target\release" will be created.
It will contain application modular jar file and all dependencies jar files (JavaFX and logback).

3.5) Run modular jar file from command-line command
Before running application modular jar file and dependencies jar files should be created as explained above.
Copy all files from directory "...\JfxPngAnalyzer\target\release" to desired location.
Change directory to that directory.
Run below command from command-line to run application:

java --module-path "." --module com.olexyarm.jfxpnganalyzer/com.olexyarm.jfxpnganalyzer.App %*

4) Known limitations
- the application does not parse a few PNG chunks as explained above;
- the "print" menu item always print only first page of TableView because of such implementation of print functionality in JavaFX;
- there is no style added to any of GUI elements, it could be added to source code (file "JfxPngAnalyzerStyles.css").
