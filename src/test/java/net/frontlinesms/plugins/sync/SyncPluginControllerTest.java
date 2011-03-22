/**
 * 
 */
package net.frontlinesms.plugins.sync;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.junit.BaseTestCase;
import net.frontlinesms.plugins.PluginInitialisationException;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SyncPluginController}
 * @author ekala
 *
 */
public class SyncPluginControllerTest extends BaseTestCase {
	public void testInit() throws PluginInitialisationException {
		// Given that the controller is set up
		SyncPluginController controller = new SyncPluginController();
		EventBus bus = mock(EventBus.class);
		
		{
			// When controller is not initialised
			
			// Then it is not registered as an event observer
			verify(bus, never()).registerObserver(any(EventObserver.class));
			
			// And no queue is initialised
			assertNull(controller.getQueueProcessor());
			
			// And no sync message DAO is initialised
			assertNull(controller.getSyncMessageDao());
		}
		
		{
			// When controller is initialised FIXME this proved quite complicated due to downcasting in the SyncMessageDao reacting badly to mocks
			//controller.init();
			
			// Then TODO it is registered as an event observer
			
			// And TODO a queue is created
			
			// And TODO a syncher is created
			
			// And TODO a sync message DAO is initialised, and TODO has been queried for messages
			
			// And TODO messages have been added to the queue
		}
	}
	
	public void testDeinit() {
		// Given that the controller is set up
		SyncPluginController controller = new SyncPluginController();
		QueueProcessor processor = mock(QueueProcessor.class);
		controller.setQueueProcessor(processor);
		EventBus bus = mock(EventBus.class);
		controller.setEventBus(bus);
		
		// When the controller is deinitialised
		controller.deinit();
		
		// Then it is deregistered as an event listener
		verify(bus).unregisterObserver(controller);
		// And it has stopped processing its queue
		verify(processor).stopProcessing();
	}
	
	@SuppressWarnings("unchecked")
	public void testNotify() {
		// Given the controller is set up
		SyncPluginController controller = new SyncPluginController();
		QueueProcessor qProcessor = mock(QueueProcessor.class);
		controller.setQueueProcessor(qProcessor);
		
		{
			// When an irrelevant notification is fired
			FrontlineEventNotification n = mock(FrontlineEventNotification.class);
			controller.notify(n);
			
			// Then nothing happens
			verify(qProcessor, never()).queue(any(FrontlineMessage.class));
		}
		
		{
			// When a EntitySavedNotification is fired for an irrelevant data type
			EntitySavedNotification n = mock(EntitySavedNotification.class);
			Contact contact = mock(Contact.class);
			when(n.getDatabaseEntity()).thenReturn(contact);
			controller.notify(n);
			
			// Then nothing happens
			verify(qProcessor, never()).queue(any(FrontlineMessage.class));
		}
		
		{
			// When a EntitySavedNotification is fired for a outoging message
			EntitySavedNotification n = mock(EntitySavedNotification.class);
			when(n.getDatabaseEntity()).thenReturn(SyncTestUtils.createOutgoingMessage());
			controller.notify(n);
			
			// Then nothing happens
			verify(qProcessor, never()).queue(any(FrontlineMessage.class));
		}
		
		{
			// When a EntitySavedNotification is fired for an incoing message
			EntitySavedNotification n = mock(EntitySavedNotification.class);
			FrontlineMessage message = SyncTestUtils.createIncomingMessage();
			when(n.getDatabaseEntity()).thenReturn(message);
			controller.notify(n);
			
			// Then the message is queued
			verify(qProcessor).queue(message);
		}
	}
}
