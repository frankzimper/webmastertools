/* 
 * Copyright (c) 2011 Frank Zimper
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webkruscht.wmt;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.json.simple.JSONObject;

import com.google.gdata.data.webmastertools.SitesEntry;

/**
 * 
 */
public class DownloadFiles {

	// Types of search query data to download. Remove unneeded
	private static ArrayList<String> props = new ArrayList<String>();
	static {
		props.add("ALL");
		props.add("WEB");
		props.add("MOBILE_RESTRICT");
		props.add("MOBILE_SMARTPHONE");
		props.add("IMAGE");
		props.add("VIDEO");
	}
	
	private static String username;
	private static String password;
	private static String filePath;
	
	private static void getProperties() throws IOException {
		Properties p = new Properties();
		String propFile = "wmt.properties";
		InputStream propStream = DownloadFiles.class.getClassLoader().getResourceAsStream(propFile);
		p.load(propStream);
		propStream.close();
		username = p.getProperty("username");
		password = p.getProperty("password");
		filePath = p.getProperty("filePath");
	}
	
	private static Options getOptions(String[] argv)
			throws NumberFormatException {
		int c;
		boolean error;
		Options ret;
		Calendar cal;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

		ret = new Options();
		String arg;
		LongOpt[] longopts = new LongOpt[3];
		//
		StringBuffer sb = new StringBuffer();
		longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("lastmonth", LongOpt.OPTIONAL_ARGUMENT, sb,
				'm');
		longopts[2] = new LongOpt("days", LongOpt.OPTIONAL_ARGUMENT, sb, 'd');

		//
		Getopt g = new Getopt("DownloadFiles", argv, "", longopts);
		g.setOpterr(false); // We'll do our own error handling
		//
		while ((c = g.getopt()) != -1)
			switch (c) {
			case 0:
				arg = g.getOptarg();
				switch ((char) (new Integer(sb.toString())).intValue()) {
				case 'd':
					ret.setDays(Integer.parseInt(arg));
				case 'm':
					cal = GregorianCalendar.getInstance();
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					ret.setEnddate(df.format(cal.getTime()));
					cal.set(Calendar.DAY_OF_MONTH, 1);
					ret.setStartdate(df.format(cal.getTime()));
				}
				break;
			}
		;
		return ret;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		WebmasterTools wmt;
		String filename;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String today = sdf.format(date);
		getProperties();
		Options options = getOptions(args);
		
		try {
			wmt = new WebmasterTools(username, password);
			
			for (SitesEntry entry : wmt.getUserSites()) {
				// only process verified sites
				if (entry.getVerified()) {
					// get download paths for site
					JSONObject data = wmt.getDownloadList(entry);
					if (data != null) {
						for (String prop: props) {
							String path = (String)data.get("TOP_QUERIES");
							path += "&prop=" + prop;
							URL url = new URL(entry.getTitle().getPlainText());
							if (options.getStartdate() != null) {
								path += "&db=" + options.getStartdate();
								path += "&de=" + options.getEnddate();
								filename = String.format("%s-%s-%s-%s-%s.csv", url.getHost(), options.getStartdate(), options.getEnddate(), prop, "TopQueries");
							} else {
								filename = String.format("%s-%s-%s-%s.csv", url.getHost(), today, prop, "TopQueries");
							}
							OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath + filename), "UTF-8");
							wmt.downloadData(path, out);
							out.close();
						}
						String path = (String)data.get("TOP_PAGES");
						URL url = new URL(entry.getTitle().getPlainText());
						filename = String.format("%s-%s-%s.csv", url.getHost(), today, "TopQueries");
						OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filePath + filename), "UTF-8");
						wmt.downloadData(path, out);
						out.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

}
