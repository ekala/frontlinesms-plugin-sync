/**
 * 
 */
package net.frontlinesms.plugins.sync;

import java.util.List;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.data.repository.hibernate.HibernateMessageDao;

/**
 * @author ekala
 *
 */
public class SyncMessageDao {
	private final BaseHibernateDao<FrontlineMessage> messageDao;
	
	public SyncMessageDao(MessageDao messageDao) {
		assert messageDao instanceof HibernateMessageDao : "This plugin will not work with other implementations of MessageDao";
		this.messageDao = (HibernateMessageDao) messageDao;
	}

	public List<FrontlineMessage> getUnsynchronisedMessages(long lastId) {
		// Get all messages after the last id - HQL query
		String queryString = "from FrontlineMessage where id > ? and type = ? ORDER BY id ASC";
		
		Object[] params = new Object[]{lastId, FrontlineMessage.Type.RECEIVED};
		
		return getList(queryString, params);
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> getList(String hqlQuery, Object...values) {
		return this.messageDao.getHibernateTemplate().find(hqlQuery, values);
	}

}
