/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.openmeetings;

import java.awt.*;
import java.awt.event.*;

import net.java.sip.communicator.plugin.otr.OtrActivator;
import net.java.sip.communicator.service.protocol.Message;
import net.java.sip.communicator.service.protocol.Contact;
import net.java.sip.communicator.service.protocol.OperationSetBasicInstantMessaging;
import net.java.sip.communicator.service.protocol.ProtocolProviderService;

import javax.swing.*;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import net.java.sip.communicator.service.contactlist.*;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.gui.Container;


public class OpenmeetingsPluginMenuItem
    extends AbstractPluginComponent
    implements ActionListener
{
    private JMenuItem menuItem;

    private MetaContact metaContact;
    
    private BundleContext bc;

    /**
     * Creates an instance of <tt>OpenmeetingsPluginMenuItem</tt>.
     * @param  
     */
    public OpenmeetingsPluginMenuItem( BundleContext bc_)
    {
        super(Container.CONTAINER_CONTACT_RIGHT_BUTTON_MENU);        
        this.bc = bc_;
        OpenmeetingsConfigManager.getInstance().setContext( bc_ );
    }
 
    /**
     * Listens for events triggered by user clicks on this menu item. Opens
     * the <tt>PluginDialog</tt>.
     */
    public void actionPerformed(ActionEvent e)
    { 
    	
      	String invitationUrl = null;
		try {
			invitationUrl = OpenmeetingsConfigManager.getInstance().getInvitationUrl( 
											OpenmeetingsConfigManager.getInstance().getLogin() );
		} catch (Exception e1) {
			System.out.println( e1.getMessage() );			
		}
    	if( invitationUrl.equals(null)){
    		System.out.println("Can't get invitation URL");
    		return;
    	}
    	
    	openUrl( invitationUrl );    	
    	
    	Contact to = metaContact.getDefaultContact();
      	String invitationUrlForSend = null;
		try {
			invitationUrlForSend = OpenmeetingsConfigManager.getInstance().getInvitationUrl( to.getDisplayName() );
		} catch (Exception e1) {
			System.out.println( e1.getMessage() );			
		}
    	if( invitationUrl.equals(null)){
    		System.out.println("Can't get invitation URL For send");
    		return;
    	}
    	    	
    	ServiceReference cRef = bc.getServiceReference( ProtocolProviderService.class.getName() );
    	ProtocolProviderService jabberProvider =
    		(ProtocolProviderService)bc.getService(cRef);
    	
    	OperationSetBasicInstantMessaging basicInstMsgImpl =
    		(OperationSetBasicInstantMessaging)jabberProvider.getOperationSet(OperationSetBasicInstantMessaging.class);
    	    	
    	String message = "I am inviting you to the conference. Please, click the link " + invitationUrlForSend;
    	Message msg = basicInstMsgImpl.createMessage( message);
    	basicInstMsgImpl.sendInstantMessage( to ,  msg);  	

    }

    private boolean conferenceCreated(String response) {
		
    	if( response.contains("It works!"))
    		return true;
    	
		return false;
	}

	private void openUrl(String url){
		
		if( url == null )
			return;
    	
    	if( !java.awt.Desktop.isDesktopSupported() ) {

            System.err.println( "Desktop is not supported (fatal)" );
            System.exit( 1 );
        }

        if ( url.length() == 0 ) {

            System.out.println( "Usage: OpenURI [URI [URI ... ]]" );
            System.exit( 0 );
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {

            System.err.println( "Desktop doesn't support the browse action (fatal)" );
            System.exit( 1 );
        }

        try {
             java.net.URI uri = new java.net.URI( url );
              desktop.browse( uri );
          }
         catch ( Exception e ) {

             System.err.println( e.getMessage() );
         }       
    	
    }
    /*
     * Implements PluginComponent#getComponent().
     */
    public Object getComponent()
    {
        if (menuItem == null)
        {
            menuItem = new JMenuItem(getName());
            menuItem.addActionListener(this);
        }
        return menuItem;
    }

    /*
     * Implements PluginComponent#getName().
     */
    public String getName()
    {
        return OpenmeetingsPluginActivator.resourceService.getI18NString("plugin.openmeetings.MENU_ITEM");
    }

    /**
     * Sets the current <tt>MetaContact</tt>. This in the case of the contact
     * right button menu container would be the underlying contact in the 
     * contact list.
     * 
     * @param metaContact the <tt>MetaContact</tt> to set.
     * 
     * @see PluginComponent#setCurrentContact(MetaContact)
     */
    public void setCurrentContact(MetaContact metaContact)
    {
        this.metaContact = metaContact;
    }
}
