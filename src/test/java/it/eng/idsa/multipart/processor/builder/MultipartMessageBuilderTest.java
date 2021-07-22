package it.eng.idsa.multipart.processor.builder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import it.eng.idsa.multipart.builder.MultipartMessageBuilder;
import it.eng.idsa.multipart.domain.MultipartMessage;
import it.eng.idsa.multipart.processor.util.TestUtilMessageService;

public class MultipartMessageBuilderTest {

	@Test
	public void buildMessageStringHeadeAndPayloadFails() {
		MultipartMessage mm = new MultipartMessageBuilder()
				.withHeaderContent("{\"ids:issuerConnector\":{\"@id\":\"http://w3id.org/engrd/connector/\"},\"ids:modelVersion\":\"4.1.1\",\"@type\":\"ids:ArtifactRequestMessage\",\"ids:requestedArtifact\":{\"@id\":\"http://w3id.org/engrd/connector/artifact/1\"},\"@context\":{\"ids\":\"https://w3id.org/idsa/core/\"},\"ids:issued\":{\"@value\":\"2020-11-25T16:43:27.051+01:00\",\"@type\":\"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"}}")
				.withPayloadContent("payload content")
				.build();
		assertNotNull(mm);
		assertNull(mm.getHeaderContent());
		assertNotNull(mm.getPayloadContent());
	}
	
	@Test
	public void buildMessageStringHeadeAndPayloadSuccessful() {
		MultipartMessage mm = new MultipartMessageBuilder()
				.withHeaderContent(TestUtilMessageService.getMessageAsString(
						TestUtilMessageService.getArtifactRequestMessage()))
				.withPayloadContent("payload content")
				.build();
		assertNotNull(mm);
		assertNotNull(mm.getHeaderContent());
		assertTrue(mm.getHeaderContent() instanceof ArtifactRequestMessage);
		assertNotNull(mm.getPayloadContent());
	}
}
