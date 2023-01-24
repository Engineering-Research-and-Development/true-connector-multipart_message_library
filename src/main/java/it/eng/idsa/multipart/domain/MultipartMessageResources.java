package it.eng.idsa.multipart.domain;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iais.eis.Message;
import it.eng.idsa.multipart.util.UtilMessageService;

public class MultipartMessageResources {
	private static final Logger logger = LoggerFactory.getLogger(MultipartMessageResources.class);

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
	
	public void clean(){
		logger.info("MultipartMessage cleanup");
		
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
