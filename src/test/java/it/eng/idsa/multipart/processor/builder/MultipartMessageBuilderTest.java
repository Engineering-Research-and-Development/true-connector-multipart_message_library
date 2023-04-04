package it.eng.idsa.multipart.processor.builder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import it.eng.idsa.multipart.builder.MultipartMessageBuilder;
import it.eng.idsa.multipart.domain.MultipartMessage;
import it.eng.idsa.multipart.exception.MultipartMessageException;
import it.eng.idsa.multipart.util.UtilMessageService;

public class MultipartMessageBuilderTest {

	@Test
	public void buildMessage_FailsFromHeaderString() {
		assertThrows(MultipartMessageException.class,
	            ()->{
	            	new MultipartMessageBuilder()
            			.withHeaderContent("{\"ids:issuerConnector\":{\"@id\":\"http://w3id.org/engrd/connector/\"},\"ids:modelVersion\":\"4.2.8\",\"@type\":\"ids:ArtifactRequestMessage\",\"ids:requestedArtifact\":{\"@id\":\"http://w3id.org/engrd/connector/artifact/1\"},\"@context\":{\"ids\":\"https://w3id.org/idsa/core/\"},\"ids:issued\":{\"@value\":\"2020-11-25T16:43:27.051+01:00\",\"@type\":\"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"}}")
            			.withPayloadContent("payload content")
            			.build();
	            });
	}
	
	@Test
	public void buildMessageStringHeadeAndPayloadSuccessful() {
		MultipartMessage mm = new MultipartMessageBuilder()
				.withHeaderContent(UtilMessageService.getMessageAsString(
						UtilMessageService.getArtifactRequestMessage()))
				.withPayloadContent("payload content")
				.build();
		assertNotNull(mm);
		assertNotNull(mm.getHeaderContent());
		assertTrue(mm.getHeaderContent() instanceof ArtifactRequestMessage);
		assertNotNull(mm.getPayloadContent());
	}
}
