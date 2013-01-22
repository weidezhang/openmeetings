/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.test.backup;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;


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
					"see http://incubator.apache.org/openmeetings/Upgrade.html for Details \n" +
					"###############################################");
			
			Element root = document.addElement("root");
			
			Element organisations = root.addElement("organisations");
			
			Element organisation1 = organisations.addElement("organisation");
			
			organisation1.addElement("name").setText(formatString("org\u01e6\u03ce\u0677\u042b1"));
			organisation1.addElement("organisation_id").setText(formatString("1"));
			organisation1.addElement("deleted").addCDATA("false");
				
			Element organisation2 = organisations.addElement("organisation");
			
			organisation2.addElement("name").setText(formatString("org2"));
			organisation2.addElement("organisation_id").setText(formatString("2"));
			organisation2.addElement("deleted").setText(formatString("false"));
			
			for (@SuppressWarnings("unchecked")
			Iterator<Element> innerIter = organisations.elementIterator( "organisation" ); innerIter.hasNext(); ) {
    			
    			Element orgObject = innerIter.next();
    			
    			String name = orgObject.element("name").getText();
    			assertNotNull(name);
    			assertNotNull(orgObject.element("deleted").getText());
    			
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