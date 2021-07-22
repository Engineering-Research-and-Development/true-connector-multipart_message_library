package it.eng.idsa.multipart.processor;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.http.protocol.HTTP;
import org.junit.jupiter.api.Test;

import de.fraunhofer.iais.eis.Message;
import it.eng.idsa.multipart.domain.MultipartMessage;
import it.eng.idsa.multipart.processor.util.TestUtilMessageService;

public class MultipartMessageProcessorFormTest {

	
	String expectedPayloadContent = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"address\":\"591  Franklin Street, Pennsylvania\",\"checksum\":\"ABC123 2020/12/24 09:29:56\",\"dateOfBirth\":\"2020/12/24 09:29:56\"}"; 

	String header = "--sLo6Ma1Cpd6T5nUnbBQqJvCMjswOqVt6vypl5-\r\n" + 
			"Content-Disposition: form-data; name=\"header\"\r\n" + 
			"Content-Type: application/json; charset=UTF-8\r\n" + 
			"Content-Transfer-Encoding: 8bit\r\n" + 
			"Content-Length: 684\r\n" + 
			"\r\n";
	
	String footer = 
			"\r\n" + 
			"--sLo6Ma1Cpd6T5nUnbBQqJvCMjswOqVt6vypl5-\r\n" + 
			"Content-Disposition: form-data; name=\"payload\"\r\n" + 
			"Content-Type: application/json; charset=UTF-8\r\n" + 
			"Content-Transfer-Encoding: 8bit\r\n" + 
			"Content-Length: 160\r\n" + 
			"\r\n" + 
			"{\"firstName\":\"John\",\"lastName\":\"Doe\",\"address\":\"591  Franklin Street, Pennsylvania\",\"checksum\":\"ABC123 2020/12/24 09:29:56\",\"dateOfBirth\":\"2020/12/24 09:29:56\"}\r\n" + 
			"--sLo6Ma1Cpd6T5nUnbBQqJvCMjswOqVt6vypl5---";
	
	Message message = TestUtilMessageService.getArtifactRequestMessage();
	String messageAsString = header + TestUtilMessageService.getMessageAsString(message) + footer;
	
	String contentType = "application/json; charset=UTF-8";
	String contentTransferEncoding = "8bit";
	String contentLengthHeader = "684";
	String contentLengthPayload = "160";
	String contentDispositionHeader = "form-data; name=\"header\"";
	String contentDispositionPayload = "form-data; name=\"payload\"";
	
	@Test
	public void parseMultipartMessageTest() {
		MultipartMessage multipartMessage = MultipartMessageProcessor.parseMultipartMessage(messageAsString);
		assertNotNull(multipartMessage);
		assertNotNull(multipartMessage.getHttpHeaders());
		assertNotNull(multipartMessage.getHeaderHeader());
		assertNotNull(multipartMessage.getHeaderContent());
		assertNotNull(multipartMessage.getPayloadHeader());
		assertNotNull(multipartMessage.getPayloadContent());
		assertEquals(message.getIssued(), multipartMessage.getHeaderContent().getIssued());
		assertEquals(message.getCorrelationMessage(), multipartMessage.getHeaderContent().getCorrelationMessage());
		assertEquals(message.getTransferContract(), multipartMessage.getHeaderContent().getTransferContract());
		assertEquals(message.getIssuerConnector(), multipartMessage.getHeaderContent().getIssuerConnector());
		assertEquals(message.getModelVersion(), multipartMessage.getHeaderContent().getModelVersion());
		assertEquals(message.getSenderAgent(), multipartMessage.getHeaderContent().getSenderAgent());
		
		assertEquals(contentType, multipartMessage.getHeaderHeader().get(HTTP.CONTENT_TYPE));
		assertEquals(contentTransferEncoding, multipartMessage.getHeaderHeader().get("Content-Transfer-Encoding"));
		assertEquals(contentLengthHeader, multipartMessage.getHeaderHeader().get(HTTP.CONTENT_LEN));
		assertEquals(contentDispositionHeader, multipartMessage.getHeaderHeader().get("Content-Disposition"));
		assertEquals(contentDispositionPayload, multipartMessage.getPayloadHeader().get("Content-Disposition"));
		assertEquals(contentLengthPayload, multipartMessage.getPayloadHeader().get(HTTP.CONTENT_LEN));
		assertEquals(contentTransferEncoding, multipartMessage.getPayloadHeader().get("Content-Transfer-Encoding"));
		assertEquals(contentType, multipartMessage.getPayloadHeader().get(HTTP.CONTENT_TYPE));
		assertEquals(expectedPayloadContent, multipartMessage.getPayloadContent());
	}
}
