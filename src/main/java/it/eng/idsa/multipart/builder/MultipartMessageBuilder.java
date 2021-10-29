package it.eng.idsa.multipart.builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import it.eng.idsa.multipart.domain.MultipartMessage;
import it.eng.idsa.multipart.exception.MultipartMessageException;

/**
 * 
 * @author Milan Karajovic and Gabriele De Luca
 *
 */


/**
 * Builder for the MultipartMessage.
 */

public class MultipartMessageBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(MultipartMessageBuilder.class);
		
	private Map<String, String> httpHeaders = new HashMap<>();
	private Map<String, String> headerHeader = new HashMap<>();
	private Message headerContent = null;
	private Map<String, String> payloadHeader = new HashMap<>();
	private String payloadContent = null;
	private Map<String, String> signatureHeader = new HashMap<>();
	private String signatureContent = null;
	private String token = null;
	
	private static Serializer serializer;
	
	static {
		serializer =  new Serializer();
	}
	
	public MultipartMessageBuilder withHttpHeader(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
		return this;
	}
	
	public MultipartMessageBuilder withHeaderHeader(Map<String, String> headerHeader) {
		this.headerHeader = headerHeader;
		return this;
	}
	
	public MultipartMessageBuilder withToken(String token) {
		this.token = token;
		return this;
	}
	
	public MultipartMessageBuilder withHeaderContent(Message headerContent) {
		this.headerContent = headerContent;
		return this;
	}
	
	public MultipartMessageBuilder withHeaderContent(String headerContent) {
		try {
			this.headerContent = serializer.deserialize(headerContent, Message.class);
		} catch (IOException e) {
			logger.error("Could not deserialize header {}", e.getLocalizedMessage());
			throw new MultipartMessageException(
					String.format("Could not deserialize header\n%s", e.getLocalizedMessage()));
		}
		return this;
	}
	
	public MultipartMessageBuilder withPayloadHeader(Map<String, String> payloadHeader) {
		this.payloadHeader = payloadHeader;
		return this;
	}
	
	public MultipartMessageBuilder withPayloadContent(String payloadContent) {
		this.payloadContent = payloadContent;
		return this;
	}
	
	public MultipartMessageBuilder withSignatureHeader(Map<String, String> signatureHeader) {
		this.signatureHeader = signatureHeader;
		return this;
	}
	
	public MultipartMessageBuilder withSignatureContent(String signatureContent) {
		this.signatureContent = signatureContent;
		return this;
	}
	
	public MultipartMessage build() {
		if(headerContent == null) {
			throw new MultipartMessageException("Invalid IDS message");
		}
		return new MultipartMessage(
									httpHeaders, 
				                    headerHeader,
				                    headerContent,
				                    payloadHeader, 
				                    payloadContent,
				                    signatureHeader,
				                    signatureContent,
				                    token
				                    );
	}

}
