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

### Servo PWM Pi Output Enable/disable (OE) Control

The active LOW Output Enable input pads (OE) on the Servo PWM Pi board allows asynchronous control of the PWM outputs and can be used to set all the outputs to a defined I2C-bus programmable logic state. The OE can also be used to externally ‘pulse width modulate’ the outputs, which is useful when multiple devices need to be dimmed or blinked together using software control. By bridging the solder pads on the Servo PWM Pi board marked OE the OE control can be connected to pin 7 (GPIO 4) on the Raspberry Pi GPIO.

Pi4J plugin pi4j-plugin-servopwmpi assumes the control over Raspberry Pi GPIO 4 for OE control of the Servo PWM Pi board. for this purpose application must inform the plugin, which GPIO number to use for the Raspberry Pi GPIO 4 pin. This is done using Pi4J Runtime Properties. There are two recommended ways for enabling Raspberry Pi GPIO OE control and configuration of the Pi GPIO number:

1. Java System Property pi4j.linux.servopwmpizero.gpio.oe.number  
  Set this property to the Raspberry Pi GPIO number to enable OE control
2. Environment variable PI4J_LINUX_SERVOPWMPIZERO_GPIO_OE_NUMBER  
  Set the environment variable to the Raspberry Pi GPIO number to enable OE control

The GPIO number to be configured depends on the Raspberry Pi OS:

* Raspberry Pi OS Bullseye  
  Use GPIO number 4
* Raspberry Pi OS Bookworm  
  The GPIO number for Raspberry Pi GPIO 4 depends on the GPIO chip that control the pin. To retrieve the correct number open a Bash terminal and enter the following command:  
  
```
  $ cat /sys/kernel/debug/gpio
  gpiochip0: GPIOs 512-565, parent: platform/20200000.gpio, pinctrl-bcm2835:
  gpio-512 (ID_SDA              )
  gpio-513 (ID_SCL              )
  gpio-514 (GPIO2               )
  gpio-515 (GPIO3               )
  gpio-516 (GPIO4               )
  gpio-517 (GPIO5               )
```


> Look for the line with GPIO name GPIO4 and read the GPIO number entry, which defines the GPIO number to be used, e. g. gpio-516 -> 516.