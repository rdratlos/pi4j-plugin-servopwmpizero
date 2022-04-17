package com.pi4j.plugin.addonboard.servopwmpi.internal;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.plugin.addonboard.servopwmpi.SERVOPWMPI;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwm;

public interface ServoPwmPiDevice  {

    /**
     * Initialise Servo PWM Pi board
     * <p>
     *  Please note that the Servo PWM Pi board does not keep Mode 1 and Mode 2
     *  settings during power-off. The registers are always reset to their factory
     *  defaults during power-up. Therefore, this method will also reset the mode
     *  registers to the recommended Servo PWM Pi board defaults
     *  ({@link SERVOPWMPI#MODE_1_DEFAULT}, {@link SERVOPWMPI#MODE_2_DEFAULT}).
     *
     * @param context PI4J Context
     * @throws InitializeException if initialisation fails
     */
    void initialize(Context context) throws InitializeException;

    /**
     * Initialise PWM pin (LEDn) on Servo PWM Pi board
     *
     * @param io PWM pin to initialise
     * @throws InitializeException if initialisation fails
     */
    void initialize(ServoPwmPiPwm io) throws InitializeException;

    /**
     * Turn the PWM signal [ON] using a specified duty-cycle (%)
     * at the pre-configured frequency (Hz).
     *
     * @param io PWM pin to turn on
     * @param dutyCycle  The duty-cycle value is a decimal value that represents the
     *                   percentage of the ON vs OFF time of the PWM signal for each
     *                   period.  A value of 50 represents a duty-cycle where half of
     *                   the time period the signal is LOW and the other half is HIGH.
     *                   The duty-cycle range is valid from 0 to 100 including factional
     *                   values. (Values above 50% mean the signal will remain HIGH more
     *                   time than LOW.)
     * @throws IOException if fails to communicate with the PWM pin
     */
    void on(ServoPwmPiPwm io, float dutyCycle) throws IOException;

    /**
     * Turn the PWM signal [OFF] by applying a zero frequency and zero duty-cycle to the PWM pin.
     *
     * @param io PWM pin to turn off
     * @throws IOException if fails to communicate with the PWM pin
     */
    void off(ServoPwmPiPwm io) throws IOException;

    /**
     *  Set the frequency value in Hertz (number of cycles per second)
     *  that the PWM signal generator should use when the PWM signal is turned 'ON'.
     *
     *  Note: This method will immediately update the live PWM signal of all PWM oins
     *  of the Servo PWM Pi board. PWM frequency change of individual PWM pins'
     *  is not supported by Servo PWM Pi boards.
     *
     * @param frequency the number of cycles per second (Hertz)
     * @throws IOException if fails to communicate with the Servo PWM Pi board
     */
    void setFrequency(int frequency) throws IOException, IllegalArgumentException;

    /**
     *  Get the configured frequency value in Hertz (number of cycles per second)
     *  that the PWM signal generator should attempt to output when the PWM signal
     *  is turned 'ON'.
     *
     *  Please note that the PWM signal generator is limited to specific frequency
     *  bands and does not generate all possible explicit frequency values.
     *  After enabling the PWM signal using the 'on(..) method, you can check the
     *  'Pwm::getActualFrequency()' method to determine what frequency the PWM
     *  generator actually applies.
     *
     * @return the configured frequency (Hz) that is used when turning the
     *         PWM signal to the 'ON' state.
     * @throws IOException if fails to communicate with the PWM pin
     */
    int getFrequency() throws IOException;

    /**
     *  Get the actual frequency value in Hertz (number of cycles per second)
     *  applied by the PWM signal generator after the PWM signal is turned 'ON'.
     *
     *  Please note that the PWM signal generator is limited to specific frequency
     *  bands and does not generate all possible explicit frequency values.
     *  After enabling the PWM signal using the 'on(..) method, you can call this
     *  method to determine what frequency the PWM generator actually applies.
     *
     * @return the actual frequency (Hz) applied by the PWM generator when the
     *         PWM signal is set to the 'ON' state.
     * @throws IOException if fails to communicate with the PWM pin
     */
    int getActualFrequency() throws IOException;

    /**
     * Switch Servo PWM Pi oscillator off (SLEEP mode)
     */
    void sleep() throws IOException;

    /**
     * Switch Servo PWM Pi oscillator on (normal mode)
     */
    void wake() throws IOException;

    /**
     * Check if Servo PWM Pi oscillator is on (normal mode) or off (SLEEP mode)
     * @return true if Servo PWM Pi device is in sleep mode
     * @throws IOException
     */
    boolean isSleeping() throws IOException;

    /**
     * Set Servo PWM Pi Mode 1 and Mode 2 registers to
     *  {@link com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#MODE_1_DEFAULT}/
     *  {@link com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#MODE_2_DEFAULT}
     * settings
     * <p>
     * @see <a href="https://github.com/abelectronicsuk/ABElectronics_Python_Libraries/blob/master/ServoPi/ServoPi.py">AB Electronics UK Servo Pi Python Library</a>
     *
     *  Please note that the default mode register settings supplier recommended by are different from
     *  the <a href="https://www.abelectronics.co.uk/docs/pdf/pca9685.pdf">PCA9685 power-up (factory) settings</a>.
     *
     * @throws IOException
     */
    void setModeRegisterDefaults() throws IOException;

    /**
     * Set Servo PWM Pi output polarity mode (INVRT)
     * <p>
     * refer to {@link {@value com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#DATASHEET}
     * 
     * @return a {@link com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#OutputPolarity} object
     * @throws IOException
     */
    SERVOPWMPI.OutputPolarity getOutputPolarity() throws IOException;

    /**
     * Set Servo PWM Pi output polarity (INVRT) mode
     * <p>
     * refer to {@link {@value com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#DATASHEET}
     * 
     * @param mode Servo PWM Pi output mode (normal, inverted)
     * @throws IOException
     */
    void setOutputPolarity(SERVOPWMPI.OutputPolarity mode) throws IOException;

    /**
     * Set Servo PWM Pi output driver (OUTDRV) type
     * <p>
     * refer to {@link {@value com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#DATASHEET}
     * 
     * @return a {@link com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#OutputDriver} object
     * @throws IOException
     */
    SERVOPWMPI.OutputDriver getOutputDriverType() throws IOException;

    /**
     * Set Servo PWM Pi output (OUTDRV) type
     * <p>
     * refer to {@link {@value com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#DATASHEET}
     * 
     * @param type Servo PWM Pi output driver type (open drain, totem pole)
     * @throws IOException
     */
    void setOutputDriverType(SERVOPWMPI.OutputDriver type) throws IOException;

    /**
     * Get Servo PWM Pi <span style="text-decoration:overline;">OE</span> input (OUTNE) mode
     * OUTNE defines the output mode of all PWM pins on Servo PWM Pi when
     * <code><span style="text-decoration:overline;">OE</span> = 1</code>, i. e.
     * Servo PWM Pi output enable pin <span style="text-decoration:overline;">OE</span>
     * is not connected or set to HIGH.
     * <p>
     * refer to {@link {@value com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#DATASHEET}
     * 
     *  Please note that for <code>OUTNE = 01</code> the PWM pin output mode applied for
     *  <code><span style="text-decoration:overline;">OE</span> = 1</code> depends on the
     *  configured {@link #setOutputDriverType output driver type}.
     *
     * @return a {@link com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#OEMode} object
     * @throws IOException
     */
    SERVOPWMPI.OEMode getOutNEMode() throws IOException;

    /**
     * Get Servo PWM Pi <span style="text-decoration:overline;">OE</span> input (OUTNE) mode
     * <p>
     * refer to {@link {@value com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#DATASHEET}
     * 
     * @return a bit string object
     * @throws IOException
     */
    String getOutNEModeBitstring() throws IOException;

    /**
     * Set Servo PWM Pi <span style="text-decoration:overline;">OE</span> input (OUTNE) mode
     * <p>
     * refer to {@link {@value com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#DATASHEET}
     * 
     * @param mode Servo PWM Pi OE input mode (low, high, floating)
     * @throws IOException
     */
    void setOutNEMode(SERVOPWMPI.OEMode mode) throws IOException;

    /**
     * Set Servo PWM Pi outputs change (OCH) mode
     * <p>
     * refer to {@link {@value com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#DATASHEET}
     * 
     * @return a {@link com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#OutputsChangeMode} object
     * @throws IOException
     */
    SERVOPWMPI.OutputsChangeMode getOutputsChangeMode() throws IOException;

    /**
     * Set Servo PWM Pi outputs change (OCH) mode
     * <p>
     * refer to {@link {@value com.pi4j.plugin.addonboard.servopwmpizero.SERVOPWMPIZERO#DATASHEET}
     * 
     * @param mode Servo PWM Pi outputs change (OCH) mode (STOP command, ACK)
     * @throws IOException
     */
    void setOutputsChangeMode(SERVOPWMPI.OutputsChangeMode mode) throws IOException;

    /**
     * Perform any shutdown steps required for the I/O pin instance
     * @param io
     * @param context
     * @throws ShutdownException
     */
    void shutdown(ServoPwmPiPwm io, Context context) throws ShutdownException;

    /**
     * Perform any shutdown steps required for the Servo PWM Pi board
     * @param context
     * @throws ShutdownException
     */
    void shutdown(Context context) throws ShutdownException;
}
