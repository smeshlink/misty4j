/**
 * 
 */
package com.smeshlink.misty.service.channel;

import java.util.EventListener;

import com.smeshlink.misty.command.CommandRequest;
import com.smeshlink.misty.command.CommandResponse;

/**
 * @author smeshlink
 *
 */
public interface ICommandListener extends EventListener {
	CommandResponse command(CommandRequest request);
}
