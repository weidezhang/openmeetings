package org.openmeetings.backup;

import static org.junit.Assert.assertNotSame;

import java.io.FileOutputStream;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.HTMLWriter;
import org.junit.Test;
import org.openmeetings.app.persistence.beans.domain.Organisation;


public class TestStringReplace {

	
	public void stringReplace() {
		
		String str = "alvaro&#3;@gmail.com";
		
		System.out.println(str);
		
		String result = this.formatString(str);
		
		System.out.println(result);
		
		assertNotSame(str, result);
		
	}
	
	private String formatString(String str) {
		str = "<![CDATA["+str+"]]>";
		return str;
	}
	
	@Test
	public void xmlReplace() {
		try {
			
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("UTF-8");
			document.addComment(
					"###############################################\n" +
					"This File is auto-generated by the Backup Tool \n" +
					"you should use the BackupPanel to modify or change this file \n" +
					"see http://code.google.com/p/openmeetings/wiki/BackupPanel for Details \n" +
					"###############################################");
			
			Element root = document.addElement("root");
			
			Element organisations = root.addElement("organisations");
			
			Element organisation1 = organisations.addElement("organisation");
			
			organisation1.addElement("name").setText(formatString("org������1"));
			organisation1.addElement("organisation_id").setText(formatString("1"));
			organisation1.addElement("deleted").addCDATA("false");
				
			Element organisation2 = organisations.addElement("organisation");
			
			organisation2.addElement("name").setText(formatString("org2"));
			organisation2.addElement("organisation_id").setText(formatString("2"));
			organisation2.addElement("deleted").setText(formatString("false"));
			
			for (Iterator<Element> innerIter = organisations.elementIterator( "organisation" ); innerIter.hasNext(); ) {
    			
    			Element orgObject = innerIter.next();
    			
    			String name = orgObject.element("name").getText();
    			String deleted = orgObject.element("deleted").getText();
    			
    			System.out.println(name);
    		}
			
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			outformat.setXHTML(true);
			outformat.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter(System.out, outformat);
			writer.write(document);
			writer.flush();
			writer.close();
			
			
		
		} catch (Exception err) {
			err.printStackTrace();
		}
	
	}
	
}
