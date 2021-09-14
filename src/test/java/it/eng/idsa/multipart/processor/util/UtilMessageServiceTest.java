package it.eng.idsa.multipart.processor.util;

import static org.junit.Assert.assertNotNull;

import java.net.URI;

import org.junit.jupiter.api.Test;

import de.fraunhofer.iais.eis.ConnectorUnavailableMessage;
import de.fraunhofer.iais.eis.ConnectorUpdateMessage;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.QueryLanguage;
import de.fraunhofer.iais.eis.QueryMessage;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionReason;
import it.eng.idsa.multipart.util.UtilMessageService;

/**
 * Used to validate TestUtilMessageService for compatibility with new IDS model</br>
 * Once version is updated, check if all mandatory fields are still created
 * @author igor.balog
 *
 */
public class UtilMessageServiceTest {
	
	@Test 
	public void artifactRequestMessage() {
		Message message = UtilMessageService.getArtifactRequestMessage();
		assertNotNull(message);
		verifyFields(message);
	}
	
	@Test 
	public void artifactResponseMessage() {
		Message message = UtilMessageService.getArtifactResponseMessage();
		assertNotNull(message);
		verifyFields(message);
	}
	
	@Test 
	public void contractAgreementMessage() {
		Message message = UtilMessageService.getContractAgreementMessage();
		assertNotNull(message);
		verifyFields(message);
	}
	
	@Test 
	public void contractRequestMessage() {
		Message message = UtilMessageService.getContractRequestMessage();
		assertNotNull(message);
		verifyFields(message);
	}
	
	@Test 
	public void descriptionRequestMessage() {
		Message message = UtilMessageService.getDescriptionRequestMessage(null);
		assertNotNull(message);
		verifyFields(message);
	}
	
	@Test 
	public void contractAgreement() {
		ContractAgreement ca = UtilMessageService.getContractAgreement();
		assertNotNull(ca);
		// mandatory fields from version 4.0.6
		assertNotNull(ca.getContractStart());
		assertNotNull(ca.getContractEnd());
	}
	
	@Test
	public void rejectionMessage() {
		RejectionMessage rejection = UtilMessageService.getRejectionMessage(RejectionReason.NOT_FOUND);
		assertNotNull(rejection);
	}
	
	@Test
	public void connectorUpdateMessage() {
		ConnectorUpdateMessage msg = UtilMessageService.getConnectorUpdateMessage(UtilMessageService.SENDER_AGENT, 
				UtilMessageService.ISSUER_CONNECTOR, UtilMessageService.AFFECTED_CONNECTOR);
		assertNotNull(msg);
	}
	
	@Test
	public void connectorUnavailableMessage() {
		ConnectorUnavailableMessage msg = UtilMessageService.getConnectorUnavailableMessage(UtilMessageService.SENDER_AGENT, 
				UtilMessageService.ISSUER_CONNECTOR, UtilMessageService.AFFECTED_CONNECTOR);
		assertNotNull(msg);
	}
	
	@Test
	public void queryMessage() {
		QueryMessage query = UtilMessageService.getQueryMessage(UtilMessageService.SENDER_AGENT, UtilMessageService.ISSUER_CONNECTOR, 
				QueryLanguage.SPARQL);
		assertNotNull(query);
	}
	
	@Test
	public void contractRequest() {
		ContractRequest cr = UtilMessageService.getContractRequest(URI.create("https://artifact.id"));
		assertNotNull(cr);
	}
	
	private void verifyFields(Message message) {
		assertNotNull(message.getModelVersion());
		assertNotNull(message.getIssuerConnector());
		assertNotNull(message.getCorrelationMessage());
		// mandatory fields from version 4.0.6 - when we updated to this version
		assertNotNull(message.getSenderAgent());
		assertNotNull(message.getSecurityToken());
	}
}
