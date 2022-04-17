package com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN  :: Servo PWM PI Add-on Board
 * FILENAME      :  ServoPwmPiPwmConfigBuilderImpl.java
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
import com.pi4j.extension.addonboard.io.AddOnBoardIOAddressConfigBuilderBase;
import static com.pi4j.io.IOConfig.PLATFORM_KEY;
import com.pi4j.io.pwm.PwmPreset;
import com.pi4j.plugin.addonboard.servopwmpi.platform.ServoPwmPiPlatform;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwmConfig;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwmConfigBuilder;

import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>ServoPwmPiPwmConfigBuilder class.</p>
 *
 * @author Thomas Reim
 * @version $Id: $Id
 */
public class ServoPwmPiPwmConfigBuilderImpl
        extends AddOnBoardIOAddressConfigBuilderBase<ServoPwmPiPwmConfigBuilder, ServoPwmPiPwmConfig>
        implements ServoPwmPiPwmConfigBuilder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected List<PwmPreset> presets = new ArrayList<>();

    /**  
     * PRIVATE CONSTRUCTOR
     * @param context Pi4J context
     */
    protected ServoPwmPiPwmConfigBuilderImpl(Context context){
        super(context);
    }

    /**
     * <p>newInstance.</p>
     *
     * @param context Pi4J context
     * @return a ServoPwmPiPwmConfigBuilderImpl object.
     */
    public static ServoPwmPiPwmConfigBuilder newInstance(Context context) {
        return new ServoPwmPiPwmConfigBuilderImpl(context);
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
    @Override
    public ServoPwmPiPwmConfigBuilderImpl phaseShift(Number phaseShift) {
        // bounds check the phase shift value
        float ps = phaseShift.floatValue();
        if(ps > 100) ps = 100f;

        if (ps >= 0) {
            this.properties.put(ServoPwmPiPwmConfig.PHASE_SHIFT_KEY, Float.toString(ps));
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPwmConfigBuilder dutyCycle(Number dutyCycle) {
        // bounds check the duty-cycle value
        float dc = dutyCycle.floatValue();
        if(dc < 0) dc = 0f;
        if(dc > 100) dc = 100f;

        this.properties.put(ServoPwmPiPwmConfig.DUTY_CYCLE_KEY, Float.toString(dc));
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPwmConfigBuilder shutdown(Number dutyCycle) {
        // bounds check the duty-cycle value
        float dc = dutyCycle.floatValue();
        if(dc < 0) dc = 0f;
        if(dc > 100) dc = 100f;

        this.properties.put(ServoPwmPiPwmConfig.SHUTDOWN_VALUE_KEY, Float.toString(dc));
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPwmConfigBuilder initial(Number dutyCycle) {

        // bounds check the duty-cycle value
        float dc = dutyCycle.floatValue();
        if(dc < 0) dc = 0f;
        if(dc > 100) dc = 100f;

        this.properties.put(ServoPwmPiPwmConfig.INITIAL_VALUE_KEY, Float.toString(dc));
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPwmConfigBuilder preset(PwmPreset ... preset){
        for(PwmPreset p : preset) {
            this.presets.add(p);
        }
        return this;
    }
    /** {@inheritDoc} */
    @Override
    public ServoPwmPiPwmConfig build() {
        /**
         * Servo PWM Pi PWM provider can serve multiple piggy-backed Servo
         * PWM pi Zero boards. As Config objects do not have access to the Pi4J
         * context we need to validate Servo PWM Pi platform settings here.
         */
        if (this.context.platforms().getAll(ServoPwmPiPlatform.class).entrySet().size() == 1) {
            if (!this.properties.containsKey(PLATFORM_KEY)) {
                this.properties.put(PLATFORM_KEY, context.platform(ServoPwmPiPlatform.class).id());
            }
        }
        if (this.properties.containsKey(PLATFORM_KEY)) {
            if (!this.context.hasPlatform(this.properties.get(PLATFORM_KEY))) {
                logger.error("Configuration key platform ignored. Servo PWM Pi platform '" + this.properties.get(PLATFORM_KEY) + "' not found.");
                this.properties.remove(PLATFORM_KEY);
            }
        }
        ServoPwmPiPwmConfig config = new ServoPwmPiPwmConfigImpl(this.getResolvedProperties(), this.presets);
        return config;
    }
}
