/**
 * 
 */
package net.frontlinesms.plugins.sync;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.domain.FrontlineMessage;

/**
 * @author ekala
 *
 */
public class MessageSyncher {
	private String synchronisationURL;
	private String requestMethod ;
	
	public MessageSyncher(String synchronisationURL) {
		setSynchronisationURL(synchronisationURL);
	}
	
	public MessageSyncher() { }
	
	/** Sets for the synchronisation URL @param synchronsationURL */
	public void setSynchronisationURL(String synchronisationURL) {
		this.synchronisationURL = synchronisationURL;
		
		// Extract URL parameters from the URL
	}

	boolean syncMessage(FrontlineMessage message) {
		Map<String, String> paramMap = createParamMap(message);
		return doPost(getUrl(), paramMap);
	}

	/** Sets the request method for the syncher */
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	
	/** Gets the request method for the syncher */
	public String getRequestMethod() {
		return requestMethod;
	}
	
	private boolean doPost(String url, Map<String, String> paramMap) {
		String data = buildRequestString(paramMap);
		
		HttpURLConnection conn = null;
		OutputStreamWriter out = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(requestMethod);
			
			out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.flush();
			
			return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (Exception e) {
			return false;
		} finally {
			try { out.close(); } catch(Exception ex) { /* ignore */ }
			try { conn.disconnect(); } catch(Exception ex) { /* ignore */ }
		}
	}

	private static String buildRequestString(Map<String, String> paramMap) {
		StringBuilder bob = new StringBuilder();
		for(Entry<String, String> e : paramMap.entrySet()) {
			bob.append('&');
			assert(!e.getKey().contains("="));
			bob.append(e.getKey());
			bob.append('=');
			bob.append(FrontlineUtils.urlEncode(e.getValue()));
		}
		return bob.length() > 0 ? bob.substring(1) : "";
	}

	private String getUrl() {
		return synchronisationURL;
	}

	private Map<String, String> createParamMap(FrontlineMessage message) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("sender", message.getSenderMsisdn());
		params.put("text", message.getTextContent());
		return params;
	}
}
