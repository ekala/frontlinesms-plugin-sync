/**
 * 
 */
package net.frontlinesms.plugins.sync;

import java.lang.reflect.Field;
import java.util.LinkedList;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.junit.BaseTestCase;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link QueueProcessor}.
 * @author ekala
 *
 */
public class QueueProcessorTest extends BaseTestCase {
	public void testProcessMessage() {
		// Given the processor is set up
		QueueProcessor processor = new QueueProcessor();
		MessageSyncher syncher = mock(MessageSyncher.class);
		processor.setMessageSyncher(syncher);
		LinkedList<FrontlineMessage> q = new LinkedList<FrontlineMessage>();
		setMessageQueue(processor, q);
		
		{
			// When a null message is processed
			processor.processMessage(null);
			
			// Then nothing happens
			assertTrue(q.isEmpty());
		}

		FrontlineMessage badMessage = mock(FrontlineMessage.class);
		{
			// When a message is processed and it fails
			when(syncher.syncMessage(badMessage)).thenReturn(false);
			processor.processMessage(badMessage);
			
			// Then it is re-added to the queue
			assertTrue(q.contains(badMessage));
		}
		
		{
			// When a message is processed and it succeeds
			FrontlineMessage goodMessage = mock(FrontlineMessage.class);
			when(syncher.syncMessage(goodMessage)).thenReturn(true);
			processor.processMessage(goodMessage);
			
			// Then it is no added to the queue
			assertTrue(q.contains(badMessage));
			assertFalse(q.contains(goodMessage));
		}
	}

	/**
	 * This is done via reflection to avoid making the queue field accessible in normal code.
	 * @param processor
	 * @param q
	 */
	private void setMessageQueue(QueueProcessor processor,
			LinkedList<FrontlineMessage> q) {
		try {
			Field qField = processor.getClass().getDeclaredField("queue");
			qField.setAccessible(true);
			qField.set(processor, q);
		} catch(Exception ex) {
			throw new RuntimeException("Problem setting QueueProcessor's message queue", ex);
		}
	}
}
