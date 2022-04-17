 Pi4J :: Java Library for Servo PWM Pi Controller
==========================================================================

[![License](https://img.shields.io/github/license/pi4j/pi4j-v2)](http://www.apache.org/licenses/LICENSE-2.0)
[![Site](https://img.shields.io/badge/Website-abelectronics.co.uk-green)](https://www.abelectronics.co.uk/p/72/servo-pwm-pi)

<a href="https://foojay.io/today/works-with-openjdk"><img align="left" src="https://github.com/foojayio/badges/raw/main/works_with_openjdk/Works-with-OpenJDK.png" width="100"></a>
<br><br>

---

Java Library to use with Servo PWM Pi Raspberry Pi expansion board from https://www.abelectronics.co.uk.

The library has been coded in Java using [pi4j version 2](https://github.com/rdratlos/pi4j-v2) that extends and fixes [pi4j](http://pi4j.com/).
* Raspberry Pi compatibility: 3, 4, zero, zero W, zero 2W
* [Raspberry Pi OS](https://www.raspberrypi.com/software/operating-systems/): April 4th 2022, 32-bit, 64-bit


### Downloading and Installing the Library

To download your Servo PWM Pi library type in terminal: 

```
git clone https://github.com/rdratlos/pi4j-plugin-servopwmpi.git
```

Before installing the library download and install the required [Pi4J V2](http://pi4j.com/) codebase. Pi4J V2 can be built using [Apache Maven 3.6.x](https://maven.apache.org/). and [Java JDK 11](https://openjdk.java.net/). Enter the following commands to build the Pi4J V2 JARs:

```
git clone https://github.com/rdratlos/pi4j-v2.git
cd pi4j-v2
mvn clean install
```

After Pi4J V2 has been successfully installed enter the following commands to build and install your Servo PWM Pi library:

```
cd ../pi4j-plugin-servopwmpi
mvn clean install
```
