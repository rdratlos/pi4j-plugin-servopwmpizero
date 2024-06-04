package com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN  :: Servo PWM PI Add-on Board
 * FILENAME      :  ServoPwmPiPwmConfigImpl.java
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

import com.pi4j.config.AddressConfig;
import com.pi4j.config.ConfigBase;
import com.pi4j.config.exception.ConfigException;
import com.pi4j.config.exception.ConfigMissingRequiredKeyException;
import com.pi4j.io.*;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmPolarity;
import com.pi4j.io.pwm.PwmPreset;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.util.StringUtil;
import java.util.ArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwmConfig;

/**
 * <p>ServoPwmPiPwmConfigImpl class.</p>
 *
 * @author Thomas Reim
 * @version $Id: $Id
 */
public class ServoPwmPiPwmConfigImpl
        extends ConfigBase<PwmConfig>
        implements IOConfig<PwmConfig>, AddressConfig<PwmConfig>, ServoPwmPiPwmConfig {
    private static final Logger logger = LoggerFactory.getLogger(ServoPwmPiPwmConfigImpl.class);

    // private configuration properties
    protected Integer channel = null;
    protected Integer address = null;
    protected Float dutyCycle = null;
    protected Float phaseShift = null;
    protected PwmPolarity polarity = PwmPolarity.NORMAL;
    protected PwmType pwmType = PwmType.HARDWARE;
    protected Float shutdownValue = null;
    protected Float initialValue = null;
    protected List<PwmPreset> presets = new ArrayList<>();
    protected String provider = null;
    protected String platform = null;

    /**
     * PRIVATE CONSTRUCTOR
     *
     * @param properties a {@link java.util.Map} object.
     * @param presets a {@link java.util.Collection} object.
     */
    public ServoPwmPiPwmConfigImpl(Map<String,String> properties, Collection<PwmPreset> presets){
        this(properties);
        this.presets.addAll(presets);
    }

    /**
     * PRIVATE CONSTRUCTOR
     *
     * @param properties a {@link java.util.Map} object.
     */
    public ServoPwmPiPwmConfigImpl(Map<String,String> properties) throws ConfigMissingRequiredKeyException, ConfigException {
        super(properties);

        // load platform property
        if(properties.containsKey(PLATFORM_KEY)){
            this.platform = properties.get(PLATFORM_KEY);
        } else {
            throw new ConfigMissingRequiredKeyException(PLATFORM_KEY);
        }

        // load address (=Servo PWM Pi PWM channel number) property
        if(properties.containsKey(ADDRESS_KEY)){
            this.channel = Integer.parseInt(properties.get(ADDRESS_KEY));
        } else {
            throw new ConfigMissingRequiredKeyException(ADDRESS_KEY);
        }

        Pattern pattern = Pattern.compile("[-_]([0-9]+)$");
        Matcher matcher = pattern.matcher(this.platform);
        String pwmID;
        if ( matcher.find() ) {
            Integer i2c_address = Integer.parseInt( matcher.group(1) );
            int platform_prefix = i2c_address.intValue() & 0xFF;
            this.address = Integer.valueOf( platform_prefix * 256 + this.channel.intValue() );
            pwmID = String.format( "SERVOPWM-%s.%d", platform_prefix, this.channel );
        } else {
            throw new ConfigException("Configuration key platform has invalid Servo PWM Pi platform ID");
        }

        // define default property values if any are missing (based on the required address value)
        this.id = StringUtil.setIfNullOrEmpty(this.id, pwmID, true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, pwmID, true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, pwmID, true);

        // load optional pwm duty-cycle from properties
        if(properties.containsKey(PHASE_SHIFT_KEY)){
            this.phaseShift = Float.parseFloat(properties.get(PHASE_SHIFT_KEY));
        }

        // load optional pwm duty-cycle from properties
        if(properties.containsKey(DUTY_CYCLE_KEY)){
            this.dutyCycle = Float.parseFloat(properties.get(DUTY_CYCLE_KEY));
        }

        // load optional pwm type from properties
        if(properties.containsKey(PWM_TYPE_KEY)){
            this.pwmType = PwmType.parse(properties.get(PWM_TYPE_KEY));
        }

        // load initial value property
        if(properties.containsKey(INITIAL_VALUE_KEY)){
            this.initialValue = Float.parseFloat(properties.get(INITIAL_VALUE_KEY));
        }

        // load shutdown value property
        if(properties.containsKey(SHUTDOWN_VALUE_KEY)){
            this.shutdownValue = Float.parseFloat(properties.get(SHUTDOWN_VALUE_KEY));
        }

        // bounds checking
        if(this.dutyCycle != null) {
            if(this.dutyCycle > 100)
                this.dutyCycle = 100f;
            if(this.dutyCycle < 0)
                this.dutyCycle = 0f;
        }

        // bounds checking
        if(this.phaseShift != null) {
            if (this.phaseShift > 100) {
                this.phaseShift = 100f;
            }
            if (this.phaseShift < 0)
                this.phaseShift = null;
        }

        // load provider property
        if(properties.containsKey(PROVIDER_KEY)){
            this.provider = properties.get(PROVIDER_KEY);
        }
    }

    @Override
    public Integer address() {
        return this.address;
    }

    @Override
    public Integer getChannel() {
        return this.channel;
    }

   @Override
    public String provider() {
        return this.provider;
    }

    @Override
    public String platform() {
        return this.platform;
    }

    @Override
    public Float dutyCycle() {
        return this.dutyCycle;
    }

    @Override
    public Float phaseShift() {
        return this.phaseShift;
    }

    @Override
    public Integer frequency() {
        logger.debug( "Ignoring per PWM pin frequency configuration for PWM '{}'", this.id );
        return null;
    }

    @Override
    public PwmType pwmType() {
        return this.pwmType;
    }

    @Override
    public Float shutdownValue() {
        return this.shutdownValue;
    }

    @Override
    public ServoPwmPiPwmConfig shutdownValue(Number dutyCycle) {

        // bounds check the duty-cycle value
        float dc = dutyCycle.floatValue();
        if(dc < 0) dc = 0;
        if(dc > 100) dc = 100;

        this.shutdownValue = dc;
        return this;
    }

    @Override
    public Float initialValue() {
        return this.initialValue;
    }

    @Override
    public Collection<PwmPreset> presets() {
        return this.presets;
    }

    @Override
    public PwmPolarity polarity() {
        return this.polarity;
    }

}
