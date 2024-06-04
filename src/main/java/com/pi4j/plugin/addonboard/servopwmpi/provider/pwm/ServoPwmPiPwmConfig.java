package com.pi4j.plugin.addonboard.servopwmpi.provider.pwm;

import com.pi4j.io.exception.IOException;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN  :: Servo PWM PI Add-on Board
 * FILENAME      :  ServoPwmPiPwmConfig.java
 *
 * This file is an extension for the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.io.pwm.PwmConfig;

/**
 * <p>ServoPwmPiPwmConfig interface.</p>
 *
 * @author Thomas Reim
 * @version $Id: $Id
 */
public interface ServoPwmPiPwmConfig extends PwmConfig {

    /** Constant <code>PHASE_SHIFT_KEY="phase-shift</code> */
    String PHASE_SHIFT_KEY = "phase-shift";

    /**
     *  Get the phase-shift value as a decimal value that represents the
     *  percentage of a PWM cycle.  The phase-shift range is valid from 0 to 100
     *  including factional values.
     *
     *  Example: A value of 50 represents a phase-shift where half of
     *  the time period the signal is LOW before the ON signal starts.
     *
     * @return phase-shift value expressed as a percentage (range: 0-100)
     */
    Float phaseShift();

    /**
     *  Get the phase-shift value as a decimal value that represents the
     *  percentage of a PWM cycle.  The phase-shift range is valid from 0 to 100
     *  including factional values.
     *
     *  Example: A value of 50 represents a phase-shift where half of
     *  the time period the signal is LOW before the ON signal starts.
     *
     * @return phase-shift value expressed as a percentage (range: 0-100)
     */
    default Float getPhaseShift() {
        return phaseShift();
    }

    /**
     * Get the Servo PWM Pi PWM channel number/address for the PWM instance.
     *
     * @return PWM channel  pin number/address (0 -15 for LED1 to LED16 outputs)
     */
    Integer getChannel();

    /**
     * Get the Servo PWM Pi PWM channel number/address for the PWM instance.
     *
     * @return PWM channel  pin number/address (0 -15 for LED1 to LED16 outputs)
     */
    default Integer channel(){
        return getChannel();
    }

}
