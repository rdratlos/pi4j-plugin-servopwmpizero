package com.pi4j.plugin.addonboard.servopwmpi;

import static java.util.Map.entry;

// MARKER INTERFACE

import java.util.EnumSet;
import java.util.Map;


/**
 * Servo PWM Pi Constants
 */
public interface SERVOPWMPI {

    /**
     * Servo PWM Pi data sheet link
     */
    static final String DATASHEET = "https://www.abelectronics.co.uk/docs/pdf/pca9685.pdf";

    // supported I2C addresses based on hardware configured pins [A0], [A1], [A2], [A3], [A4].and [A5].
    static final int ADDRESS_000000 = 0x40;
    static final int ADDRESS_000001 = 0x41;
    static final int ADDRESS_000010 = 0x42;
    static final int ADDRESS_000011 = 0x43;
    static final int ADDRESS_000100 = 0x44;
    static final int ADDRESS_000101 = 0x45;
    static final int ADDRESS_000110 = 0x46;
    static final int ADDRESS_000111 = 0x47;
    static final int ADDRESS_001000 = 0x48;
    static final int ADDRESS_001001 = 0x49;
    static final int ADDRESS_001010 = 0x4A;
    static final int ADDRESS_001011 = 0x4B;
    static final int ADDRESS_001100 = 0x4C;
    static final int ADDRESS_001101 = 0x4D;
    static final int ADDRESS_001110 = 0x4E;
    static final int ADDRESS_001111 = 0x4F;
    static final int ADDRESS_010000 = 0x50;
    static final int ADDRESS_010001 = 0x51;
    static final int ADDRESS_010010 = 0x52;
    static final int ADDRESS_010011 = 0x53;
    static final int ADDRESS_010100 = 0x54;
    static final int ADDRESS_010101 = 0x55;
    static final int ADDRESS_010110 = 0x56;
    static final int ADDRESS_010111 = 0x57;
    static final int ADDRESS_011000 = 0x58;
    static final int ADDRESS_011001 = 0x59;
    static final int ADDRESS_011010 = 0x5A;
    static final int ADDRESS_011011 = 0x5B;
    static final int ADDRESS_011100 = 0x5C;
    static final int ADDRESS_011101 = 0x5D;
    static final int ADDRESS_011110 = 0x5E;
    static final int ADDRESS_011111 = 0x5F;
    static final int ADDRESS_100000 = 0x60;
    static final int ADDRESS_100001 = 0x61;
    static final int ADDRESS_100010 = 0x62;
    static final int ADDRESS_100011 = 0x63;
    static final int ADDRESS_100100 = 0x64;
    static final int ADDRESS_100101 = 0x65;
    static final int ADDRESS_100110 = 0x66;
    static final int ADDRESS_100111 = 0x67;
    static final int ADDRESS_101000 = 0x68;
    static final int ADDRESS_101001 = 0x69;
    static final int ADDRESS_101010 = 0x6A;
    static final int ADDRESS_101011 = 0x6B;
    static final int ADDRESS_101100 = 0x6C;
    static final int ADDRESS_101101 = 0x6D;
    static final int ADDRESS_101110 = 0x6E;
    static final int ADDRESS_101111 = 0x6F;
    static final int ADDRESS_110000 = 0x70;
    static final int ADDRESS_110001 = 0x71;
    static final int ADDRESS_110010 = 0x72;
    static final int ADDRESS_110011 = 0x73;
    static final int ADDRESS_110100 = 0x74;
    static final int ADDRESS_110101 = 0x75;
    static final int ADDRESS_110110 = 0x76;
    static final int ADDRESS_110111 = 0x77;
    static final int ADDRESS_111000 = 0x78;
    static final int ADDRESS_111001 = 0x79;
    static final int ADDRESS_111010 = 0x7A;
    static final int ADDRESS_111011 = 0x7B;
    static final int ADDRESS_111100 = 0x7C;
    static final int ADDRESS_111101 = 0x7D;
    static final int ADDRESS_111110 = 0x7E;
    static final int ADDRESS_111111 = 0x7F;

    // connected Raspberry Pi GPIO pin number for OE (output enable/disable) control
    static final int PI_GPIO_OE = 4; // PIN 7 = BCM 4

    // default settings
    static final int DEFAULT_ADDRESS = ADDRESS_000000;
    static final int DEFAULT_POLLING_TIME = 50;

    // PWM pins
    static final int LED1  = 0;
    static final int LED2  = 1;
    static final int LED3  = 2;
    static final int LED4  = 3;
    static final int LED5  = 4;
    static final int LED6  = 5;
    static final int LED7  = 6;
    static final int LED8  = 7;
    static final int LED9  = 8;
    static final int LED10  = 9;
    static final int LED11 = 10;
    static final int LED12 = 11;
    static final int LED13 = 12;
    static final int LED14 = 13;
    static final int LED15 = 14;
    static final int LED16 = 15;
    static final Map<String,Integer> PWM_PINS = Map.ofEntries(
        entry("LED1", LED1),
        entry("LED2", LED2),
        entry("LED3", LED3),
        entry("LED4", LED4),
        entry("LED5", LED5),
        entry("LED6", LED6),
        entry("LED7", LED7),
        entry("LED8", LED8),
        entry("LED9", LED9),
        entry("LED10", LED10),
        entry("LED11", LED11),
        entry("LED12", LED12),
        entry("LED13", LED13),
        entry("LED14", LED14),
        entry("LED15", LED15),
        entry("LED16", LED16)
    );

    // communication registers
    static final byte REGISTER_MODE_1 = 0x00;
    static final byte REGISTER_MODE_2 = 0x01;

    /**
     * Mode 1 register defaults
     *
     * @see <a href="https://github.com/abelectronicsuk/ABElectronics_Python_Libraries/blob/master/ServoPi/ServoPi.py">AB Electronics UK Servo Pi Python Library</a>
     * @author Thomas Reim
     * @version $Id: $Id
     */
    static final byte MODE_1_DEFAULT = 0x00;

    /**
     * Mode 2 register defaults
     *
     * @see <a href="https://github.com/abelectronicsuk/ABElectronics_Python_Libraries/blob/master/ServoPi/ServoPi.py">AB Electronics UK Servo Pi Python Library</a>
     * @author Thomas Reim
     * @version $Id: $Id
     */
    static final byte MODE_2_DEFAULT = 0x0C;

    // Mode 1 register constants
    static final byte RESTART_DISABLE_MASK = (byte) 0x7F;   // for use with bitwise AND
    static final byte RESTART_ENABLE_MASK = (byte) 0x80;    // for use with bitwise OR
    static final byte SLEEP_MASK = (byte) 0x10;             // Mode 1 register bit 4

    // Mode 2 register constants
    static final byte INVRT_MASK = (byte) 0x10;             // Mode 2 register bit 4
    static final byte OCH_MASK = (byte) 0x08;               // Mode 2 register bit 3
    static final byte OUTDRV_MASK = (byte) 0x04;            // Mode 2 register bit 2
    static final byte OUTNE_MASK = (byte) 0x03;             // Mode 2 register bit 0 and bit 1

    // PWM frequency (pre-scale) register
    static final int REGISTER_PRE_SCALE = 0xFE;

    static final double FREQ_OSC_CLOCK = 25000000.0;
    static final byte DEFAULT_PRE_SCALE = 0x1E;
    static final int DEFAULT_PWM_FREQUENCY = 200;   // PRE_SCALE = 0x1E

    static final int LED0_ON_L  = 0x06;
    static final int LED0_ON_H  = 0x07;
    static final int LED0_OFF_L = 0x08;
    static final int LED0_OFF_H = 0x09;

    /**
     * Output Driver (OUTDRV) Enumerations
     *
     * @author Thomas Reim
     * @version $Id: $Id
     */
    public enum OutputDriver {
        OPEN_DRAIN(0, "open_drain"),
        TOTEM_POLE(OUTDRV_MASK, "totem_pole");

        private final int value;
        private final String name;

        /**
         * Output Driver (OUTDRV) Enumerations
         */
        private OutputDriver(int value, String name) {
            this.value = value;
            this.name = name;
        }

        /**
         * <p>Getter for the field <code>value</code>.</p>
         *
         * @return a int.
         */
        public int getValue() {
            return value;
        }

        /**
         * <p>Getter for the field <code>name</code>.</p>
         *
         * @return a {@link java.lang.String} object.
         */
        public String getName() {
            return name;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return name.toUpperCase();
        }

         /**
         * <p>Getter for the field <code>bitvalue</code>.</p>
         *
         * @return a int.
         */
        public int getBit() {
            return (value > 0 ? 1 : 0);
        }

        /**treemap
         * <p>all.</p>
         *
         * @return a {@link java.util.EnumSet} object.
         */
        public static EnumSet<OutputDriver> all() {
            return EnumSet.allOf(OutputDriver.class);
        }

        /**
         * <p>parse.</p>
         *
         * @param type a {@link java.lang.String} object.
         * @return a an {@link #OutputDriver} object.
         */
        public static OutputDriver parse(String type) {
            if(type.equalsIgnoreCase("0")) return OutputDriver.OPEN_DRAIN;
            if(type.equalsIgnoreCase("1")) return OutputDriver.TOTEM_POLE;
            if(type.equalsIgnoreCase("0x00")) return OutputDriver.OPEN_DRAIN;
            if(type.equalsIgnoreCase(String.format("0x%02x", OUTDRV_MASK))) return OutputDriver.TOTEM_POLE;
            if(type.toLowerCase().startsWith("o")) return OutputDriver.OPEN_DRAIN;
            if(type.toLowerCase().startsWith("t")) return OutputDriver.TOTEM_POLE;
            return OutputDriver.TOTEM_POLE; // default
        }
    }

    /**
     * Output Polarity (INVRT) Enumerations
     *
     * @author Thomas Reim
     * @version $Id: $Id
     */
    public enum OutputPolarity {
        NORMAL(0, "normal"),
        INVERTED(INVRT_MASK, "inverted");

        private final int value;
        private final String name;

        /**
         * Output Mode (INVRT) Enumerations
         */
        private OutputPolarity(int value, String name) {
            this.value = value;
            this.name = name;
        }

        /**
         * <p>Getter for the field <code>value</code>.</p>
         *
         * @return a int.
         */
        public int getValue() {
            return value;
        }

         /**
         * <p>Getter for the field <code>bitvalue</code>.</p>
         *
         * @return a int.
         */
        public int getBit() {
            return (value > 0 ? 1 : 0);
        }

        /**
         * <p>Getter for the field <code>name</code>.</p>
         *
         * @return a {@link java.lang.String} object.
         */
        public String getName() {
            return name;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return name.toUpperCase();
        }

        /**
         * <p>all.</p>
         *
         * @return a {@link java.util.EnumSet} object.
         */
        public static EnumSet<OutputPolarity> all() {
            return EnumSet.allOf(OutputPolarity.class);
        }

        /**
         * <p>parse.</p>
         *
         * @param type a {@link java.lang.String} object.
         * @return an {@link #OutputPolarity} object.
         */
        public static OutputPolarity parse(String type) {
            if(type.equalsIgnoreCase("0")) return OutputPolarity.NORMAL;
            if(type.equalsIgnoreCase("1")) return OutputPolarity.INVERTED;
            if(type.equalsIgnoreCase("0x00")) return OutputPolarity.NORMAL;
            if(type.equalsIgnoreCase(String.format("0x%02x", INVRT_MASK))) return OutputPolarity.INVERTED;
            if(type.toLowerCase().startsWith("n")) return OutputPolarity.NORMAL;
            if(type.toLowerCase().startsWith("i")) return OutputPolarity.INVERTED;
            return OutputPolarity.NORMAL; // default
        }
    }

    /**
     * <span style="text-decoration:overline;">OE</span> Mode (OUTNE) Enumerations
     *
     * @author Thomas Reim
     * @version $Id: $Id
     */
    public enum OEMode {
        LOW(0, "low"),
        HIGH(1, "high"),
        HIGH_IMPEDANCE(2, "floating");

        private final int value;
        private final String name;

        /**
         * <span style="text-decoration:overline;">OE</span> Mode (OUTNE) Enumerations
         */
        private OEMode(int value, String name) {
            this.value = value;
            this.name = name;
        }

        /**
         * <p>Getter for the field <code>value</code>.</p>
         *
         * @return a int.
         */
        public int getValue() {
            return value;
        }

        /**
         * <p>Getter for the field <code>name</code>.</p>
         *
         * @return a {@link java.lang.String} object.
         */
        public String getName() {
            return name;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return name.toUpperCase();
        }

        /**
         * <p>all.</p>
         *
         * @return a {@link java.util.EnumSet} object.
         */
        public static EnumSet<OEMode> all() {
            return EnumSet.allOf(OEMode.class);
        }

        /**
         * <p>parse.</p>
         *
         * @param type a {@link java.lang.String} object.
         * @return an {@link #OEMode} object.
         */
        public static OEMode parse(String type) {
            if(type.equalsIgnoreCase("0")) return OEMode.LOW;
            if(type.equalsIgnoreCase("1")) return OEMode.HIGH;
            if(type.equalsIgnoreCase("2")) return OEMode.HIGH_IMPEDANCE;
            if(type.equalsIgnoreCase("3")) return OEMode.HIGH_IMPEDANCE;
            if(type.equalsIgnoreCase("0x00")) return OEMode.LOW;
            if(type.equalsIgnoreCase("0x01")) return OEMode.HIGH;
            if(type.equalsIgnoreCase("0x02")) return OEMode.HIGH_IMPEDANCE;
            if(type.equalsIgnoreCase("0x03")) return OEMode.HIGH_IMPEDANCE;
            if(type.toLowerCase().startsWith("l")) return OEMode.LOW;
            if(type.toLowerCase().startsWith("h")) return OEMode.HIGH;
            if(type.toLowerCase().startsWith("f")) return OEMode.HIGH_IMPEDANCE;
            return OEMode.LOW; // default
        }
    }

    /**
     * Outputs Change (OCH) Enumerations
     *
     * @author Thomas Reim
     * @version $Id: $Id
     */
    public enum OutputsChangeMode {
        STOP(0, "STOP"),
        ACK(OCH_MASK, "ACK");

        private final int value;
        private final String name;

        /**
         * Outputs Change (OCH) Enumerations
         */
        private OutputsChangeMode(int value, String name) {
            this.value = value;
            this.name = name;
        }

        /**
         * <p>Getter for the field <code>value</code>.</p>
         *
         * @return a int.
         */
        public int getValue() {
            return value;
        }

         /**
         * <p>Getter for the field <code>bitvalue</code>.</p>
         *
         * @return a int.
         */
        public int getBit() {
            return (value > 0 ? 1 : 0);
        }

        /**
         * <p>Getter for the field <code>name</code>.</p>
         *
         * @return a {@link java.lang.String} object.
         */
        public String getName() {
            return name;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            String format = "outputs change on %s";
            if (value == 0) {
                format += " command";
            }
            return String.format(format, name.toUpperCase());
        }

        /**
         * <p>all.</p>
         *
         * @return a {@link java.util.EnumSet} object.
         */
        public static EnumSet<OutputsChangeMode> all() {
            return EnumSet.allOf(OutputsChangeMode.class);
        }

        /**
         * <p>parse.</p>
         *
         * @param type a {@link java.lang.String} object.
         * @return an {@link #OutputsChangeMode} object.
         */
        public static OutputsChangeMode parse(String type) {
            if(type.equalsIgnoreCase("0")) return OutputsChangeMode.STOP;
            if(type.equalsIgnoreCase("1")) return OutputsChangeMode.ACK;
            if(type.equalsIgnoreCase(String.format("0x00", OCH_MASK))) return OutputsChangeMode.STOP;
            if(type.equalsIgnoreCase(String.format("0x%02x", OCH_MASK))) return OutputsChangeMode.ACK;
            if(type.toLowerCase().startsWith("s")) return OutputsChangeMode.STOP;
            if(type.toLowerCase().startsWith("a")) return OutputsChangeMode.ACK;
            return OutputsChangeMode.STOP; // default
        }
    }

}


