package net.frontlinesms.plugins.sync;

import java.lang.reflect.Field;

import net.frontlinesms.data.domain.FrontlineMessage;

public class SyncUtils {
	
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
