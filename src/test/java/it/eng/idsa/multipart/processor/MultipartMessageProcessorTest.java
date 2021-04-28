package it.eng.idsa.multipart.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import it.eng.idsa.multipart.builder.MultipartMessageBuilder;
import it.eng.idsa.multipart.domain.MultipartMessage;
import it.eng.idsa.multipart.util.MultipartMessageKey;
import it.eng.idsa.multipart.util.TestUtilMessageService;

public class MultipartMessageProcessorTest {
	
	private static Message ARTIFACT_REQUEST_MESSAGE = TestUtilMessageService.getArtifactRequestMessage();
	
	private Map<String, String> expectedHttpHeader  = new HashMap<String, String>() {
		private static final long serialVersionUID = 6644844396388888064L;
	{
	    put(MultipartMessageKey.CONTENT_TYPE.label, "multipart/mixed; boundary=CQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4Dkn6;charset=UTF-8");
	    put(MultipartMessageKey.FORWARD_TO.label, "Forward-To: broker");
	}}; 
	
	private String expectedHeaderContentString = TestUtilMessageService.getMessageAsString(ARTIFACT_REQUEST_MESSAGE);
	private String expectedPayloadContentString = "{\"catalog.offers.0.resourceEndpoints.path\":\"/pet\"}";
	private String expectedSignatureContentString = "{\"signature.resourceEndpoints.path\":\"/signature\"}";
	
	private Map<String, String> expectedHeaderHeader  = new HashMap<>() {
		private static final long serialVersionUID = 3266167283830886491L;
	{
	    put(MultipartMessageKey.CONTENT_DISPOSITION.label, "form-data; name=\"header\"");
	    put(MultipartMessageKey.CONTENT_LENGTH.label, "534");
	}};
	
	private Map<String, String> expectedPayloadHeader  = new HashMap<>() {
		private static final long serialVersionUID = -7084243556730137950L;
	{
	    put(MultipartMessageKey.CONTENT_DISPOSITION.label, "form-data; name=\"payload\"");
	    put(MultipartMessageKey.CONTENT_LENGTH.label, "50");
	}};
	
	private Map<String, String> expectedSignatureHeader  = new HashMap<>() {
		private static final long serialVersionUID = -4884253312953549157L;
	{
	    put(MultipartMessageKey.CONTENT_DISPOSITION.label, "form-data; name=\"signature\"");
	    put(MultipartMessageKey.CONTENT_LENGTH.label, "49");
	}};
	
	
	private String multipartMessageString;
	private MultipartMessage expectedMultipartMessage;

	@BeforeEach
    public void init() throws IOException {
		multipartMessageString = new StringBuilder()
				.append("POST /idscp_out HTTP/1.1\r\n" + 
						"Host: core-container:8080\r\n" + 
						"Content-Type: multipart/mixed; boundary=CQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4Dkn6;charset=UTF-8\r\n" + 
						"Forward-To: broker\r\n" + 
						"\r\n" + 
						"--CQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4Dkn6\r\n" + 
						"Content-Disposition: form-data; name=\"header\"\r\n" + 
						"Content-Length: 534\r\n")
						.append(TestUtilMessageService.getMessageAsString(ARTIFACT_REQUEST_MESSAGE))
						.append("\r\n")
						.append("--CQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4Dkn6\r\n")
						.append("Content-Disposition: form-data; name=\"payload\"\r\n" + 
								"Content-Length: 50\r\n" + 
								"\r\n" + 
								"{\"catalog.offers.0.resourceEndpoints.path\":\"/pet\"}\r\n")
						.append("--CQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4Dkn6\r\n")
						.append("Content-Disposition: form-data; name=\"signature\"\r\n" + 
								"Content-Length: 49\r\n" + 
								"\r\n" + 
								"{\"signature.resourceEndpoints.path\":\"/signature\"}\r\n")
						.append("--CQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4Dkn6\r\n")
				.toString();
		expectedMultipartMessage = new MultipartMessageBuilder()
															.withHttpHeader(expectedHttpHeader)
															.withHeaderHeader(expectedHeaderHeader)
															.withHeaderContent(expectedHeaderContentString)
															.withPayloadHeader(expectedPayloadHeader)
															.withPayloadContent(expectedPayloadContentString)
															.withSignatureHeader(expectedSignatureHeader)
															.withSignatureContent(expectedSignatureContentString)
															.build();
    }

	// test WITHOUT HttpHeader
	@Test
	public void testParseMultipartMessageWithoutHttpHeader() {
		
		// do
		MultipartMessage resultMultipartMessage = MultipartMessageProcessor.parseMultipartMessage(multipartMessageString);
		
		// then
		assertTrue("httpHeader shuould be empty", resultMultipartMessage.getHttpHeaders().isEmpty());
		assertTrue("headerContents, payloadContents and signatureContents are not equals", expectedMultipartMessage.equals(resultMultipartMessage));
		assertEquals("headerHeaders::Content-Disposition are not equals", expectedMultipartMessage.getHeaderHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString(), resultMultipartMessage.getHeaderHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString());
		assertEquals("headerHeaders::Content-Length are not equals", expectedMultipartMessage.getHeaderHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString(), resultMultipartMessage.getHeaderHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString());
		assertEquals("payloadHeaders::Content-Disposition are not equals", expectedMultipartMessage.getPayloadHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString(), resultMultipartMessage.getPayloadHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString());
		assertEquals("payloadHeaders::Content-Length are not equals", expectedMultipartMessage.getPayloadHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString(), resultMultipartMessage.getPayloadHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString());
		assertEquals("signatureHeaders::Content-Disposition are not equals", expectedMultipartMessage.getSignatureHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString(), resultMultipartMessage.getSignatureHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString());
		assertEquals("signatureHeaders::Content-Length are not equals", expectedMultipartMessage.getSignatureHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString(), resultMultipartMessage.getSignatureHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString());
	}
	
	// test WITH HttpHeader
	@Test
	public void testParseMultipartMessageWithHttpHeader() {
		
		// when
		String contentType = "Content-Type: form-data; boundary=TESTCQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4;charset=UTF-8";
		
		// do
		MultipartMessage resultMultipartMessage = MultipartMessageProcessor.parseMultipartMessage(multipartMessageString, contentType);
		
		// then
		assertTrue("Content-Type in the httpHeader shuould exists", resultMultipartMessage.getHttpHeaders().get(MultipartMessageKey.CONTENT_TYPE.label).startsWith("Content-Type: form-data; boundary="));
		assertTrue("headerContents, payloadContents and signatureContents are not equals", expectedMultipartMessage.equals(resultMultipartMessage));
		assertEquals("headerHeaders::Content-Disposition are not equals", expectedMultipartMessage.getHeaderHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString(), resultMultipartMessage.getHeaderHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString());
		assertEquals("headerHeaders::Content-Length are not equals", expectedMultipartMessage.getHeaderHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString(), resultMultipartMessage.getHeaderHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString());
		assertEquals("payloadHeaders::Content-Disposition are not equals", expectedMultipartMessage.getPayloadHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString(), resultMultipartMessage.getPayloadHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString());
		assertEquals("payloadHeaders::Content-Length are not equals", expectedMultipartMessage.getPayloadHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString(), resultMultipartMessage.getPayloadHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString());
		assertEquals("signatureHeaders::Content-Disposition are not equals", expectedMultipartMessage.getSignatureHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString(), resultMultipartMessage.getSignatureHeader().get(MultipartMessageKey.CONTENT_DISPOSITION.label).toString());
		assertEquals("signatureHeaders::Content-Length are not equals", expectedMultipartMessage.getSignatureHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString(), resultMultipartMessage.getSignatureHeader().get(MultipartMessageKey.CONTENT_LENGTH.label).toString());
	}
	
	// test WITHOUT HttpHeader
	@Test
	public void testMultipartMessagetoStringWithoutHttpHeader() throws IOException {
		
		// when
		MultipartMessage multipartMessage = new MultipartMessageBuilder()
				.withHttpHeader(expectedHttpHeader)
				.withHeaderHeader(expectedHeaderHeader)
				.withHeaderContent(expectedHeaderContentString)
				.withPayloadHeader(expectedPayloadHeader)
				.withPayloadContent(expectedPayloadContentString)
				.withSignatureHeader(expectedSignatureHeader)
				.withSignatureContent(expectedSignatureContentString)
				.build();
		
		// do
		String resultMultipartMessageString = MultipartMessageProcessor.multipartMessagetoString(multipartMessage, false);
		
		// Divide multipart message on the lines
		Stream<String> lines = resultMultipartMessageString.trim().lines();
		List<String> linesInMultipartMessage = lines.collect(Collectors.toList());
		// Boundary
		String boundary = linesInMultipartMessage.get(0);
		// Delete first and the last line;
		linesInMultipartMessage.remove(0);
		linesInMultipartMessage.remove(linesInMultipartMessage.size()-1);
		
		StringBuilder stringBuilder = new StringBuilder();
		linesInMultipartMessage.forEach(e -> stringBuilder.append(e + System.lineSeparator()));
		
		String[] parts =  stringBuilder.toString().split(boundary);
		String resultHeader = parts[0];
		String resultPayload = parts[1];
		String resultSignature = parts[2];
		
		// then
		String expectedHeader = "Content-Disposition: form-data; name=\"header\"" + System.lineSeparator() +
				"Content-Length: 534" + System.lineSeparator() + 
				"" + System.lineSeparator() + 
				TestUtilMessageService.getMessageAsString(ARTIFACT_REQUEST_MESSAGE)
				+ System.lineSeparator() + 
				"";
		String expectedPayload = "Content-Disposition: form-data; name=\"payload\"" + System.lineSeparator() +  
				"Content-Length: 50" + System.lineSeparator() +  
				"" + System.lineSeparator() +  
				"{\"catalog.offers.0.resourceEndpoints.path\":\"/pet\"}";
		String expectedSignature = "Content-Disposition: form-data; name=\"signature\"" + System.lineSeparator() + 
				"Content-Length: 49" + System.lineSeparator() + 
				"" + System.lineSeparator() + 
				"{\"signature.resourceEndpoints.path\":\"/signature\"}";
		validateHeaderMessagesAreTheSame(expectedHeader, resultHeader);

		assertEquals("Numbers parts in the multipart message are 3", 3, parts.length);
		assertEquals("Payload is not as expected", expectedPayload.trim(), resultPayload.trim());
		assertEquals("Singnture is not as expected", expectedSignature.trim(), resultSignature.trim());
	}

	// test WITH HttpHeader
	@Test
	public void testMultipartMessagetoStringWithHttpHeader() throws IOException {
		// when
		MultipartMessage multipartMessage = new MultipartMessageBuilder()
				.withHttpHeader(expectedHttpHeader)
				.withHeaderHeader(expectedHeaderHeader)
				.withHeaderContent(expectedHeaderContentString)
				.withPayloadHeader(expectedPayloadHeader)
				.withPayloadContent(expectedPayloadContentString)
				.withSignatureHeader(expectedSignatureHeader)
				.withSignatureContent(expectedSignatureContentString)
				.build();
				
		// do
		String resultMultipartMessageString = MultipartMessageProcessor.multipartMessagetoString(multipartMessage);
		
		// Devide multipart message on the lines
		Stream<String> lines = resultMultipartMessageString.trim().lines();
		List<String> linesInMultipartMessage = lines.collect(Collectors.toList());
		// Boundary
		String boundary = linesInMultipartMessage.get(3);
		
		// get Contet-Type
		String contentType = linesInMultipartMessage.get(1);
		
		// Delete HttpHeaders, first boundary and the last boundary
		linesInMultipartMessage.remove(0);
		linesInMultipartMessage.remove(0);
		linesInMultipartMessage.remove(0);
		linesInMultipartMessage.remove(0);
		linesInMultipartMessage.remove(linesInMultipartMessage.size()-1);
		
		StringBuilder stringBuilder = new StringBuilder();
		linesInMultipartMessage.forEach(e -> stringBuilder.append(e + System.lineSeparator()));
		
		String[] parts =  stringBuilder.toString().split(boundary);
		String resultHeader = parts[0];
		String resultPayload = parts[1];
		String resultSignature = parts[2];
		
		// then
		String expectedHeader = "Content-Disposition: form-data; name=\"header\"" + System.lineSeparator() +
				"Content-Length: 534" + System.lineSeparator() + 
				"" + System.lineSeparator() + 
				TestUtilMessageService.getMessageAsString(ARTIFACT_REQUEST_MESSAGE)
				 + System.lineSeparator() + 
				"";
		String expectedPayload = "Content-Disposition: form-data; name=\"payload\"" + System.lineSeparator() +  
				"Content-Length: 50" + System.lineSeparator() +  
				"" + System.lineSeparator() +  
				"{\"catalog.offers.0.resourceEndpoints.path\":\"/pet\"}";
		String expectedSignature = "Content-Disposition: form-data; name=\"signature\"" + System.lineSeparator() + 
				"Content-Length: 49" + System.lineSeparator() + 
				"" + System.lineSeparator() + 
				"{\"signature.resourceEndpoints.path\":\"/signature\"}";

		validateHeaderMessagesAreTheSame(expectedHeader, resultHeader);

		assertTrue("", contentType.startsWith("multipart/mixed; boundary="));
		assertEquals("Numbers parts in the multipart message are 3", 3, parts.length);
		assertEquals("Payload is not as expected", expectedPayload.trim(), resultPayload.trim());
		assertEquals("Singnture is not as expected", expectedSignature.trim(), resultSignature.trim());
		
	}
	
	@Test
	@Disabled
	public void splitString() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Content-Disposition", "form-data; name=\"header\"");
		
		 String headerHeaderString = headers
                 .entrySet()
                 .parallelStream()
                 .flatMap(e -> Stream.of(e.getKey() + ": " + e.getValue()))
                 .collect(Collectors.joining(System.lineSeparator()));
		System.out.println(headerHeaderString);
	}
	
	private void validateHeaderMessagesAreTheSame(String expectedHeader, String resultHeader) throws IOException {
		StringBuilder sbExpectedHeader = new StringBuilder(expectedHeader);
		// delete Content-Type and Content-Length lines
		sbExpectedHeader.delete(0, sbExpectedHeader.indexOf("\n") + 1);
		sbExpectedHeader.delete(0, sbExpectedHeader.indexOf("\n") + 1);
		Message expectedHeaderMessage = new Serializer().deserialize(sbExpectedHeader.toString(), Message.class);
		
		StringBuilder sbResultHeaderMessage = new StringBuilder(resultHeader);
		// delete Content-Type and Content-Length lines
		sbResultHeaderMessage.delete(0, sbResultHeaderMessage.indexOf("\n") + 1);
		sbResultHeaderMessage.delete(0, sbResultHeaderMessage.indexOf("\n") + 1);
		Message resultHeaderMessage = new Serializer().deserialize(sbResultHeaderMessage.toString(), Message.class);

		assertTrue(resultHeaderMessage instanceof ArtifactRequestMessage);
		assertEquals(expectedHeaderMessage.getId(), resultHeaderMessage.getId());
		assertEquals(expectedHeaderMessage.getModelVersion(), resultHeaderMessage.getModelVersion());
		assertEquals(expectedHeaderMessage.getIssued(), resultHeaderMessage.getIssued());
		assertEquals(expectedHeaderMessage.getCorrelationMessage(), resultHeaderMessage.getCorrelationMessage());
		assertEquals(expectedHeaderMessage.getTransferContract(), resultHeaderMessage.getTransferContract());
	}
	
}
