package net.frontlinesms.plugins.sync;

import static org.mockito.Mockito.*;

import java.util.LinkedHashMap;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.junit.BaseTestCase;

/**
 * Unit tests for {@link MessageSyncher}
 * @author Emmanuel Kala <emmanuel(at)ushahidi.com>
 *
 */
public class MessageSyncherTest extends BaseTestCase {
	public void testGetHttpRequestURL() {
		// Given the syncher has been set up
		MessageSyncher syncher = new MessageSyncher("http://example.com", new LinkedHashMap<String, String>() {
			{
				this.put("secretKey", "ABC123");
				this.put("m", "${message_content}");
				this.put("s", "${sender_number}");
			}
		});
		FrontlineMessage message = mock(FrontlineMessage.class);
		when(message.getTextContent()).thenReturn("Hello my friend");
		when(message.getSenderMsisdn()).thenReturn("+254720123456");
		
		{
			// When a GET request is specified
			syncher.setRequestMethod("GET");
			
			// Then a GET URL is generated
			String url = syncher.getHttpRequestURL(message);
			assertEquals("http://example.com?secretKey=ABC123&m=Hello+my+friend&s=%2B254720123456", url);
		}
		
		{
			// When a POST request is specified
			syncher.setRequestMethod("POST");
			
			// Then the base URL is returned
			String url = syncher.getHttpRequestURL(message);
			assertEquals("http://example.com", url);
		}
	}
}
