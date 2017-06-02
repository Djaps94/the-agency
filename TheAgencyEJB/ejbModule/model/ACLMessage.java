package model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ACLMessage implements Serializable{
	
	private static final long serialVersionUID = -361951712504554701L;
	
	private Performative performative;
	private AID sender;
	private List<AID> recievers;
	private AID replyTo;
	private String content;
	private Object contentObject;
	private Map<String, Object> userArgs;
	private String language;
	private String encoding;
	private String ontology;
	private String protocol;
	private String conversationId;
	private String replyWith;
	private String inReplyTo;
	private Long replyBy;
	
	private enum Performative { ACCEPT_PROPOSAL,
							    AGREE,
							    CANCEL,
							    CFP,
							    CONFIRM,
							    DISCONFIRM,
							    FAILURE,
							    INFORM,
							    INFORM_IF,
							    INFORM_REF,
							    NOT_UNDERSTOOD,
							    PROPAGATE,
							    PROPOSE,
							    PROXY,
							    QUERY_IF,
							    QUERY_REF,
							    REFUSE,
							    REJECT_PROPOSAL,
							    REQUEST,
							    REQUEST_WHEN,
							    REQUEST_WHENEVER,
							    SUBSCRIBE} ;
	
	public ACLMessage() { }
		
	public AID getSender() {
		return sender;
	}
	public void setSender(AID sender) {
		this.sender = sender;
	}
	public List<AID> getRecievers() {
		return recievers;
	}
	public void setRecievers(List<AID> recievers) {
		this.recievers = recievers;
	}
	public AID getReplyTo() {
		return replyTo;
	}
	public void setReplyTo(AID replyTo) {
		this.replyTo = replyTo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Object getContentObject() {
		return contentObject;
	}
	public void setContentObject(Object contentObject) {
		this.contentObject = contentObject;
	}
	public Map<String, Object> getUserArgs() {
		return userArgs;
	}
	public void setUserArgs(Map<String, Object> userArgs) {
		this.userArgs = userArgs;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getOntology() {
		return ontology;
	}
	public void setOntology(String ontology) {
		this.ontology = ontology;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	public String getReplyWith() {
		return replyWith;
	}
	public void setReplyWith(String replyWith) {
		this.replyWith = replyWith;
	}
	public String getInReplyTo() {
		return inReplyTo;
	}
	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
	public Long getReplyBy() {
		return replyBy;
	}
	public void setReplyBy(Long replyBy) {
		this.replyBy = replyBy;
	}

	public Performative getPerformative() {
		return performative;
	}

	public void setPerformative(Performative performative) {
		this.performative = performative;
	}
}
