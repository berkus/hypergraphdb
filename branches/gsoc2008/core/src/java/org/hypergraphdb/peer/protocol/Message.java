package org.hypergraphdb.peer.protocol;

import java.util.UUID;

public class Message
{
	public static final String REMEMBER_ACTION = "remeber";
	
	private Performative performative;
	private String action;
	private UUID conversationId;
	private UUID taskId;
	private String replyTo;
	private Object content;
	
	public Message(Performative performative, String action)
	{
		this.performative = performative;
		this.action = action;
		this.conversationId = new UUID(0L, 0L);
	}
	public Message(Performative performative, String action, UUID conversationId)
	{
		this.performative = performative;
		this.action = action;
		this.conversationId = conversationId;
	}

	public Message(String action, UUID conversationId)
	{
		this.action = action;
		this.conversationId = conversationId;
	}

	public Performative getPerformative()
	{
		return performative;
	}

	public void setPerformative(Performative performative)
	{
		this.performative = performative;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}
	
	
	public UUID getConversationId()
	{
		return conversationId;
	}
	public void setConversationId(UUID conversationId)
	{
		this.conversationId = conversationId;
	}	
	
	public String getReplyTo()
	{
		return replyTo;
	}
	public void setReplyTo(String replyTo)
	{
		this.replyTo = replyTo;
	}
	public String toString()
	{
		String result = "Perf: " + performative + "; Action: " + action + "; Conversation: " + conversationId + "; Task: " + taskId;
		result += ";\nReplyTo: " + replyTo + "; Content: " + content;
		
		return result;
	}
	public Object getContent()
	{
		return content;
	}
	public void setContent(Object content)
	{
		this.content = content;
	}
	public UUID getTaskId()
	{
		return taskId;
	}
	public void setTaskId(UUID taskId)
	{
		this.taskId = taskId;
	}
	
}
