package com.pi4j.plugin.addonboard.servopwmpi;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: ADDONBOARD   :: Servo PWM PI
 * FILENAME      :  ServoPwmPi.java
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
import com.pi4j.extension.PluginService;
import com.pi4j.extension.addonboard.AddOnBoard;
import com.pi4j.provider.Provider;
import com.pi4j.plugin.addonboard.servopwmpi.provider.pwm.ServoPwmPiProvider;
import com.pi4j.plugin.addonboard.servopwmpi.platform.ServoPwmPiPlatform;
import com.pi4j.plugin.linuxfs.provider.gpio.digital.LinuxFsDigitalOutputProvider;
import java.util.ServiceLoader;

/**
 * <p>ServoPwmPi add-on board plugin class.</p>
 *
 * @author Thomas Reim
 * @version $Id: $Id
 */
public class ServoPwmPi implements AddOnBoard {
    public static final String BOARD_NAME = "Servo PWM Pi";
    public static final String BOARD_ID = "servopwmpizero";

    // Platform name and unique ID
    /** Constant <code>PLATFORM_NAME="NAME +  Platform"</code> */
    public static final String SERVOPWMPIZERO_PLATFORM_NAME = BOARD_NAME + " Platform";
    /** Constant <code>PLATFORM_ID="ID + -platform"</code> */
    public static final String SERVOPWMPIZERO_PLATFORM_ID = BOARD_ID + "-platform";
    /** Constant <code>PLATFORM_DESCRIPTION="Pi4J platform used for mock testing."</code> */
    public static final String SERVOPWMPIZERO_PLATFORM_DESCRIPTION = "Pi4J platform for Servo PWM Pi add-on board.";

    // PWM Provider name and unique ID
    /** Constant <code>PWM_PROVIDER_NAME="NAME +  PWM Provider"</code> */
    public static final String SERVOPWMPIZERO_PWM_PROVIDER_NAME = BOARD_NAME + " PWM Provider";
    /** Constant <code>PWM_PROVIDER_ID="ID + -pwm"</code> */
    public static final String SERVOPWMPIZERO_PWM_PROVIDER_ID = BOARD_ID + "-pwm";
    
    // Inverse output enable control GPIO pin ID (on Raspberry Pi host)
    public static final String SERVOPWMPIZERO_OE_CONTROL_PIN_ID = BOARD_ID + "-Inv(OE)";
    
    private final Provider providers[] = {
            ServoPwmPiProvider.newInstance(),
    };
    private ServoPwmPiPlatform platform = null;
    
    static Iterable<AddOnBoard> getAddOnBoards() {
        return ServiceLoader.load(AddOnBoard.class);
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(PluginService service) throws InitializeException {
        if (this.platform == null) {
            // register all I/O Providers with the plugin service
            this.platform = new ServoPwmPiPlatform();
            service.register(this.platform)
                   .register(this.providers);
        } else {
            throw new InitializeException(String.format("Servo PWM Pi platform '%s' has altrady been initialised", this.platform.id()));
        }
        
    }

    /** {@inheritDoc} */
    @Override
    public void shutdown(Context context) throws ShutdownException {
        // perform shutdown on hardware
        this.platform = null;
    }
}
