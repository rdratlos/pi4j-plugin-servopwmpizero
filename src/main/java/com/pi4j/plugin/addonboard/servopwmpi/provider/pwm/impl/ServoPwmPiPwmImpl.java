package com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: ADDONBOARD   :: Servo PWM PI
 * FILENAME      :  ServoPwmPiPwmImpl.java
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
import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.PwmBase;
import com.pi4j.plugin.addonboard.servopwmpi.internal.ServoPwmPiDevice;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiProvider;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwm;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwmConfig;



/**
 * <p>ServoPwmPiPwm class.</p>
 *
 * @author rdratlos@nepomuc.de
 * @version $Id: $Id
 */
public class ServoPwmPiPwmImpl extends PwmBase  implements ServoPwmPiPwm {

    protected final ServoPwmPiDevice device;
    protected float phase_shift = -1f;

    /**
     * <p>Constructor for ServoPwmPiPwm.</p>
     *
     * @param provider a {@link com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiProvider} object.
     * @param device a {@link com.pi4j.plugin.addonboard.servopwmpi.internal.ServoPwmPiDevice} object.
     * @param config a {@link com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwmConfig} object.
     */
    public ServoPwmPiPwmImpl(ServoPwmPiProvider provider, ServoPwmPiDevice device, ServoPwmPiPwmConfig config){
        super(provider, config);
        this.device = device;
        if (config.phaseShift() != null) {
            this.phase_shift = config.phaseShift();
        }
    }

    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPwm initialize(Context context) throws InitializeException {
        // PWM pin initialization
        // initialize this I/O instance with the underlying hardware chip
        // perform any communication with the hardware to configure this chip for PWM
        device.initialize(this);

        super.initialize(context);

        // return this IO instance
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPwm on() throws IOException {
        float configuredDutyCycle = getDutyCycle();
        if (dutyCycle > 0) {
            this.onState = true;
            this.device.on(this, configuredDutyCycle);
        } else {
            this.off();
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPwm off() throws IOException {
        this.onState = false;
        this.device.off(this);
        setDutyCycle(0f);
        return this;
    }

    /**
     *  Set the duty-cycle value
     *  This method is extends to setDutyCycle() with a phase-shift parameter.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100)
     * @param phase_shift Delay until the PWM output will be asserted: Defined as percentage of a PWM cycle)
     * @throws IOException if fails to communicate with the PWM pin
     */
    public void setDutyCycle(Number dutyCycle, Number phase_shift) throws IOException {
        setDutyCycle(dutyCycle);
        setPhaseShift(phase_shift);
    }

    /** {@inheritDoc} */
    @Override
    public int getFrequency() throws IOException {
        int f = this.device.getFrequency();
        super.setFrequency(f);
        return f;
    }

    /** {@inheritDoc} */
    @Override
    public int getActualFrequency() throws IOException {
        return this.device.getActualFrequency();
    }

    /** {@inheritDoc} */
    @Override
    public void setFrequency(int frequency) throws IOException {
        // Retrieve Servo PWM Pi PWM frequency and store it
        this.getFrequency();
        throw new UnsupportedOperationException("ServoPwmPiPwm::setFrequency(...) is not supported. PWM freuency can be configured only for the Servo PWM Pi board.");
    }

    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPwm shutdown(Context context) throws ShutdownException {
        // perform any shutdown required
        super.shutdown(context);

        // perform any shutdown required
        device.shutdown(this, context);

        // return this IO instance
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public void setPhaseShift(Number phase_shift) throws IOException {
        float ps = phase_shift.floatValue();
        
        if (ps > 100) ps = 100;
        
        if (ps < 0) {
            this.phase_shift = -1f;
        } else {
            this.phase_shift = ps;
        }
    }

    /** {@inheritDoc} */
    @Override
    public float getPhaseShift() throws IOException {
        if (this.phase_shift < 0) {
            return this.dutyCycle();
        } else {
            return this.phase_shift;
        }
    }
}

