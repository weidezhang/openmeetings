package net.java.sip.communicator.plugin.openmeetings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;

import javax.xml.soap.SOAPException;

//import net.java.sip.communicator.impl.protocol.zeroconf.MessageZeroconfImpl;
import net.java.sip.communicator.service.configuration.ConfigurationService;
import net.java.sip.communicator.util.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


public class OpenmeetingsConfigManager {
	Logger logger = Logger.getLogger(OpenmeetingsPluginActivator.class);
	private String server;
    private String protoPrefix;
    private String omUriContext;
	private String login;
	private String password;
	private String stringForHash = "user:";
	private String hash;
	OpenmeetingsPluginSoapClient soapClient;
	private static BundleContext bundleContext;
	private static ConfigurationService configurationService = null;
	
	private static OpenmeetingsConfigManager instance;
	private EncryptionEngine encryptionEngine;
	
	private OpenmeetingsConfigManager(){
		
		super();
		soapClient = new OpenmeetingsPluginSoapClient();
		encryptionEngine = new EncryptionEngine();
	}

	public static OpenmeetingsConfigManager getInstance(){
		if( instance == null ){
			instance = new OpenmeetingsConfigManager();
		}
		return instance;
	}
	
	 private static String convertToHex(byte[] data) { 
	        StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < data.length; i++) { 
	            int halfbyte = (data[i] >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do { 
	                if ((0 <= halfbyte) && (halfbyte <= 9)) 
	                    buf.append((char) ('0' + halfbyte));
	                else 
	                    buf.append((char) ('a' + (halfbyte - 10)));
	                halfbyte = data[i] & 0x0F;
	            } while(two_halfs++ < 1);
	        } 
	        return buf.toString();
	 } 
	 
	private String getHash() throws Exception {
		
		if( hash != null )
			return hash;
		
		byte[] bytesOfMessage = null;
		try {
			bytesOfMessage = stringForHash.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] thedigest = md.digest(bytesOfMessage);
		
		final String hash = convertToHex(thedigest);
		return hash;
	}
	private String getMd5Hash( String str ) throws Exception{
		
		byte[] bytesOfMessage = null;
		try {
			bytesOfMessage = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println( e.getMessage() );			
		}

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] thedigest = md.digest(bytesOfMessage);
		
		final String hash = convertToHex(thedigest);
		return hash;		
	}
	
	public String getCreationUrl() throws Exception{
        final String url = getProtoPrefix() + getServer() + "/client/username="+getLogin()+"&password="+getPassword()+
							"&hash=" + getHash();
		System.out.println( "CREATION URL = " + url );
		return url;
	}
	
	public String createInvitationUrl( String hash ) throws Exception {
		final String url = "http://"+ getServer() + "/openmeetings/?invitationHash=" + hash;
		System.out.println( "INVITATION URL = " + url );
		return url;		
	}
	
	public String getInvitationUrl(String displayedName) throws Exception {
        String protoPrefix = getProtoPrefix();
		String server = getServer();
        String uriContext = getOmUriContext();
		soapClient.setServerUrl(protoPrefix + server + uriContext);
		
		String invitationHash = null; 						
		
		try {
			invitationHash = soapClient.getInvitationHash(getLogin(), getPassword(), displayedName);
		} catch (Exception e) {			
			logger.error(e);
		}

		if (invitationHash == null) return null;

		String invitationUrl = createInvitationUrl(invitationHash);
		return invitationUrl;		
	}

	public String sendUrl() throws Exception {
				
		String url = getCreationUrl();
		HttpURLConnection omURLConnection = null;
		
		try {
		    URL omURL = new URL("http://" + getServer() );
		    omURLConnection = (HttpURLConnection) omURL.openConnection();
		    omURLConnection.setRequestMethod("GET");
		    omURLConnection.setDoInput(true);
		    omURLConnection.setDoOutput( true );
		    omURLConnection.connect();

		} catch (MalformedURLException e) {     // new URL() failed
			
			return null;
			
		} catch (IOException e) {               // openConnection() failed
			return null;
		}				 
		
		//write to url		
		OutputStreamWriter out = new OutputStreamWriter(
                omURLConnection.getOutputStream());
		out.write( url );
		out.flush();
			
		out.close();
        System.out.println("After flushing output stream. ");
        System.out.println("Getting an input stream...");
        InputStream is = omURLConnection.getInputStream();
        // any response?
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        String response = null;
        while ( (line = br.readLine()) != null)
        {
            System.out.println("line: " + line);
            response += line;            
        }
		
		out.close();
		hash = null;
		
		return response;
	}
	
	 public static ConfigurationService getConfigurationService()
	    {
	        if (configurationService == null)
	        {
	            ServiceReference confReference
	                = bundleContext.getServiceReference(
	                    ConfigurationService.class.getName());
	            configurationService
	                = (ConfigurationService) bundleContext
	                                        .getService(confReference);
	        }
	        return configurationService;
	    }
	 
    public void setServer(String server){
		this.server = server;
		getConfigurationService().setProperty( "plugin.openmeetings.SERVER", server);
	}
	public String getServer() {
		String value = (String)getConfigurationService().getProperty( "plugin.openmeetings.SERVER" );
        if (null == value) {
            value = "";
        }
		server = value;
		return server;
	}

    public void setProtoPrefix(String protoPrefix){
		this.protoPrefix = protoPrefix;
		getConfigurationService().setProperty("plugin.openmeetings.PROTOCOL_PREFIX", protoPrefix);
	}
    public String getProtoPrefix() {
		String value = (String)getConfigurationService().getProperty("plugin.openmeetings.PROTOCOL_PREFIX");
        if (null == value) {
            value = "";
        }
		protoPrefix = value;
		return protoPrefix;
	}

    public void setOmUriContext(String omUriContext){
		this.omUriContext = omUriContext;
		getConfigurationService().setProperty("plugin.openmeetings.OM_URI_CONTEXT", omUriContext);
	}
    public String getOmUriContext() {
		String value = (String)getConfigurationService().getProperty("plugin.openmeetings.OM_URI_CONTEXT");
        if (null == value) {
            value = "";
        }
        if (!value.endsWith("/")) {
            value += "/";
        }
		omUriContext = value;
		return omUriContext;
	}

	public void setLogin( String login ){
		this.login = login;		
		getConfigurationService().setProperty( "plugin.openmeetings.LOGIN", login);
	}
	public String getLogin() {
		String value = (String)getConfigurationService().getProperty("plugin.openmeetings.LOGIN");
		login = value;
		return login;
	}

	public void setPassword(String password) throws Exception {
		this.password = password;
		if( password == null )
			return;
		String encrypted = encryptionEngine.encrypt( password );
		getConfigurationService().setProperty( "plugin.openmeetings.ENCRYPTED_PASSWORD", encrypted );
	}
	public String getPassword() throws Exception {
		String value = (String)getConfigurationService().getProperty( "plugin.openmeetings.ENCRYPTED_PASSWORD" );
		if( value == null )
			return null;
		password = encryptionEngine.decrypt( value );
		return password;
	}

	public void setContext(BundleContext bc_) {
		this.bundleContext = bc_;		
	}
}
