/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.openmeetings;

import java.util.*;

import net.java.sip.communicator.impl.gui.event.PluginComponentEvent;
import net.java.sip.communicator.impl.gui.main.contactlist.ContactListPane;
import net.java.sip.communicator.plugin.otr.OtrActivator;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.gui.internal.GuiServiceActivator;
import net.java.sip.communicator.service.resources.ResourceManagementService;
import net.java.sip.communicator.service.resources.ResourceManagementServiceUtils;
import net.java.sip.communicator.util.*;

import org.osgi.framework.*;

public class OpenmeetingsPluginActivator
    implements BundleActivator
{
    public static BundleContext bundleContext;

    public static ResourceManagementService resourceService;

    Logger logger = Logger.getLogger(OpenmeetingsPluginActivator.class);

    /**
     * Called when this bundle is started so the Framework can perform the
     * bundle-specific activities necessary to start this bundle. In the case of
     * our example plug-in we create our menu item and register it as a plug-in
     * component in the right button menu of the contact list.
     */
    public void start(BundleContext bc) throws Exception
    {
        bundleContext = bc;
        resourceService =
            ResourceManagementServiceUtils
                .getService(OpenmeetingsPluginActivator.bundleContext);

        OpenmeetingsPluginMenuItem openMeetingsPlugin =
            new OpenmeetingsPluginMenuItem(bc);

        Hashtable<String, String> containerFilter =
            new Hashtable<String, String>();
        containerFilter.put(Container.CONTAINER_ID,
            Container.CONTAINER_CONTACT_RIGHT_BUTTON_MENU.getID());

        bc.registerService(PluginComponent.class.getName(), openMeetingsPlugin,
            containerFilter);

        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(ConfigurationForm.FORM_TYPE,
            ConfigurationForm.ADVANCED_TYPE);
        bc.registerService(
            ConfigurationForm.class.getName(),
            new LazyConfigurationForm(
                "net.java.sip.communicator.plugin.openmeetings.OpenmeetingsConfigPanel",
                getClass().getClassLoader(), "plugin.skinmanager.PLUGIN_ICON",
                "plugin.openmeetings.PLUGIN_NAME", 1002, true), properties);

    }

    /**
     * Called when this bundle is stopped so the Framework can perform the
     * bundle-specific activities necessary to stop the bundle. In the case of
     * our example plug-in we have nothing to do here.
     */
    public void stop(BundleContext bc) throws Exception
    {

    }
}
