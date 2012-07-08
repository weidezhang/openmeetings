package net.java.sip.communicator.plugin.openmeetings;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import net.java.sip.communicator.util.swing.*;

import org.osgi.framework.*;


public class OpenmeetingsConfigPanel
        extends TransparentPanel
{    
   
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final JTextField teServer = new JTextField( 20 );
	private final JTextField teLogin = new JTextField( 20 );
	private final JPasswordField tePassword = new JPasswordField( 20 );
	private final JTextField fakeField = new JTextField( 20 );
	private final JButton btOk = new JButton(OpenmeetingsPluginActivator.resourceService.
										getI18NString("plugin.openmeetings.BUTTON_OK"));
    
	private String server;
    private String login;
    private String password;
    
    public OpenmeetingsConfigPanel() throws Exception
    {
        super(new BorderLayout());

        Dimension prefSize = new Dimension( 105, 30 );
        JPanel headerPanel = new TransparentPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.LINE_AXIS));

        JLabel lblHeader = new JLabel(OpenmeetingsPluginActivator.resourceService.
        		getI18NString("plugin.openmeetings.CONFIG_HEADER"));
        
        headerPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        headerPanel.add( lblHeader );
        lblHeader.setPreferredSize( new Dimension( 200, 30 ));
        
        JPanel serverPanel = new TransparentPanel();
        serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.LINE_AXIS));        
        JLabel lblServer = new JLabel(OpenmeetingsPluginActivator.resourceService.
        						getI18NString("plugin.openmeetings.SERVER"));
        lblServer.setPreferredSize(prefSize);
        serverPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        serverPanel.add(lblServer);        
        serverPanel.add( teServer );
        
        JPanel loginPanel = new TransparentPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.LINE_AXIS));        
        JLabel lblLogin = new JLabel(OpenmeetingsPluginActivator.resourceService.
        						getI18NString("plugin.openmeetings.LOGIN"));
        lblLogin.setPreferredSize(prefSize);
        loginPanel.setAlignmentX(LEFT_ALIGNMENT);
        loginPanel.add(lblLogin);
        loginPanel.add( teLogin );        
        
        JPanel passwordPanel = new TransparentPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.LINE_AXIS));        
        JLabel lblPassword = new JLabel(OpenmeetingsPluginActivator.resourceService.
        						getI18NString("plugin.openmeetings.PASWWORD"));
        lblPassword.setPreferredSize(prefSize);
        passwordPanel.setAlignmentX(LEFT_ALIGNMENT);
        passwordPanel.add( lblPassword );
        passwordPanel.add( tePassword );
        
        
        teServer.setText( OpenmeetingsConfigManager.getInstance().getServer() );
        teLogin.setText( OpenmeetingsConfigManager.getInstance().getLogin() );
        tePassword.setText( OpenmeetingsConfigManager.getInstance().getPassword() );
        
        JPanel buttonPanel = new TransparentPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setAlignmentX( LEFT_ALIGNMENT );
        btOk.addActionListener(new ButtonOkListener());
        btOk.setAlignmentX(LEFT_ALIGNMENT);
        btOk.setPreferredSize(new Dimension( 50, 30 ) );
        //buttonPanel.add( Box.createRigidArea( new Dimension( 60, 5)));
        buttonPanel.add(btOk);
        buttonPanel.add(fakeField);
        fakeField.setVisible( false );

        JPanel omPanel = new TransparentPanel();        
        omPanel.setLayout(new BoxLayout(omPanel, BoxLayout.PAGE_AXIS));
        omPanel.add( headerPanel );
        omPanel.add( Box.createRigidArea( new Dimension(20, 5)));
        omPanel.add( serverPanel, BorderLayout.WEST );
        omPanel.add( Box.createRigidArea( new Dimension(20, 5)));
        omPanel.add( loginPanel );
        omPanel.add( Box.createRigidArea( new Dimension(20, 5)));
        omPanel.add( passwordPanel );
        omPanel.add( Box.createRigidArea( new Dimension(20, 5)));
        omPanel.add( buttonPanel );

        add( omPanel, BorderLayout.PAGE_START);
    }
    public String getTest(){
    	
    	return "Hello!!!!";
    }
    
    public void setServer(String server) {
    	
		this.server = server;		
	}
    
	public String getServer() {
		return server;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	public String getLogin() {
		return login;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}

	private class ButtonOkListener implements ActionListener
    {
        /**
         * Invoked when an action occurs.
         * @param e <tt>ActionEvent</tt>.
         */
        public void actionPerformed(ActionEvent e)
        {  	
        	OpenmeetingsConfigManager.getInstance().setSever( teServer.getText() );
        	OpenmeetingsConfigManager.getInstance().setLogin( teLogin.getText() );
        	try {
				OpenmeetingsConfigManager.getInstance().setPassword( new String( tePassword.getPassword() ) );
			} catch (Exception e1) {				
				e1.printStackTrace();
			}
        }
    }
    private class ButtonCancelListener implements ActionListener
    {
        /**
         * Invoked when an action occurs.
         * @param e <tt>ActionEvent</tt>.
         */
        public void actionPerformed(ActionEvent e)
        {
        	
        }
    }    
}
