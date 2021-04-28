package it.eng.idsa.multipart.util;

import java.io.IOException;
import java.net.URI;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.ArtifactResponseMessage;
import de.fraunhofer.iais.eis.ArtifactResponseMessageBuilder;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.ConstraintBuilder;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractAgreementMessage;
import de.fraunhofer.iais.eis.ContractAgreementMessageBuilder;
import de.fraunhofer.iais.eis.ContractRequestMessage;
import de.fraunhofer.iais.eis.ContractRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.Util;
import it.eng.idsa.multipart.processor.MultipartMessageProcessor;

public class TestUtilMessageService {

	public static final String TOKEN_VALUE = "DummyTokenValue";

	public static URI REQUESTED_ARTIFACT = URI.create("http://w3id.org/engrd/connector/artifact/1");

	public static URI ISSUER_CONNECTOR = URI.create("http://w3id.org/engrd/connector");
	public static URI RECIPIENT_CONNECTOR = URI.create("http://w3id.org/engrd/connector/recipient");
	public static URI SENDER_AGENT = URI.create("http://sender.agent/sender");
	
	public static String MODEL_VERSION = "4.0.6";
	
	public static URI CORRELATION_MESSAGE = URI.create("http://w3id.org/connectorUnavailableMessage/1a421b8c-3407-44a8-aeb9-253f145c869a");
	public static URI TRANSFER_CONTRACT = URI.create("http://w3id.org/engrd/connector/examplecontract");
	
	public static XMLGregorianCalendar ISSUED;
	public static XMLGregorianCalendar START_DATE;
	public static XMLGregorianCalendar END_DATE;
	
	static {
		try {
			ISSUED = DateUtil.now();
			START_DATE = DateUtil.now();
			END_DATE = DateUtil.now();
			Duration duration = DatatypeFactory.newInstance().newDurationYearMonth(true, 0, 2);
			END_DATE.add(duration);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static ArtifactRequestMessage getArtifactRequestMessage() {
		return new ArtifactRequestMessageBuilder()
				._issued_(ISSUED)
				._correlationMessage_(CORRELATION_MESSAGE)
//				._transferContract_(TRANSFER_CONTRACT)
				._issuerConnector_(ISSUER_CONNECTOR)
				._senderAgent_(SENDER_AGENT)
				._modelVersion_(MODEL_VERSION)
				._requestedArtifact_(REQUESTED_ARTIFACT)
				._securityToken_(getDynamicAttributeToken())
				.build();
	}

	public static ArtifactResponseMessage getArtifactResponseMessage() {
		return new ArtifactResponseMessageBuilder()
				._issued_(ISSUED)
				._correlationMessage_(CORRELATION_MESSAGE)
//				._transferContract_(TRANSFER_CONTRACT)
				._issuerConnector_(ISSUER_CONNECTOR)
				._senderAgent_(SENDER_AGENT)
				._modelVersion_(MODEL_VERSION)
				._securityToken_(getDynamicAttributeToken())
				.build();
	}
	
	public static DescriptionRequestMessage getDescriptionRequestMessage() {
		return new DescriptionRequestMessageBuilder()
				._issued_(ISSUED)
				._issuerConnector_(ISSUER_CONNECTOR)
				._modelVersion_(MODEL_VERSION)
				._senderAgent_(SENDER_AGENT)
				._correlationMessage_(CORRELATION_MESSAGE)
				._securityToken_(getDynamicAttributeToken())
				.build();
	}

	/**
	 * Used as header in contract agreement flow
	 * @return
	 */
	public static ContractAgreementMessage getContractAgreementMessage() {
		return new ContractAgreementMessageBuilder()
				._modelVersion_(MODEL_VERSION)
//				._transferContract_(URI.create("http://transferedContract"))
				._correlationMessage_(URI.create("http://correlationMessage"))
				._issued_(ISSUED)
				._issuerConnector_(ISSUER_CONNECTOR)
				._securityToken_(getDynamicAttributeToken())
				._senderAgent_(SENDER_AGENT)
				.build();
	}
	
	/**
	 * Used as payload in contract agreement flow
	 * @return
	 */
	public static ContractAgreement getContractAgreement() {
		Constraint constraint = new ConstraintBuilder()
				._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
				._operator_(BinaryOperator.EQUALS)
				._rightOperand_(new RdfResource("2021-04-01T00:00:00Z", URI.create("xsd:datetimeStamp")))
				._pipEndpoint_(URI.create("https//pip.com/policy_evaluation_time"))
				.build();
		
		Permission permission = new PermissionBuilder()
				._action_(Util.asList(Action.USE))
				._target_(REQUESTED_ARTIFACT)
				._assignee_(Util.asList(URI.create("https://assignee.com")))
				._assigner_(Util.asList(URI.create("https://assigner.com")))
				._constraint_(Util.asList(constraint))
				.build();
				
		return new ContractAgreementBuilder()
				._provider_(ISSUER_CONNECTOR)
				._consumer_(RECIPIENT_CONNECTOR)
				._permission_(Util.asList(permission))
				._contractStart_(START_DATE)
				._contractEnd_(END_DATE)
				.build();
	}
	
	public static ContractRequestMessage getContractRequestMessage() {
		return new ContractRequestMessageBuilder()
				._issued_(ISSUED)
				._modelVersion_(MODEL_VERSION)
				._issuerConnector_(ISSUER_CONNECTOR)
				._recipientConnector_(Util.asList(RECIPIENT_CONNECTOR))
				._correlationMessage_(CORRELATION_MESSAGE)
				._senderAgent_(SENDER_AGENT)
				._securityToken_(getDynamicAttributeToken())
				.build();
	}
	
	public static String getMessageAsString(Message message) {
		try {
			return MultipartMessageProcessor.serializeToJsonLD(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static DynamicAttributeToken getDynamicAttributeToken() {
		return new DynamicAttributeTokenBuilder()
				._tokenFormat_(TokenFormat.JWT)
				._tokenValue_(TOKEN_VALUE)
				.build();		
	}

}
