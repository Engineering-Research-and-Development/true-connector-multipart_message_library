package it.eng.idsa.multipart.domain;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.util.Map;

import de.fraunhofer.iais.eis.Message;
import it.eng.idsa.multipart.processor.MultipartMessageProcessor;

/**
 * 
 * @author Milan Karajovic and Gabriele De Luca
 *
 */

/**
 * Type of the MultipartMessage.
 */
public class MultipartMessage implements AutoCloseable{
	
	private static final Cleaner cleaner = Cleaner.create();
    private final Cleaner.Cleanable cleanable;

    public MultipartMessage() {
        this.messageResources = new MultipartMessageResources();
        this.cleanable = cleaner.register(this, cleaner(messageResources));
    }

    @Override
    public void close() throws Exception {
        cleanable.clean();
    }

    private static Runnable cleaner(MultipartMessageResources resource) {
        return () -> {
            // Perform cleanup actions
            resource.clean();
        };
    }
	
	private MultipartMessageResources messageResources;
	
	public MultipartMessage(Map<String, String> httpHeaders, Map<String, String> headerHeader, Message headerContent,
			Map<String, String> payloadHeader, String payloadContent, Map<String, String> signatureHeader,
			String signatureContent, String token) {
		super();
		this.messageResources = new MultipartMessageResources(httpHeaders, headerHeader, headerContent, payloadHeader,
				payloadContent, signatureHeader, signatureContent, token);
		this.cleanable = cleaner.register(this, cleaner(messageResources));
	}
	

	public Map<String, String> getHttpHeaders() {
		return messageResources.getHttpHeaders();
	}
	
	public String getToken() {
		return messageResources.getToken();
	}
	
	/**
	 * Serialize Message object to JsonLD format
	 */
	public String getHeaderContentString() {
		try {
			// return new Serializer().serializePlainJson(messageResources.getHeaderContent());
			return MultipartMessageProcessor.serializeToJsonLD(messageResources.getHeaderContent());
		} catch (IOException e) {
			//TODO: throw exception
			return "";
		} 
	}

	/*
	 * Two messages are equals if the: headerContent, payloadContent and signatureContent are equals.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		
		MultipartMessage multipartMessage = (MultipartMessage) obj;
		return
			   (messageResources.getHeaderContent() == null) ? (messageResources.getHeaderContent() == multipartMessage.getHeaderContent()) : isHeaderContentEquqls(multipartMessage.getHeaderContent()) &&
			   (messageResources.getPayloadContent() == null) ? (messageResources.getPayloadContent() == multipartMessage.getPayloadContent()) : (messageResources.getPayloadContent().equals(multipartMessage.getPayloadContent())) &&
			   (messageResources.getSignatureContent() == null) ? (messageResources.getSignatureContent()  == multipartMessage.getSignatureContent()) : (messageResources.getSignatureContent() .equals(multipartMessage.getSignatureContent()));
	}
	
	@Override
	public final int hashCode() {
		
		int result = 17;
		if(messageResources.getHeaderContent() != null) {
			result = 31 * result + messageResources.getHeaderContent().hashCode();
		}
		if(messageResources.getPayloadContent() != null) {
			result = 31 * result + messageResources.getPayloadContent().hashCode();
		}
		if(messageResources.getSignatureContent()  != null) {
			result = 31 * result + messageResources.getSignatureContent() .hashCode();
		}
		
		return result;
	}
	
	public Map<String, String> getHeaderHeader() {
		return messageResources.getHeaderHeader();
	}

	public Message getHeaderContent() {
		return messageResources.getHeaderContent();
	}

	public Map<String, String> getPayloadHeader() {
		return messageResources.getPayloadHeader();
	}

	public String getPayloadContent() {
		return messageResources.getPayloadContent();
	}

	public Map<String, String> getSignatureHeader() {
		return messageResources.getSignatureHeader();
	}

	public String getSignatureContent() {
		return messageResources.getSignatureContent();
	}
	
	// TODO: check this in the documentation: This should be adapted to the every new version of the: de.fraunhofer.iais.eis.Message
	// Problem is on the Fraunhofer: In the class is not implemented method equals for the de.fraunhofer.iais.eis.Message
	private boolean isHeaderContentEquqls(Message headerContent) {
		return (messageResources.getHeaderContent().getContentVersion() == null) ? (messageResources.getHeaderContent().getContentVersion() == headerContent.getContentVersion()) : (messageResources.getHeaderContent().getContentVersion().equals(headerContent.getContentVersion())) &&
			   (messageResources.getHeaderContent().getCorrelationMessage() == null) ? (messageResources.getHeaderContent().getCorrelationMessage() == headerContent.getCorrelationMessage()) : (messageResources.getHeaderContent().getCorrelationMessage().equals(headerContent.getCorrelationMessage())) &&
			   (messageResources.getHeaderContent().getIssued() == null) ? (messageResources.getHeaderContent().getIssued() == headerContent.getIssued()) : (messageResources.getHeaderContent().getIssued().equals(headerContent.getIssued())) &&
			   (messageResources.getHeaderContent().getIssuerConnector() == null) ? (messageResources.getHeaderContent().getIssuerConnector() == headerContent.getIssuerConnector()) : (messageResources.getHeaderContent().getIssuerConnector().equals(headerContent.getIssuerConnector())) &&
			   (messageResources.getHeaderContent().getModelVersion() == null) ? (messageResources.getHeaderContent().getModelVersion() == headerContent.getModelVersion()) : (messageResources.getHeaderContent().getModelVersion().equals(headerContent.getModelVersion())) &&
			   (messageResources.getHeaderContent().getRecipientAgent() == null) ? (messageResources.getHeaderContent().getRecipientAgent() == headerContent.getRecipientAgent()) : (messageResources.getHeaderContent().getRecipientAgent().equals(headerContent.getRecipientAgent())) &&
			   (messageResources.getHeaderContent().getRecipientConnector() == null) ? (messageResources.getHeaderContent().getRecipientConnector() == headerContent.getRecipientConnector()) : (messageResources.getHeaderContent().getRecipientConnector().equals(headerContent.getRecipientConnector())) &&
			   (messageResources.getHeaderContent().getSenderAgent() == null) ? (messageResources.getHeaderContent().getSenderAgent() == headerContent.getSenderAgent()) : (messageResources.getHeaderContent().getSenderAgent().equals(headerContent.getSenderAgent())) &&		   
			   (messageResources.getHeaderContent().getTransferContract() == null) ? (messageResources.getHeaderContent().getTransferContract() == headerContent.getTransferContract()) : (messageResources.getHeaderContent().getTransferContract().equals(headerContent.getTransferContract())) &&
			   (messageResources.getHeaderContent().getId() == null) ? (messageResources.getHeaderContent().getId() == headerContent.getId()) : (messageResources.getHeaderContent().getId().equals(headerContent.getId()));	   
	}
}

