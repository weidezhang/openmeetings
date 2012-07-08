package net.java.sip.communicator.plugin.openmeetings;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OpenmeetingsPluginSoapClient {

	String serverUrl;
	
	private static final String NAMESPACE_PREFIX = "openmeetings";
	
	public OpenmeetingsPluginSoapClient(){
		super();
	}
	
	public String getSID( String username, String password ) throws Exception {
		final SOAPMessage soapMessage = getSoapMessage();
		final SOAPBody soapBody = soapMessage.getSOAPBody();
		final SOAPElement sessionElement = soapBody.addChildElement("getSession", NAMESPACE_PREFIX);
	
		soapMessage.saveChanges();
		
//		System.out.println("\n Soap request:\n");
//		soapMessage.writeTo(System.out);
//		System.out.println();
		
		final SOAPConnection soapConnection = getSoapConnection();
		final SOAPMessage soapMessageReply = soapConnection.call(soapMessage, getUserServiceUrl());
//		System.out.println("\n Soap response:\n");
//		soapMessageReply.writeTo(System.out);
//		System.out.println();
		//final String textContent = soapMessageReply.getSOAPBody().getChildElement();
	
		//System.out.println( "SID = " + textContent);
		soapConnection.close();
		
		SOAPBody responseBody = soapMessageReply.getSOAPBody();

		String sid = null;
		
		
		final Node getSessionResponse = responseBody.getFirstChild();
		final Node returnResult = getSessionResponse.getFirstChild();

		final NodeList childNodes = returnResult.getChildNodes();
		sid = childNodes.item(5).getTextContent();		

		return sid;
	}
	
	private String login( final String sid, final String username, final String password) throws SOAPException, IOException {
		final SOAPMessage soapMessage = getSoapMessage();
		final SOAPBody soapBody = soapMessage.getSOAPBody();
		final SOAPElement loginElement = soapBody.addChildElement("loginUser", NAMESPACE_PREFIX);

		loginElement.addChildElement("SID", NAMESPACE_PREFIX).addTextNode(sid);
		loginElement.addChildElement("username", NAMESPACE_PREFIX).addTextNode(username);
		loginElement.addChildElement("userpass", NAMESPACE_PREFIX).addTextNode(password);

//		System.out.println("\nLOGIN REQUEST:\n");
//		soapMessage.writeTo(System.out);
//		System.out.println();
		
		soapMessage.saveChanges();

		final SOAPConnection soapConnection = getSoapConnection();
		final SOAPMessage soapMessageReply = soapConnection.call(soapMessage, getUserServiceUrl());
		final String textContent = soapMessageReply.getSOAPBody().getFirstChild().getTextContent();

//		System.out.println("\nLOGIN RESPONSE:\n");
//		soapMessageReply.writeTo(System.out);
//		System.out.println();
//		
//		System.out.println( "LOGIN =  " + textContent);
//		
		if( !textContent.equals("1")	)
			JOptionPane.showMessageDialog(null,OpenmeetingsPluginActivator.resourceService.
								getI18NString("plugin.openmeetings.ERROR_LOGIN_MSG"));
	
		soapConnection.close();

		return textContent;
	}
	public String getInvitationHash( final String username, final String password, final String displayedName ) throws Exception {
		final SOAPMessage soapMessage = getSoapMessage();
		final SOAPBody soapBody = soapMessage.getSOAPBody();
		final SOAPElement requestElement = soapBody.addChildElement("getInvitationHash", NAMESPACE_PREFIX);

		String sid = getSID( username, password );
		String error_id = null;
		try {
			error_id = login( sid, username, password);
		} catch (Exception e) {
			System.out.println( e.getMessage() );
		}		
		
		if( !error_id.equals( "1" ) ){
			System.out.println("User cant login!");
			return null;
		}
			
		String room_id = getAvailableRooms( sid );
		
		requestElement.addChildElement("SID", NAMESPACE_PREFIX).addTextNode(sid);
		requestElement.addChildElement("username", NAMESPACE_PREFIX).addTextNode( displayedName );
		requestElement.addChildElement("room_id", NAMESPACE_PREFIX).addTextNode(room_id);
				
		soapMessage.saveChanges();

//		System.out.println("\nGET INVITATION REQUEST:\n");
//		soapMessage.writeTo(System.out);
//		System.out.println();
		
		final SOAPConnection soapConnection = getSoapConnection();
		final SOAPMessage soapMessageReply = soapConnection.call(soapMessage, getJabberServiceUrl());
		
//		System.out.println("\nGET INVITATION RESPONSE:\n");
//		soapMessageReply.writeTo(System.out);
//		System.out.println();
		
		final String textContent = soapMessageReply.getSOAPBody().getFirstChild().getTextContent();

//		System.out.println( "INVITATION RESPONSE =  " + textContent);
		soapConnection.close();		

		return textContent;
	}
	
	private String getAvailableRooms( final String sid ) throws SOAPException, IOException, TransformerException {
		final SOAPMessage soapMessage = getSoapMessage();
		final SOAPBody soapBody = soapMessage.getSOAPBody();
			
		final SOAPElement elemCodeElement = soapBody.addChildElement("getAvailableRooms",NAMESPACE_PREFIX);

		elemCodeElement.addChildElement("SID", "rooms").addTextNode(sid);
		
//		System.out.println("\nGET_AVAILABLE_ROOMS REQUEST:\n");
//		soapMessage.writeTo(System.out);
//		System.out.println();
		
		soapMessage.saveChanges();

		final SOAPConnection soapConnection = getSoapConnection();
		final SOAPMessage soapMessageReply = soapConnection.call(soapMessage, getJabberServiceUrl());
		final String textContent = soapMessageReply.getSOAPBody().getTextContent();

//		System.out.println("\nGET_AVAILABLE_ROOMS RESPONSE:\n");
//		soapMessageReply.writeTo(System.out);
//		System.out.println();
		
		
		final Node getRoomsResponse = soapMessageReply.getSOAPBody();
		final Node getFirstRoomResult = getRoomsResponse.getFirstChild().getFirstChild();

		String rooms_id = new String();
		final NodeList childNodes = getFirstRoomResult.getChildNodes();
		int count = childNodes.getLength();
		for( int i = 0; i < count; ++i ){
		    String nodeName = childNodes.item( i ).getNodeName();		   
		    if( nodeName.contains("rooms_id")){		      
		        rooms_id = childNodes.item(i).getTextContent();
		    }		        
		}
						
//		System.out.println( "GET_AVAILABLE_ROOMS RESULT =  " + rooms_id );
		soapConnection.close();
		
		return rooms_id;
	}


	private String getErrorCode( final String sid, final String error_id ) throws SOAPException, IOException {
		final SOAPMessage soapMessage = getSoapMessage();
		final SOAPBody soapBody = soapMessage.getSOAPBody();
		final SOAPElement errorCodeElement = soapBody.addChildElement("getErrorByCode", NAMESPACE_PREFIX);

		errorCodeElement.addChildElement("SID", NAMESPACE_PREFIX).addTextNode(sid);
		errorCodeElement.addChildElement("errorid", NAMESPACE_PREFIX).addTextNode(error_id);
		errorCodeElement.addChildElement("language_id", NAMESPACE_PREFIX).addTextNode("0");

//		System.out.println("\nERROR CODE REQUEST:\n");
//		soapMessage.writeTo(System.out);
//		System.out.println();
		
		soapMessage.saveChanges();

		final SOAPConnection soapConnection = getSoapConnection();
		final SOAPMessage soapMessageReply = soapConnection.call(soapMessage, getUserServiceUrl());
		final String textContent = soapMessageReply.getSOAPBody().getFirstChild().getTextContent();

//		System.out.println("\nERROR CODE RESPONSE:\n");
//		soapMessageReply.writeTo(System.out);
//		System.out.println();
//		
//		System.out.println( "ERROR RESULT =  " + textContent);
		soapConnection.close();
		
		return textContent;
	}

	private SOAPConnection getSoapConnection() throws UnsupportedOperationException, SOAPException {
		
		final SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		final SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		return soapConnection;
	}
	
	private SOAPMessage getSoapMessage() throws SOAPException {
		final MessageFactory messageFactory = javax.xml.soap.MessageFactory.newInstance();
		final SOAPMessage soapMessage = messageFactory.createMessage();

		// Object for message parts
		final SOAPPart soapPart = soapMessage.getSOAPPart();
		final SOAPEnvelope envelope = soapPart.getEnvelope();

		envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
		envelope.addNamespaceDeclaration("xsd", "http://basic.beans.data.app.openmeetings.org/xsd");
		envelope.addNamespaceDeclaration("xsd", "http://basic.beans.persistence.app.openmeetings.org/xsd");
		envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		envelope.addNamespaceDeclaration("enc", "http://schemas.xmlsoap.org/soap/encoding/");
		envelope.addNamespaceDeclaration("env", "http://schemas.xmlsoap.org/soap/envelop/");
		

		envelope.addNamespaceDeclaration(NAMESPACE_PREFIX, "http://services.axis.openmeetings.org");
		envelope.addNamespaceDeclaration( "rooms", "http://rooms.beans.persistence.app.openmeetings.org/xsd");

		envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

		return soapMessage;
	}
	
	private void addSID( final String sid, final SOAPMessage soapMessage) 
	throws SOAPException {
		final SOAPHeader header = soapMessage.getSOAPHeader();
		final SOAPElement sidHeader = header.addChildElement("SID", NAMESPACE_PREFIX);
		sidHeader.addChildElement("SID", NAMESPACE_PREFIX).addTextNode(sid);
	}


	
	public void setServer( String serverUrl_ ){		
		serverUrl = serverUrl_;		
	}
	
	private String getServer(){
		return serverUrl;
	}
	
	private String getUserServiceUrl(){		
		String url = "http://" + getServer() + "/openmeetings/services/UserService?wsdl";
		System.out.println( "URL = " + url);
		return url;
	}
	private String getJabberServiceUrl(){		
		String url = "http://" + getServer() + "/openmeetings/services/JabberService?wsdl";
		System.out.println( "URL = " + url);
		return url;
	}
}