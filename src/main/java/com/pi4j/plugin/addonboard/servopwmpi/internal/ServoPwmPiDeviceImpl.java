package com.pi4j.plugin.addonboard.servopwmpi.internal;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.plugin.addonboard.servopwmpi.SERVOPWMPI;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServoPwmPiDeviceImpl implements SERVOPWMPI, ServoPwmPiDevice {
    private static final Logger logger = LoggerFactory.getLogger(ServoPwmPiDeviceImpl.class);

    // local/internal I2C reference for communication with hardware chip
    protected final I2C i2c;

    // PWM frequency (same for all PWM outputs)
    private int requested_pwm_frequency = DEFAULT_PWM_FREQUENCY;

    // PWM frequency (same for all PWM outputs)
    private int actual_pwm_frequency = DEFAULT_PWM_FREQUENCY;

    private Context context = null;

    /**
     * Constructor
     * @param i2c
     */
    public ServoPwmPiDeviceImpl(I2C i2c){
        // set local reference to I2C instance
        this.i2c = i2c;
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(Context context) throws InitializeException {
        byte pre_scale;

        // atomic operation to configure chip registers
        try {
            // Detect current ServoPWM Pi PWM frequency settings
            pre_scale = (byte) this.i2c.readRegister(REGISTER_PRE_SCALE);
            this.actual_pwm_frequency = SERVOPWMPI.preScaleToFrequency(pre_scale);
            if (pre_scale != DEFAULT_PRE_SCALE) {
                this.requested_pwm_frequency = this.actual_pwm_frequency;
            } else {
                this.requested_pwm_frequency = DEFAULT_PWM_FREQUENCY;
            }
            this.context = context;
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::initialize() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::initialize() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::initialize() I2C connection to Servo PWM Pi device not configured");
            }
        }

        /**
         * Servo PWM Pi board does not keep Mpde 1/2 register settings
         * during power-off. Initialize mode defaults here:
         */
        synchronized (this.i2c) {
            this.i2c.writeRegister(REGISTER_MODE_1, MODE_1_DEFAULT);
            this.i2c.writeRegister(REGISTER_MODE_2, MODE_2_DEFAULT);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(ServoPwmPiPwm io) throws InitializeException {
        int channel = io.getChannel();

        if (channel < 0 || channel > 15) {
            throw new InitializeException("initialize(): Configured PWM channel (LED" + String.format("%d", channel) + ") is out of range (LED0 - LED16)");
        }

    }

    /** {@inheritDoc} */
    @Override
    public void shutdown(ServoPwmPiPwm io, Context context) throws ShutdownException {
        // TODO :: Implement any needed GPIO OUTPUT pin shutdown logic here
    }

    /** {@inheritDoc} */
    @Override
    public void shutdown(Context context) throws ShutdownException {
        // TODO :: Implement any needed GPIO OUTPUT pin shutdown logic here
    }

    /** {@inheritDoc} */
    @Override
    public void on(ServoPwmPiPwm io, float dutyCycle) throws IOException {
        int on_value;
        int off_value;
        int on_steps;

        on_steps = Math.round(4096f / 100f * dutyCycle);
        switch(on_steps) {
            case 4096:
                on_value = 0x1000;
                off_value = 0;
                break;
            case 0:
                on_value = 0;
                off_value = 0x1000;
                break;
            default:
                /*
                 * on_value is phase-shift, i. e. switch-on delay
                 * If phase-shift has not benn defined, we use on time for
                 * phase-shift to reduce EMI
                 */
                if (io.phaseShift() < 0) {
                    on_value = on_steps - 1;
                } else {
                    on_value = Math.round(4096 / 100 * io.phaseShift()) - 1;
                }
                if (on_value + on_steps > 4096) {
                    /*
                     * LEDn_ON > LEDn_OFF:
                     * LEDn_OFF count starts in subsequent PWM frame
                     */
                    off_value = on_value + on_steps - 4096;
                } else {
                    /*
                     * LEDn_ON < LEDn_OFF:
                     * LEDn_OFF count starts in same PWM frame
                     */
                    off_value = on_value + on_steps;
                }
                break;
        }
        try {
            synchronized (this.i2c) {
                this.i2c.writeRegister(LED0_ON_L + 4 * io.channel(), (byte) (on_value & 0xFF));
                this.i2c.writeRegister(LED0_ON_H + 4 * io.channel(), (byte) (on_value >> 8));
                this.i2c.writeRegister(LED0_OFF_L + 4 * io.channel(), (byte) (off_value & 0xFF));
                this.i2c.writeRegister(LED0_OFF_H + 4 * io.channel(), (byte) (off_value >> 8));
            }
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::on() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::on() I2C connection to Servo PWM Pi device not configured");
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void off(ServoPwmPiPwm io) throws IOException {
        int on_value = 0;
        int off_value = 0x1000;

        try {
            synchronized (this.i2c) {
                this.i2c.writeRegister(LED0_ON_L + 4 * io.channel(), (byte) on_value);
                this.i2c.writeRegister(LED0_ON_H + 4 * io.channel(), (byte) on_value);
                this.i2c.writeRegister(LED0_OFF_L + 4 * io.channel(), (byte) (off_value & 0xFF));
                this.i2c.writeRegister(LED0_OFF_H + 4 * io.channel(), (byte) (off_value >> 8));
            }
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::off() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::off() I2C connection to Servo PWM Pi device not configured");
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getActualFrequency() throws IOException {
        refreshPwmFrequencyConfiguration();
        return this.actual_pwm_frequency;
    }

    /** {@inheritDoc} */
    @Override
    public int getFrequency() throws IOException {
        refreshPwmFrequencyConfiguration();
        return this.requested_pwm_frequency;
    }

    /** {@inheritDoc} */
    @Override
    public void setFrequency(int frequency) throws IOException, IllegalArgumentException {
        byte currentMode1State;
        byte restartMode;

        if (frequency < 40 || frequency > 1000) {
            throw new IllegalArgumentException("setFrequency(): Requested frequency (" + String.format("%d", frequency) + " Hz) out of range (40 Hz - 1 kHz)");
        }
        if (frequency != this.requested_pwm_frequency) {
            try {
                currentMode1State = (byte) this.i2c.readRegister(REGISTER_MODE_1);
            } catch (Pi4JException e) {
                if (this.i2c != null) {
                    throw new InitializeException("ServoPwmPiDeviceImpl::setFrequency() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
                } else {
                    throw new InitializeException("ServoPwmPiDeviceImpl::setFrequency() I2C connection to Servo PWM Pi device not configured");
                }
            } catch (IllegalStateException e) {
                if (this.i2c != null) {
                    logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                    currentMode1State = MODE_1_DEFAULT;
                } else {
                    throw new InitializeException("ServoPwmPiDeviceImpl::setFrequency() I2C connection to Servo PWM Pi device not configured");
                }
            }
            /*
             * Switch oscillator off (SLEEP mode)
             */
            this.sleep();
            /*
             * Set new PWM frequency and wake-up PWM controller
             */
            synchronized (this.i2c) {
                this.i2c.writeRegister(REGISTER_PRE_SCALE, SERVOPWMPI.frequencyToPreScale(frequency));
                this.i2c.writeRegister(REGISTER_MODE_1, currentMode1State);
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException("Wait for PWM controller reset thread interrupted.", e);
            }
            /*
             * Restart all PWM channels with new frequency:
             * PWM controller will automatically clear RESTART bit after restart
             */
            restartMode = (byte) (currentMode1State | RESTART_ENABLE_MASK);
            synchronized (this.i2c) {
                this.i2c.writeRegister(REGISTER_MODE_1, restartMode);
            }
            this.requested_pwm_frequency = frequency;
            try {
                this.actual_pwm_frequency = SERVOPWMPI.preScaleToFrequency((byte) this.i2c.readRegister(REGISTER_PRE_SCALE));
            } catch (IllegalStateException e) {
                this.actual_pwm_frequency = this.requested_pwm_frequency;
            }
        }
    }

    /**
     * Fetch current PWM frequency settings from the PWM controller
     */
    private void refreshPwmFrequencyConfiguration () throws IOException {
        byte pre_scale;

        try {
            pre_scale = (byte) this.i2c.readRegister(REGISTER_PRE_SCALE);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::refreshPwmFrequencyConfiguration() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::refreshPwmFrequencyConfiguration() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                pre_scale = DEFAULT_PRE_SCALE;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::refreshPwmFrequencyConfiguration() I2C connection to Servo PWM Pi device not configured");
            }
        }
        this.actual_pwm_frequency = SERVOPWMPI.preScaleToFrequency(pre_scale);
        if (pre_scale == DEFAULT_PRE_SCALE) {
            this.requested_pwm_frequency = DEFAULT_PWM_FREQUENCY;
        } else {
            this.requested_pwm_frequency = this.actual_pwm_frequency;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void sleep() throws IOException {
        byte currentMode1State;
        byte sleepMode;

        if (!this.isSleeping()) {
            try {
                currentMode1State = (byte) this.i2c.readRegister(REGISTER_MODE_1);
            } catch (IllegalStateException e) {
                if (this.i2c != null) {
                    logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                    currentMode1State = MODE_1_DEFAULT;
                } else {
                    throw new InitializeException("ServoPwmPiDeviceImpl::sleep() I2C connection to Servo PWM Pi device not configured");
                }
            }
            sleepMode = (byte) (currentMode1State | SLEEP_MASK);
            synchronized (this.i2c) {
                this.i2c.writeRegister(REGISTER_MODE_1, sleepMode);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void wake() throws IOException {
        byte currentMode1State;
        byte wakeUpMode;

        if (this.isSleeping()) {
            try {
                currentMode1State = (byte) this.i2c.readRegister(REGISTER_MODE_1);
            } catch (IllegalStateException e) {
                if (this.i2c != null) {
                    logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                    currentMode1State = MODE_1_DEFAULT;
                } else {
                    throw new InitializeException("ServoPwmPiDeviceImpl::wake() I2C connection to Servo PWM Pi device not configured");
                }
            }
            wakeUpMode = (byte) (currentMode1State & ~SLEEP_MASK);
            synchronized (this.i2c) {
                this.i2c.writeRegister(REGISTER_MODE_1, wakeUpMode);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSleeping() throws IOException {
        byte currentMode1State;
        byte sleepStatus;

        try {
            currentMode1State = (byte) this.i2c.readRegister(REGISTER_MODE_1);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::isSleeping() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::isSleeping() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                currentMode1State = MODE_1_DEFAULT;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::isSleeping() I2C connection to Servo PWM Pi device not configured");
            }
        }
        sleepStatus = (byte) (currentMode1State & SLEEP_MASK);

        return (sleepStatus != 0);
    }

    /** {@inheritDoc} */
    @Override
    public void setModeRegisterDefaults() throws IOException {
        synchronized (this.i2c) {
            this.i2c.writeRegister(REGISTER_MODE_1, MODE_1_DEFAULT);
            this.i2c.writeRegister(REGISTER_MODE_2, MODE_2_DEFAULT);
        }
    }

    /** {@inheritDoc} */
    @Override
    public OutputPolarity getOutputPolarity() throws IOException {
        byte currentMode2State;
        byte outputMode;

        try {
            currentMode2State = (byte) this.i2c.readRegister(REGISTER_MODE_2);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutputPolarity() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutputPolarity() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                currentMode2State = MODE_2_DEFAULT;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutputPolarity() I2C connection to Servo PWM Pi device not configured");
            }
        }
        outputMode = (byte) (currentMode2State & INVRT_MASK);
        return OutputPolarity.parse(String.format("0x%02x", outputMode));
    }

    /** {@inheritDoc} */
    @Override
    public void setOutputPolarity(OutputPolarity mode) throws IOException {
        byte currentMode2State;
        byte outputMode;

        try {
            currentMode2State = (byte) this.i2c.readRegister(REGISTER_MODE_2);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutputPolarity() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutputPolarity() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                currentMode2State = MODE_2_DEFAULT;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutputPolarity() I2C connection to Servo PWM Pi device not configured");
            }
        }
        outputMode = (byte) (currentMode2State & INVRT_MASK);
        if (OutputPolarity.parse(String.format("0x%02x", outputMode)) != mode) {
            synchronized (this.i2c) {
                outputMode = (byte) (INVRT_MASK & mode.getValue());
                this.i2c.writeRegister(REGISTER_MODE_2, (byte) (currentMode2State & ~INVRT_MASK | outputMode));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public OutputDriver getOutputDriverType() throws IOException {
        byte currentMode2State;
        byte outputType;

        try {
            currentMode2State = (byte) this.i2c.readRegister(REGISTER_MODE_2);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutputDriverType() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutputDriverType() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                currentMode2State = MODE_2_DEFAULT;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutputDriverType() I2C connection to Servo PWM Pi device not configured");
            }
        }
        outputType = (byte) (currentMode2State & OUTDRV_MASK);
        return OutputDriver.parse(String.format("0x%02x", outputType));
    }

    /** {@inheritDoc} */
    @Override
    public void setOutputDriverType(OutputDriver type) throws IOException {
        byte currentMode2State;
        byte outputType;

        try {
            currentMode2State = (byte) this.i2c.readRegister(REGISTER_MODE_2);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutputDriverType() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutputDriverType() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                currentMode2State = MODE_2_DEFAULT;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutputDriverType() I2C connection to Servo PWM Pi device not configured");
            }
        }
        outputType = (byte) (currentMode2State & OUTDRV_MASK);
        if (OutputDriver.parse(String.format("0x%02x", outputType)) != type) {
            synchronized (this.i2c) {
                outputType = (byte) (OUTDRV_MASK & type.getValue());
                this.i2c.writeRegister(REGISTER_MODE_2, (byte) (currentMode2State & ~OUTDRV_MASK | outputType));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public OEMode getOutNEMode() throws IOException {
        byte currentMode2State;
        byte oePinMode;

        try {
            currentMode2State = (byte) this.i2c.readRegister(REGISTER_MODE_2);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutNEMode() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutNEMode() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                currentMode2State = MODE_2_DEFAULT;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutNEMode() I2C connection to Servo PWM Pi device not configured");
            }
        }
        oePinMode = (byte) (currentMode2State & OUTNE_MASK);
        if (oePinMode == OEMode.HIGH.getValue()) {
            /*
             * PWM pins are set to high-impedance, if pin output type is open-drain
             */
            if (getOutputDriverType() == OutputDriver.OPEN_DRAIN) {
                return OEMode.HIGH_IMPEDANCE;
            }
        } else if (oePinMode > OEMode.HIGH_IMPEDANCE.getValue()) {
            return OEMode.HIGH_IMPEDANCE;
        }
        return OEMode.parse(String.format("0x%02x", oePinMode));
    }

    /** {@inheritDoc} */
    @Override
    public String getOutNEModeBitstring() throws IOException {
        byte currentMode2State;

        try {
            currentMode2State = (byte) this.i2c.readRegister(REGISTER_MODE_2);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutNEModeBitstring() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutNEModeBitstring() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                currentMode2State = MODE_2_DEFAULT;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutNEModeBitstring() I2C connection to Servo PWM Pi device not configured");
            }
        }
        String b = String.format("%16s", Integer.toBinaryString(currentMode2State & OUTNE_MASK)).replace(' ', '0');
        return b.substring(b.length() - 2);
    }

    /** {@inheritDoc} */
    @Override
    public void setOutNEMode(OEMode mode) throws IOException {
        byte currentMode2State;
        byte oePinMode;

        try {
            currentMode2State = (byte) this.i2c.readRegister(REGISTER_MODE_2);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutNEMode() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutNEMode() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                currentMode2State = MODE_2_DEFAULT;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutNEMode() I2C connection to Servo PWM Pi device not configured");
            }
        }
        oePinMode = (byte) (currentMode2State & OUTNE_MASK);
        if (OEMode.parse(String.format("0x%02x", oePinMode)) != mode) {
            synchronized (this.i2c) {
                oePinMode = (byte) (OUTNE_MASK & mode.getValue());
                this.i2c.writeRegister(REGISTER_MODE_2, (byte) (currentMode2State & ~OUTNE_MASK | oePinMode));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public OutputsChangeMode getOutputsChangeMode() throws IOException {
        byte currentMode2State;
        byte ochMode;

        try {
            currentMode2State = (byte) this.i2c.readRegister(REGISTER_MODE_2);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutputsChangeMode() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutputsChangeMode() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                currentMode2State = MODE_2_DEFAULT;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::getOutputsChangeMode() I2C connection to Servo PWM Pi device not configured");
            }
        }
        ochMode = (byte) (currentMode2State & OCH_MASK);
        return OutputsChangeMode.parse(String.format("0x%02x", ochMode));
    }

    /** {@inheritDoc} */
    @Override
    public void setOutputsChangeMode(OutputsChangeMode mode) throws IOException {
        byte currentMode2State;
        byte ochMode;

        try {
            currentMode2State = (byte) this.i2c.readRegister(REGISTER_MODE_2);
        } catch (Pi4JException e) {
            if (this.i2c != null) {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutputsChangeMode() I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") failed");
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutputsChangeMode() I2C connection to Servo PWM Pi device not configured");
            }
        } catch (IllegalStateException e) {
            if (this.i2c != null) {
                logger.warn("Operating I2C connection to Servo PWM Pi device (" + String.format("0x%x", this.i2c.device()) + ") in test (mock) mode");
                currentMode2State = MODE_2_DEFAULT;
            } else {
                throw new InitializeException("ServoPwmPiDeviceImpl::setOutputsChangeMode() I2C connection to Servo PWM Pi device not configured");
            }
        }
        ochMode = (byte) (currentMode2State & OCH_MASK);
        if (OutputsChangeMode.parse(String.format("0x%02x", ochMode)) != mode) {
            synchronized (this.i2c) {
                ochMode = (byte) (OCH_MASK & mode.getValue());
                this.i2c.writeRegister(REGISTER_MODE_2, (byte) (currentMode2State & ~OCH_MASK | ochMode));
            }
        }
    }

    public boolean isInitialized() {
        return (this.context != null);
    }
}
