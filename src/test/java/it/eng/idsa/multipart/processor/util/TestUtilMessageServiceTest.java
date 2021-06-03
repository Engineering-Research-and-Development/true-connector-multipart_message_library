package it.eng.idsa.multipart.processor.util;

import static org.junit.Assert.assertNotNull;

import java.net.URI;

import org.junit.jupiter.api.Test;

import de.fraunhofer.iais.eis.ConnectorUnavailableMessage;
import de.fraunhofer.iais.eis.ConnectorUpdateMessage;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.QueryLanguage;
import de.fraunhofer.iais.eis.QueryMessage;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionReason;
import it.eng.idsa.multipart.util.TestUtilMessageService;

/**
 * Used to validate TestUtilMessageService for compatibility with new IDS model</br>
 * Once version is updated, check if all mandatory fields are still created
 * @author igor.balog
 *
 */
public class TestUtilMessageServiceTest {
	
	@Test 
	public void artifactRequestMessage() {
		Message message = TestUtilMessageService.getArtifactRequestMessage();
		assertNotNull(message);
		verifyFields(message);
	}
	
	@Test 
	public void artifactResponseMessage() {
		Message message = TestUtilMessageService.getArtifactResponseMessage();
		assertNotNull(message);
		verifyFields(message);
	}
	
	@Test 
	public void contractAgreementMessage() {
		Message message = TestUtilMessageService.getContractAgreementMessage();
		assertNotNull(message);
		verifyFields(message);
	}
	
	@Test 
	public void contractRequestMessage() {
		Message message = TestUtilMessageService.getContractRequestMessage();
		assertNotNull(message);
		verifyFields(message);
	}
	
	@Test 
	public void descriptionRequestMessage() {
		Message message = TestUtilMessageService.getDescriptionRequestMessage();
		assertNotNull(message);
		verifyFields(message);
	}
	
	@Test 
	public void contractAgreement() {
		ContractAgreement ca = TestUtilMessageService.getContractAgreement();
		assertNotNull(ca);
		// mandatory fields from version 4.0.6
		assertNotNull(ca.getContractStart());
		assertNotNull(ca.getContractEnd());
	}
	
	@Test
	public void rejectionMessage() {
		RejectionMessage rejection = TestUtilMessageService.getRejectionMessage(RejectionReason.NOT_FOUND);
		assertNotNull(rejection);
	}
	
	@Test
	public void connectorUpdateMessage() {
		ConnectorUpdateMessage msg = TestUtilMessageService.getConnectorUpdateMessage(TestUtilMessageService.SENDER_AGENT, 
				TestUtilMessageService.ISSUER_CONNECTOR, TestUtilMessageService.AFFECTED_CONNECOTR);
		assertNotNull(msg);
	}
	
	@Test
	public void connectorUnavailableMessage() {
		ConnectorUnavailableMessage msg = TestUtilMessageService.getConnectorUnavailableMessage(TestUtilMessageService.SENDER_AGENT, 
				TestUtilMessageService.ISSUER_CONNECTOR, TestUtilMessageService.AFFECTED_CONNECOTR);
		assertNotNull(msg);
	}
	
	@Test
	public void queryMessage() {
		QueryMessage query = TestUtilMessageService.getQueryMessage(TestUtilMessageService.SENDER_AGENT, TestUtilMessageService.ISSUER_CONNECTOR, 
				QueryLanguage.SPARQL);
		assertNotNull(query);
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
