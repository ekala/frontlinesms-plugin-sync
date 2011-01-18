package net.frontlinesms.plugins.sync;

import java.lang.reflect.Field;

import net.frontlinesms.data.domain.FrontlineMessage;

public class SyncUtils {
	/** Sender name parameter */
	public static final String PARAM_SENDER_NAME = "{sender_name}";
	/** Sender number parameter */
	public static final String PARAM_SENDER_NUMBER = "{sender_number}";
	/** Message content parameter */
	public static final String PARAM_MESSAGE_CONTENT = "{message}";
	
	public static long getId(FrontlineMessage incomingMessage) {
		try {
			Field id = incomingMessage.getClass().getDeclaredField("id");
			id.setAccessible(true);
			return id.getLong(incomingMessage);
		} catch(Exception ex) {
			throw new RuntimeException("Problem getting message ID by reflecton.", ex);
		}
	}


}
