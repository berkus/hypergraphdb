package org.hypergraphdb.peer.workflow;

import java.util.HashMap;
import java.util.UUID;

import org.hypergraphdb.peer.PeerInterface;
import org.hypergraphdb.peer.PeerRelatedActivity;
import org.hypergraphdb.peer.protocol.Message;
import org.hypergraphdb.peer.protocol.Performative;
import org.hypergraphdb.util.Pair;

/**
 * @author Cipri Costa
 *
 * Superclass for conversation activities. The state transitions in a conversation are triggered in two ways: by 
 * explicitly calling a method that can "say" something or by receiving a message ("hearing" something)
 *
 * The conversation will always remember the last message that was said or heard as this is supposed to carry 
 * all the information that is needed about past messages.
 * 
 * This class allows implementors to declaratively set what type of message to except when the conversation is 
 * in a given state. If a message is received in a state and there are no defined transitions for that state and
 * the performative of the message, a "do not understand" reply is sent.
 * 
 * @param <StateType>
 */
public class Conversation<StateType> extends AbstractActivity<StateType>
{
	private static final UUID NULL_UUID = new UUID(0L, 0L);
	
	private PeerRelatedActivity sendActivity;
	private PeerInterface peerInterface;
	private Message msg;

	private HashMap<Pair<StateType, Performative>, StateType> performativeTransitions = new HashMap<Pair<StateType,Performative>, StateType>();
	
	public Conversation(PeerRelatedActivity sendActivity, PeerInterface peerInterface, Message msg, StateType start, StateType end)
	{
		if (msg.getConversationId().equals(NULL_UUID))
		{
			msg.setConversationId(UUID.randomUUID());
		}
		this.sendActivity = sendActivity;
		this.msg = msg;
		this.peerInterface = peerInterface;
		
		this.sendActivity.setTarget(msg.getReplyTo());
		
		setState(start);
	}


	protected void doRun()
	{
		
	}
	
	protected void registerPerformativeTransition(StateType fromState, Performative performative, StateType toState)
	{
		performativeTransitions.put(new Pair<StateType, Performative>(fromState, performative), toState);
	}

	public void handleIncomingMessage(Message msg)
	{
		StateType state = getState();
		
		Pair<StateType, Performative> key = new Pair<StateType, Performative>(state, msg.getPerformative());
		StateType newState = performativeTransitions.get(key);

		if ((newState != null) && compareAndSetState(state, newState))
		{
			//new state set
			this.msg = msg;
			stateChanged();
		}else{
			
			//TODO say don't understand
		}
	}

	protected void sendMessage()
	{
		sendActivity.setMessage(msg);
		peerInterface.execute(sendActivity);
	}
	
	public Message getMessage()
	{
		return msg;
	}

	public void setMessage(Message msg)
	{
		this.msg = msg;
	}
}
