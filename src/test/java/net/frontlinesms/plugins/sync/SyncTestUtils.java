/**
 * 
 */
package net.frontlinesms.plugins.sync;

import net.frontlinesms.data.domain.FrontlineMessage;

/**
 * @author ekala
 *
 */
public class SyncTestUtils {

	static FrontlineMessage createOutgoingMessage() {
		return FrontlineMessage.createOutgoingMessage(0, "123", "456", "hi");
	}

	static FrontlineMessage createIncomingMessage() {
		return FrontlineMessage.createIncomingMessage(0, "123", "456", "hi");
	}

}
