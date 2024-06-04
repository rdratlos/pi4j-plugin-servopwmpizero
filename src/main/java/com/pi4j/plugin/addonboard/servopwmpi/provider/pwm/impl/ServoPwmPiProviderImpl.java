package com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.impl;

import com.pi4j.boardinfo.util.BoardInfoHelper;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN  :: Servo PWM PI Add-on Board
 * FILENAME      :  ServoPwmPiProviderImpl.java
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

import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProviderBase;
import com.pi4j.plugin.addonboard.servopwmpi.platform.ServoPwmPiPlatform;
import com.pi4j.provider.exception.ProviderException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiProvider;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwm;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiPwmConfig;

/**
 * <p>ServoPwmPiProviderImpl class.</p>
 *
 * @author Thomas Reim
 * @version $Id: $Id
 */
public class ServoPwmPiProviderImpl extends PwmProviderBase implements ServoPwmPiProvider {

    protected Map<String, ServoPwmPiPlatform> platforms = new ConcurrentHashMap<>();

    /**
     * <p>Constructor for ServoPwmPiProviderImpl.</p>
     */
    public ServoPwmPiProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    @Override
    public int getPriority() {
        // the Servo PWM Pi PWM driver should be higher priority than conflicting (new) Linux FS PWM driver
        return BoardInfoHelper.usesRP1() ? 150 : 100;
    }

    /**
     * <p>create.</p>
     *
     * @param config a ServoPwmPiPwmConfig object.
     * @return a ServoPwmPiPwmImpl object.
     */
    @Override
    public ServoPwmPiPwm create(ServoPwmPiPwmConfig config) throws ProviderException {
        ServoPwmPiPwm pwm = null;

        // validate provider setup
        if(this.platforms.isEmpty()){
            throw new ProviderException("ServoPwmPiProvider::add(...) has not been called; this provider must be configured using the add() method prior to creating I/O instances.");
        }

        if (!config.getPlatform().isEmpty()) {
            if (this.platforms.containsKey(config.getPlatform())) {
                pwm = new ServoPwmPiPwmImpl(this, platforms.get(config.getPlatform()).pwmDevice(), config);
            } else {
                throw new ProviderException(String.format("Servo PWM Pi platform '%s' does not exist", config.getPlatform()));
            }
        } else {
            if (this.platforms.size() > 1) {
                throw new ProviderException(String.format("Please specify the Servo PWM Pi platform to use for PWM pin '%s'", config.id()));
            }
        }

        // create new output I/O instance
        if (pwm == null) {
            pwm = new ServoPwmPiPwmImpl(this, this.platforms.values().stream().findFirst().get().pwmDevice(), config);
        }

        this.context.registry().add(pwm);
        return pwm;
    }

    @Override
    public void add(ServoPwmPiPlatform platform) throws ProviderException {
        this.platforms.putIfAbsent(platform.id(), platform);
    }

    @Override
    public void remove(ServoPwmPiPlatform platform) {
        if (this.platforms.containsKey(platform.id())) {
            this.platforms.remove(platform.id(), platform);
        }
    }

    @Override
    public Pwm create(PwmConfig config) {
        throw new UnsupportedOperationException("create(PwmConfig config) is not supported for Servo PWM Pi PWMs.");
    }
}
