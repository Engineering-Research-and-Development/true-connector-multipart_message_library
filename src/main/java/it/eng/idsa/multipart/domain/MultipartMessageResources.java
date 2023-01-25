package it.eng.idsa.multipart.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.fraunhofer.iais.eis.Message;
import it.eng.idsa.multipart.processor.MultipartMessageProcessor;
import it.eng.idsa.multipart.util.UtilMessageService;

public class MultipartMessageResources {
	private Map<String, String> httpHeaders = new HashMap<>();
	private Map<String, String> headerHeader = new HashMap<>();
	private Message headerContent = null;
	private Map<String, String> payloadHeader = new HashMap<>();
	private String payloadContent = null;
	private Map<String, String> signatureHeader= new HashMap<>();
	private String signatureContent = null;
	private String token = null;
	
	public MultipartMessageResources() {
		super();
	}
	
	public MultipartMessageResources(Map<String, String> httpHeaders, Map<String, String> headerHeader, Message headerContent,
			Map<String, String> payloadHeader, String payloadContent, Map<String, String> signatureHeader,
			String signatureContent, String token) {
		super();
		this.httpHeaders = httpHeaders;
		this.headerHeader = headerHeader;
		this.headerContent = headerContent;
		this.payloadHeader = payloadHeader;
		this.payloadContent = payloadContent;
		this.signatureHeader = signatureHeader;
		this.signatureContent = signatureContent;
		this.token = token;
	}

	public Map<String, String> getHttpHeaders() {
		return httpHeaders;
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public Map<String, String> getHeaderHeader() {
		return headerHeader;
	}

	public void setHeaderHeader(Map<String, String> headerHeader) {
		this.headerHeader = headerHeader;
	}

	public Message getHeaderContent() {
		return headerContent;
	}

	public void setHeaderContent(Message headerContent) {
		this.headerContent = headerContent;
	}

	public Map<String, String> getPayloadHeader() {
		return payloadHeader;
	}

	public void setPayloadHeader(Map<String, String> payloadHeader) {
		this.payloadHeader = payloadHeader;
	}

	public String getPayloadContent() {
		return payloadContent;
	}

	public void setPayloadContent(String payloadContent) {
		this.payloadContent = payloadContent;
	}

	public Map<String, String> getSignatureHeader() {
		return signatureHeader;
	}

	public void setSignatureHeader(Map<String, String> signatureHeader) {
		this.signatureHeader = signatureHeader;
	}

	public String getSignatureContent() {
		return signatureContent;
	}

	public void setSignatureContent(String signatureContent) {
		this.signatureContent = signatureContent;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * Serialize Message object to JsonLD format
	 */
	public String getHeaderContentString() {
		try {
			// return new Serializer().serializePlainJson(messageResources.getHeaderContent());
			return MultipartMessageProcessor.serializeToJsonLD(getHeaderContent());
		} catch (IOException e) {
			//TODO: throw exception
			return "";
		} 
	}
	
	/*
	 * Two messages are equals if the: headerContent, payloadContent and signatureContent are equals.
	 */
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		MultipartMessageResources multipartMessage = (MultipartMessageResources) obj;
		
		return new EqualsBuilder()
				.append(true, isHeaderContentEquals(multipartMessage.getHeaderContent()))
				.append(this.payloadContent, multipartMessage.getPayloadContent())
				.append(this.signatureContent, multipartMessage.getSignatureContent())
				.append(this.token, multipartMessage.getToken())
				.isEquals();
	}
	
	// TODO: check this in the documentation: This should be adapted to the every new version of the: de.fraunhofer.iais.eis.Message
		// Problem is on the Fraunhofer: In the class is not implemented method equals for the de.fraunhofer.iais.eis.Message
		private boolean isHeaderContentEquals(Message headerContent) {
			return new EqualsBuilder()
					.append(getHeaderContent().getContentVersion(), headerContent.getContentVersion())
					.append(getHeaderContent().getCorrelationMessage(), headerContent.getCorrelationMessage())
					.append(getHeaderContent().getIssued(), headerContent.getIssued())
					.append(getHeaderContent().getIssuerConnector(), headerContent.getIssuerConnector())
					.append(getHeaderContent().getModelVersion(), headerContent.getModelVersion())
					.append(getHeaderContent().getRecipientAgent(), headerContent.getRecipientAgent())
					.append(getHeaderContent().getRecipientConnector(), headerContent.getRecipientConnector())
					.append(getHeaderContent().getSenderAgent(), headerContent.getSenderAgent())
					.append(getHeaderContent().getTransferContract(), headerContent.getTransferContract())
					.append(getHeaderContent().getId(), headerContent.getId())
					.isEquals();
		}
		
		public final int hashCode() {
			return new HashCodeBuilder()
					.append(this.headerContent)
					.append(this.payloadContent)
					.append(this.signatureContent)
					.append(this.token)
					.toHashCode();
		}
	
	public void clean(){
		if(this.httpHeaders != null) {
			this.httpHeaders.clear();
		}
		if(this.headerHeader != null) {
			this.headerHeader.clear();
		}
		if(this.headerContent != null) {
			this.headerContent = UtilMessageService.getArtifactRequestMessage();
		}
		if(this.payloadHeader != null) {
			this.payloadHeader.clear();
		}
		if(this.payloadContent != null) {
			this.payloadContent = "DUMMY";
		}
		if(this.signatureHeader != null) {
			this.signatureHeader.clear();
		}
		if(this.signatureContent != null) {
			this.signatureContent = "DUMMY";
		}
		if(this.token != null) {
			this.token = "DUMMY";
		}
	}
}
