package com.pi4j.plugin.addonboard.servopwmpi.provider.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: ADDONBOARD   :: Servo PWM PI
 * FILENAME      :  ServoPwmPiPwmConfigBuilder.java
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
import com.pi4j.io.IOAddressConfigBuilder;
import com.pi4j.io.pwm.PwmPreset;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.impl.ServoPwmPiPwmConfigBuilderBase;

/**
 * <p>ServoPwmPiPwmConfigBuilder interface.</p>
 *
 * @author Thomas Reim
 * @version $Id: $Id
 */
//public interface ServoPwmPiPwmConfigBuilder  extends ConfigBuilder<ServoPwmPiPwmConfigBuilder, ServoPwmPiPwmConfig> {
public interface ServoPwmPiPwmConfigBuilder
        extends IOAddressConfigBuilder<ServoPwmPiPwmConfigBuilder, ServoPwmPiPwmConfig> {
    /**
     * <p>newInstance.</p>
     *
     * @param context {@link Context}
     * @return a ServoPwmPiPwmConfigBuilder object.
     */
    static ServoPwmPiPwmConfigBuilder newInstance(Context context)  {
        return ServoPwmPiPwmConfigBuilderBase.newInstance(context);
    }

    /**
     *  Set the phase shift value as a decimal value that represents the
     *  percentage of a PWM cycle. The phase shift range is valid from 0 to 100
     *  including factional values.  (Value -1 means the phase shift will be
     *  configured by the Servo PWM Pi device.)
     *
     * @param phaseShift phase shift value expressed as a percentage (range: 0-100)
     * @return this builder instance
     */
    ServoPwmPiPwmConfigBuilder phaseShift(Number phaseShift);
    
    /**
     *  Set the duty-cycle value as a decimal value that represents the
     *  percentage of the ON vs OFF time of the PWM signal for each
     *  period.  The duty-cycle range is valid from 0 to 100 including
     *  factional values.  (Values above 50% mean the signal will
     *  remain HIGH more time than LOW.)
     *
     *  Example: A value of 50 represents a duty-cycle where half of
     *  the time period the signal is LOW and the other half is HIGH.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this builder instance
     */
    ServoPwmPiPwmConfigBuilder dutyCycle(Number dutyCycle);

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when the Pi4J context is shutdown.
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this builder instance
     */
    ServoPwmPiPwmConfigBuilder shutdown(Number dutyCycle);

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when this PWM instance is created and initialized.
     * This option can be helpful if you wish to do something like set a default PWM
     * signal (by configuring this 'initial' value to 50%) when your application
     * creates the PWM instance.  This just helps eliminate a second line of code
     * to manually start the PWM signal for cases where you prefer it is auto-started.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this builder instance
     */
    ServoPwmPiPwmConfigBuilder initial(Number dutyCycle);

    /**
     * Add one or more PwmPresets to this PWM instance. You can create new PWM
     * preset instance using the 'PwmPreset::newBuilder(name)' static
     * factory method.
     *
     * @param preset one or more pre-configured PwmPreset instances
     * @return this builder instance
     */
    ServoPwmPiPwmConfigBuilder preset(PwmPreset ... preset);
}
