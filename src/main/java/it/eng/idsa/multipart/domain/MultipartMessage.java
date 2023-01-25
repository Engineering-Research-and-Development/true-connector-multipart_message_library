package it.eng.idsa.multipart.domain;

import java.lang.ref.Cleaner;
import java.util.Map;

import de.fraunhofer.iais.eis.Message;

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
    private MultipartMessageResources messageResources;

    public MultipartMessage() {
        this.messageResources = new MultipartMessageResources();
        this.cleanable = cleaner.register(this, cleaner(messageResources));
    }

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
	
	/**
	 * Serialize Message object to JsonLD format
	 */
	public String getHeaderContentString() {
		return messageResources.getHeaderContentString();
	}

	/*
	 * Two messages are equals if the: headerContent, payloadContent and signatureContent are equals.
	 */
	@Override
	public boolean equals(Object obj) {
		return this.messageResources.equals(((MultipartMessage)obj).messageResources);
	}
	
	@Override
	public final int hashCode() {
		return messageResources.hashCode();
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
}

