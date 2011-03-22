package net.frontlinesms.plugins.sync;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.junit.HibernateTestCase;
import net.frontlinesms.plugins.sync.SyncMessageDao;

/**
 * 
 */

/**
 * Integration tests for {@link SyncMessageDao}.
 * @author ekala
 */
public class SyncMessageDaoTest extends HibernateTestCase {
	@Autowired
	private MessageDao messageDao;
	private SyncMessageDao dao;
	
	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}
	
	public void testGetUnsynchronisedMessages() {
		{
			// Given there are no messages in the database
			dao = new SyncMessageDao(this.messageDao);
			
			// When I request all unsynchronised messages
			List<FrontlineMessage> messages = dao.getUnsynchronisedMessages(0);
			
			// Then I get an empty list
			assertEquals(0, messages.size());
		}

		{
			// Given there is one message in the database
			dao = new SyncMessageDao(this.messageDao);
			
			FrontlineMessage incomingMessage = SyncTestUtils.createIncomingMessage();
			messageDao.saveMessage(incomingMessage);
			
			long lastSyncId = SyncUtils.getId(incomingMessage);
			simpleTestGetUnsynchronisedMessages(lastSyncId);
			
			// Given there is an outgoing message in the database
			messageDao.saveMessage(SyncTestUtils.createOutgoingMessage());
			
			// Then there is no change to the messages fetched
			simpleTestGetUnsynchronisedMessages(lastSyncId);
		}
	}
	
	private void simpleTestGetUnsynchronisedMessages(long lastSyncId) {
		{
			// When I request all unsynchronised messages
			List<FrontlineMessage> messages = dao.getUnsynchronisedMessages(0);
			
			// Then I get my message
			assertEquals(1, messages.size());
		}
		
		{
			// When I request all unsynchronised messages after the last ID syncd
			List<FrontlineMessage> messages = dao.getUnsynchronisedMessages(lastSyncId);
			
			// Then I get an empty list
			assertEquals(0, messages.size());
		}
		
		{
			// When I request all unsynchronised messages after a very big ID
			List<FrontlineMessage> messages = dao.getUnsynchronisedMessages(Long.MAX_VALUE);
			
			// Then I get an empty list
			assertEquals(0, messages.size());
		}
	}
}
