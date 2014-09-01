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
package org.apache.openmeetings.core.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.openmeetings.util.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class LibraryChartLoader {

    private static final Logger log = new Logger();

    private static final String fileExt = ".xchart";

    private static LibraryChartLoader instance;

    private LibraryChartLoader() {
    }

    public static synchronized LibraryChartLoader getInstance() {
        if (instance == null) {
            instance = new LibraryChartLoader();
        }
        return instance;
    }

    @SuppressWarnings("rawtypes")
	public ArrayList loadChart(File dir, String fileName) {
    	BufferedReader reader = null;
        try {
            File file = new File(dir, fileName + fileExt);

            log.error("filepathComplete: " + file);

            XStream xStream = new XStream(new XppDriver());
            xStream.setMode(XStream.NO_REFERENCES);

            reader = new BufferedReader(new FileReader(file));
            ArrayList lMapList = (ArrayList) xStream.fromXML(reader);

            return lMapList;
        } catch (Exception err) {
            log.error(err);
        } finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
        }

        return null;
    }

}
