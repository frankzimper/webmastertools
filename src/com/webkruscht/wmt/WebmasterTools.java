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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.client.webmastertools.WebmasterToolsService;
import com.google.gdata.data.webmastertools.SitesEntry;
import com.google.gdata.data.webmastertools.SitesFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.RedirectRequiredException;
import com.google.gdata.util.ServiceException;

/**
 * Methods for downloading Webmaster Tools search queries data
 */
public class WebmasterTools {
	
	WebmasterToolsService svc;

	String username = null;
	String password = null;
	String host = "www.google.com";
	String app_name = "Webkruscht-WMT";
	String dl_list_url = "/webmasters/tools/downloads-list?hl=%s&siteUrl=%s";
	String sites_path = "/webmasters/tools/feeds/sites/";
	// language for Webmaster Tools downloads - determines number format
	String lang = "en";
	
	/**
	 * Constructor which expects the Google credentials to login
	 * 
	 * @param user
	 * @param password
	 * @throws AuthenticationException
	 */
	public WebmasterTools(String user, String password) throws AuthenticationException {
		this.username = user;
		this.password = password;
		init();
	}
	
	/**
	 * Initialization and login to Webmaster Tools account
	 * @throws AuthenticationException
	 */
	private void init() throws AuthenticationException {
		svc = new WebmasterToolsService("Webkruscht-WMT");
		svc.setUserCredentials(username, password);		
	}
	
	private URL _getUrl(String path) throws MalformedURLException {
		return new URL("https://" + host + path);
	}

	/**
	 * Get a list of available search data downloads for a particular site
	 * @param site
	 * @return HashMap of available download paths
	 * @throws Exception
	 */
	public JSONObject getDownloadList(SitesEntry site) throws Exception {
		JSONObject data = null;
		URL url = _getUrl(String.format(dl_list_url, lang, site.getTitle().getPlainText()));
		GDataRequest req = svc.createRequest(RequestType.QUERY, url, ContentType.JSON);
		try { 
			req.execute();
			data = (JSONObject)JSONValue.parse(new InputStreamReader(req.getResponseStream()));
		} catch (RedirectRequiredException e) {
			return null;
		}
		return data;
	}
	
	/**
	 * Download search query data and write it to out
	 * @param path
	 * @param out
	 * @throws Exception
	 */
	public void downloadData(String path, OutputStreamWriter out) throws Exception {
		String data;
		URL url = _getUrl(path);
		GDataRequest req = svc.createRequest(RequestType.QUERY, url, ContentType.TEXT_PLAIN);
		req.execute();
		BufferedReader in = new BufferedReader(new InputStreamReader(req.getResponseStream()));
		while ((data = in.readLine()) != null) {
			out.write(data + "\n");
		}
	}
	
	/**
	 * Get list of registered sites from the user's account
	 * @return
	 * @throws ServiceException
	 * @throws IOException
	 */
	public List<SitesEntry> getUserSites()
		    throws ServiceException, IOException {
		  try {
		    // Request the feed
		    SitesFeed sitesResultFeed = svc.getFeed(_getUrl(sites_path), SitesFeed.class);

			return sitesResultFeed.getEntries();
		  } catch (MalformedURLException e) {
		    throw new IOException("URL for sites feed is malformed.");
		  }
		}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}	
}
