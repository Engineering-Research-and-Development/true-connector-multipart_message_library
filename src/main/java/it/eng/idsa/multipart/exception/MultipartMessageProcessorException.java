package it.eng.idsa.multipart.exception;

/**
 * Exception when doing manipulating with MultipartMessage
 * Used when String cannot be deserialized to IDS Message or to MultipartMessage
 * @author igor.balog
 *
 */
public class MultipartMessageProcessorException extends RuntimeException {

	private static final long serialVersionUID = 5829300650258445289L;
	
	public MultipartMessageProcessorException(String message) {
		super(message);
	}
}
