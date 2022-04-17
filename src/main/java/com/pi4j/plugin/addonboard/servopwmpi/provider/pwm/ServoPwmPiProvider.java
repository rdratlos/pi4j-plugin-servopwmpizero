package com.pi4j.plugin.addonboard.servopwmpi.provider.pwm;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: ADDONBOARD   :: Servo PWM PI
 * FILENAME      :  ServoPwmPiProvider.java
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

import com.pi4j.provider.exception.ProviderException;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.plugin.addonboard.servopwmpi.ServoPwmPi;
import com.pi4j.plugin.addonboard.servopwmpi.platform.ServoPwmPiPlatform;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.impl.ServoPwmPiProviderImpl;

/**
 * <p>ServoPwmPiProvider interface.</p>
 *
 * @author Thomas Reim
 * @version $Id: $Id
 */
//public interface ServoPwmPiProvider extends AddOnBoardPwmProvider, ServoPwmPi {
public interface ServoPwmPiProvider extends PwmProvider {
    String NAME = ServoPwmPi.SERVOPWMPIZERO_PWM_PROVIDER_NAME;
    String ID = ServoPwmPi.SERVOPWMPIZERO_PWM_PROVIDER_ID;

    /**
     * <p>newInstance.</p>
     *
     * @return a {@link ServoPwmPiProvider} object.
     */
    static ServoPwmPiProvider newInstance() {
        return new ServoPwmPiProviderImpl();
    }
    
    ServoPwmPiPwm create(ServoPwmPiPwmConfig config);

    void add(ServoPwmPiPlatform platform) throws ProviderException;
    void remove(ServoPwmPiPlatform platform);
}
