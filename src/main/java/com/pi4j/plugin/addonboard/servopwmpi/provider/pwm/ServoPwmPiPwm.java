package com.pi4j.plugin.addonboard.servopwmpi.provider.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: ADDONBOARD   :: Servo PWM PI
 * FILENAME      :  ServoPwmPiPwm.java
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

import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.impl.ServoPwmPiPwmConfigBuilderBase;

/**
 * <p>ServoPwmPiPwm interface.</p>
 *
 * @author Thomas Reim
 * @version $Id: $Id
 */
public interface ServoPwmPiPwm extends Pwm {

    /**
     * <p>newConfigBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link ServoPwmPiPwmConfigBuilder} object.
     */
    static ServoPwmPiPwmConfigBuilder newConfigBuilder(Context context){
        return ServoPwmPiPwmConfigBuilderBase.newInstance(context);
    }

    /**
     * Get the Servo PWM Pi PWM channel number/address of this PWM instance.
     *
     * @return PWM channel  pin number/address (0 -15 for LED1 to LED16 outputs)
     * @throws IOException if fails to communicate with the Servo PWM Pi PWM channel
     */
    int getChannel() throws IOException;

    /**
     * Get the Servo PWM Pi PWM channel number/address of this PWM instance.
     *
     * @return PWM channel  pin number/address (0 -15 for LED1 to LED16 outputs)
     * @throws IOException if fails to communicate with the Servo PWM Pi PWM channel
     */
    default int channel(){
        return getChannel();
    }

    /**
     *  Get the phase-shift value as a decimal value that represents the
     *  percentage of a PWM cycle.  The phase-shift range is valid from 0 to 100
     *  including factional values.
     *
     *  Example: A value of 50 represents a phase-shift where half of
     *  the time period the signal is LOW before the ON signal starts.
     *
     * @return phase-shift value expressed as a percentage (range: 0-100)
     * @throws IOException if fails to communicate with the PWM pin
     */
    float getPhaseShift() throws IOException;

    /**
     *  Get the phase-shift value as a decimal value that represents the
     *  percentage of a PWM cycle.  The phase-shift range is valid from 0 to 100
     *  including factional values.
     *
     *  Example: A value of 50 represents a phase-shift where half of
     *  the time period the signal is LOW before the HIGH signal starts.
     *
     * @return phase-shift value expressed as a percentage (range: 0-100)
     * @throws IOException if fails to communicate with the PWM pin
     */
    default float phaseShift() throws IOException { return getPhaseShift();}

    /**
     *  Set the phase-shift value as a decimal value that represents the
     *  percentage of a PWM cycle. The phase-shift range is valid from 0 to 100
     *  including factional values. This method will not update a live PWM signal,
     *  but rather stage the phase-shift value for subsequent call to the
     *  'ServoPwmPiPwm::On()' method.  Call 'ServoPwmPiPwm::On()' if you
     *  wish to make a live/immediate change to the phase-shift on an existing
     *  PWM signal. Special value (-1) lets Servo PWM Pi auto-configure the
     *  phase shift.
     *
     * @param phaseShift phase-shift value expressed as a percentage (range: 0-100)
     * @throws IOException if fails to communicate with the PWM pin
     */
    void setPhaseShift(Number phaseShift) throws IOException;

    /**
     *  Set the phase-shift value as a decimal value that represents the
     *  percentage of a PWM cycle. The phase-shift range is valid from 0 to 100
     *  including factional values. This method will not update a live PWM signal,
     *  but rather stage the phase-shift value for subsequent call to the
     *  'ServoPwmPiPwm::On()' method.  Call 'ServoPwmPiPwm::On()' if you
     *  wish to make a live/immediate change to the phase-shift on an existing
     *  PWM signal. Special value (-1) lets Servo PWM Pi auto-configure the
     *  phase shift.
     *
     * @param phaseShift phase-shift value expressed as a percentage (range: 0-100)
     * @return returns this ServoPwmPiPwm instance
     * @throws IOException if fails to communicate with the PWM pin
     */
    default ServoPwmPiPwm phaseShift(Number phaseShift) throws IOException { setPhaseShift(phaseShift); return this; }

}
