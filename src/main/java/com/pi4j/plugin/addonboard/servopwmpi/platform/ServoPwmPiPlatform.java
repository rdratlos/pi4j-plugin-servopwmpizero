package com.pi4j.plugin.addonboard.servopwmpi.platform;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: ADDONBOARD   :: Servo PWM PI
 * FILENAME      :  ServoPwmPiPlatform.java
 *
 * This file is an extension for the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.addonboard.platform.AddOnBoardPlatform;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.i2c.I2C;
import com.pi4j.platform.Platform;
import com.pi4j.provider.exception.ProviderException;
import com.pi4j.provider.exception.ProviderNotFoundException;

import com.pi4j.plugin.addonboard.servopwmpi.SERVOPWMPI;
import com.pi4j.plugin.addonboard.servopwmpi.ServoPwmPi;
import com.pi4j.plugin.addonboard.servopwmpi.internal.ServoPwmPiDevice;
import com.pi4j.plugin.addonboard.servopwmpi.internal.ServoPwmPiDeviceImpl;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiProvider;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwm;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwmConfig;
import com.pi4j.plugin.linuxfs.provider.gpio.digital.LinuxFsDigitalOutputProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>ServoPwmPiPlatform class.</p>
 *
 * @author Thomas Reim
 * @version $Id: $Id
 */
public class ServoPwmPiPlatform extends AddOnBoardPlatform implements Platform {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final int SERVO_PWM_PI_DEFAULT_I2C_ADDRESS = SERVOPWMPI.DEFAULT_ADDRESS;
    private static final int SERVO_PWM_PI_I2C_BUS = 1;
    private int i2cAddress = -1;

    protected ServoPwmPiDevice device = null;
    // local/internal GPIO reference for output enable/disable of the hardware chip
    protected static DigitalOutput invOE = null;
    
    /**
     * <p>Constructor for MockPlatform.</p>
     */
    public ServoPwmPiPlatform() {
        this(SERVO_PWM_PI_DEFAULT_I2C_ADDRESS);
    }

    public ServoPwmPiPlatform(int i2c_address) {
        super(ServoPwmPi.SERVOPWMPIZERO_PLATFORM_ID + String.format("_%d", i2c_address),
              ServoPwmPi.SERVOPWMPIZERO_PLATFORM_NAME + String.format(" (i2c: 0x%x)", i2c_address),
              ServoPwmPi.SERVOPWMPIZERO_PLATFORM_DESCRIPTION);
        this.i2cAddress = i2c_address;
    }

    /** {@inheritDoc} */
    @Override
    public int priority() {
        // the MOCK platform has a priority of -1000 to indicate that it has a very
        // low priority and should only be used in the case where other platforms
        // are not found in the classpath
        return super.priority();
    }

    /** {@inheritDoc} */
    @Override
    public boolean enabled(Context context) {
        // TODO: Here we need to check if Servo PWM Pi is actually connected via i2c
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected String[] getProviders() {
        return new String[] {
            ServoPwmPiProvider.ID};
    }

    public ServoPwmPiPwm create(ServoPwmPiPwmConfig config) {
        
        // validate provider setup
        if(this.device == null){
            throw new ProviderException("ServoPwmPiPlatform::initialise(...) has not been called; this platform must be initialised prior to creating I/O instances.");
        }
        
        // create new output I/O instance
        ServoPwmPiProvider provider = this.getProvider(ServoPwmPiProvider.ID);
        if(provider == null) {
            throw new ProviderNotFoundException(ServoPwmPiProvider.ID);
        }
        // Create PWM instance
        return provider.create(config);
    }

    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPlatform initialize(Context context) throws InitializeException {
        I2C i2c = null;
        
        // Prevent from double initialisation
        if (this.device != null) {
            return this;
        }
        
        if (invOE == null && context.hasProvider(IOType.DIGITAL_OUTPUT)) {
            /*
             * Servo PWM Pi Context wants OE control of the add-on board.
             * All piggy-backed Servo PWM Pi boards share one Raspberry Pi
             * GPIO pin for OE control (DOUT-4). We should only provide it once
             * to avoid error messages.
            */
            if(context.registry().exists(ServoPwmPi.SERVOPWMPIZERO_OE_CONTROL_PIN_ID)) {
                invOE = context.registry().get(ServoPwmPi.SERVOPWMPIZERO_OE_CONTROL_PIN_ID);
            }
            if (invOE == null) {
                String preferredOEProvider;
                if (context.hasProvider(LinuxFsDigitalOutputProvider.ID)) {
                    preferredOEProvider = LinuxFsDigitalOutputProvider.ID;
                } else {
                    preferredOEProvider = context.provider(IOType.DIGITAL_OUTPUT).id();
                }
                var oeConfig = DigitalOutput.newConfigBuilder(context)
                        .id(ServoPwmPi.SERVOPWMPIZERO_OE_CONTROL_PIN_ID)
                        .name("Servo PWM Pi O\u0305E\u0305 control")
                        .address(SERVOPWMPI.PI_GPIO_OE)
                        .shutdown(DigitalState.HIGH)
                        .initial(DigitalState.HIGH)
                        .provider(preferredOEProvider);
                invOE = context.create(oeConfig);
                invOE.addListener((DigitalStateChangeEvent e) -> {
                    logger.info(String.format("Servo PWM Pi O\u0305E\u0305 control: outputs %s", (e.state() == DigitalState.HIGH) ? "disabled" : "enabled"));
                });
                logger.info("adding digital output to registry [id={}; name={}; description={}; class={}]",
                            invOE.id(), invOE.name(), invOE.description(), invOE.getClass().getName()); 
            }
        }

        this.context = context;
        if (context.hasProvider(IOType.I2C)) {
            var i2cConfig = I2C.newConfigBuilder(context)
                    .bus(SERVO_PWM_PI_I2C_BUS)
                    .device(this.i2cAddress)
                    .id("servopwmpizero-i2c" + String.format("-0x%x", this.i2cAddress))
                    .name("Servo PWM Pi I2C device")
                    .build();
            i2c = context.i2c().create(i2cConfig);
        }
        if (i2c != null) {
            this.device = new ServoPwmPiDeviceImpl(i2c);
            if (context.hasProvider(ServoPwmPiProvider.ID)) {
                var pwmProvider = (ServoPwmPiProvider) context.providers().get(ServoPwmPiProvider.ID);
                if (pwmProvider != null) {
                    pwmProvider.add(this);
                }
                this.addProvider(context, ServoPwmPiProvider.ID);
            }
            try {
                this.device.initialize(context);
            } catch (InitializeException e) {
                this.device.shutdown(context);
                this.device = null;
                throw new InitializeException("");
            }
            logger.info(String.format("output driver type (OUTDRV): %s (%d)", this.device.getOutputDriverType().toString(), this.device.getOutputDriverType().getBit()));
            logger.info(String.format("output polarity mode (INVRT): %s (%d)", this.device.getOutputPolarity().toString(), this.device.getOutputPolarity().getBit()));
            logger.info(String.format("outputs change mode (OCH): %s (%d)", this.device.getOutputsChangeMode().toString(), this.device.getOutputsChangeMode().getBit()));
            logger.info(String.format("O\u0305E\u0305 pin not enabled mode (OUTNE): %s (%s)", this.device.getOutNEMode().toString(), this.device.getOutNEModeBitstring()));
            logger.info(String.format("PWM frequency: %d Hz", this.device.getFrequency()));
            if (this.device.isSleeping()) {
                logger.info("oscillator off (SLEEP mode)");
            }
        } else {
            logger.error("i2c provider not in context");
            throw new InitializeException("");
        }

        return this;
    }
    
    public Context getContext() {
        return this.context;
    }

    public ServoPwmPiDevice getPwmDevice() {
        return this.device;
    }

    public ServoPwmPiDevice pwmDevice() {
        return this.getPwmDevice();
    }

    public void setPwmFrequency(int frequency) {
        this.device.setFrequency(frequency);
        logger.info(String.format("[%s]: changed PWM frequency to: %d Hz", this.id, this.device.getFrequency()));
    }

    public int getActualPwmFrequency() {
        return this.device.getActualFrequency();
    }

    public int getPwmFrequency() {
        return this.device.getFrequency();
    }

    public int pwmFrequency() {
        return this.getPwmFrequency();
    }
    
    public boolean isSleeping() {
        return this.device.isSleeping();
    }

    /**
     *  Switch Servo PWM Pi board to sleep mode.
     *
     *  No PWM control is possible. LED outputs cannot be turned on, off or dimmed
     */
    public void sleep() {
        this.device.sleep();
        logger.info(String.format("[%s]: oscillator switched off (SLEEP)", this.id));
    }

    /**
     *  Switch Servo PWM Pi board into normal mode.
     *
     *  PWM control is enabled.
     */
    public void wake() {
        this.device.wake();
        logger.info(String.format("[%s]: oscillator switched on", this.id));
    }

    public boolean outputEnabled() {
        if (invOE != null) {
            return invOE.isLow();
        }
        return true;
    }
    
    public static void outputEnable() {
        if (invOE != null) {
            invOE.low();
        }
    }
    
    public static void outputDisable() {
        if (invOE != null) {
            invOE.high();
        }
    }
    
    public void setOutputPolarity(SERVOPWMPI.OutputPolarity mode) {
        this.device.setOutputPolarity(mode);
        logger.info(String.format("[%s]: changed polarity mode (INVRT) mode to: %s", this.id, mode.toString()));
    }

    public void setOutputDriverType(SERVOPWMPI.OutputDriver type) {
        this.device.setOutputDriverType(type);
        logger.info(String.format("[%s]: changed output driver type (OUTDRV) mode to: %s", this.id, type.toString()));
    }

    public void setOutNEMode(SERVOPWMPI.OEMode mode) {
        this.device.setOutNEMode(mode);
        logger.info(String.format("[%s]: changed O\u0305E\u0305 pin not enabled mode (OUTNE) to: %s", this.id, mode.toString()));
    }

    public void setOutputsChangeMode(SERVOPWMPI.OutputsChangeMode mode) {
        this.device.setOutputsChangeMode(mode);
        logger.info(String.format("[%s]: changed outputs change mode (OCH) mode to: %s", this.id, mode.toString()));
    }

    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPlatform shutdown(Context context) throws ShutdownException {
        if (context.hasProvider(ServoPwmPiProvider.ID)) {
            var pwmProvider = (ServoPwmPiProvider) context.providers().get(ServoPwmPiProvider.ID);
            if (pwmProvider != null) {
                pwmProvider.remove(this);
            }
        }
        this.device.shutdown(context);
        this.device = null;
        if (context.platforms().getAll(this.getClass()).entrySet().size() == 1) {
            /**
             * No more Servo PWM Pi platforms left
             * Shutdown and remove output enable control GPIO pin if still in Pi4J registry
             */
            if(context.registry().exists(ServoPwmPi.SERVOPWMPIZERO_OE_CONTROL_PIN_ID)) {
                invOE.shutdown(context);
            }
            invOE = null;
        }
        this.i2cAddress = -1;
        
        return this;
    }

}
