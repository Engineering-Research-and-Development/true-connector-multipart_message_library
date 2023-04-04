package it.eng.idsa.multipart.processor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.ConstraintBuilder;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.DataRepresentationBuilder;
import de.fraunhofer.iais.eis.DataResourceBuilder;
import de.fraunhofer.iais.eis.DefaultAction;
import de.fraunhofer.iais.eis.DefaultBinaryOperator;
import de.fraunhofer.iais.eis.DefaultContentType;
import de.fraunhofer.iais.eis.DefaultLanguage;
import de.fraunhofer.iais.eis.DefaultLeftOperand;
import de.fraunhofer.iais.eis.DefaultSecurityProfile;
import de.fraunhofer.iais.eis.ImageRepresentationBuilder;
import de.fraunhofer.iais.eis.ImageResourceBuilder;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.TextRepresentationBuilder;
import de.fraunhofer.iais.eis.TextResourceBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import it.eng.idsa.multipart.processor.MultipartMessageProcessor;
import it.eng.idsa.multipart.util.DateUtil;
import it.eng.idsa.multipart.util.UtilMessageService;

/**
 * Utility class for creating SelfDescription document with 2 resource catalogs and each resource catalog has</br>
 * 2 resources with one contract offer and one representation
 * @author igor.balog
 *
 */
public class SelfDescriptionUtil {
	
	private static final @NotNull URI ISSUER_CONNECTOR = URI.create("https://issuer.connector.com");
	private static URI MAINTAINER = URI.create("https://maintainer.connector.com");
	private static URI CURATOR = URI.create("https://curator.connector.com");
	private static String INFO_MODEL_VERSION = "4.2.8";
	private static String SELF_DESCRIPTION_TITLE = "Self Description title";
	private static String SELF_DESCRIPTION_DESCRIPTION = "Self Description - description with some more text";
	private static String OUTBOUND_INFO_MODEL_VERSION = "4.2.8";
	private static @NotNull URI DEFAUT_ENDPOINT = URI.create("https://default.endpoint.com");;
	private static URI ACCESS_URL = URI.create("https://access.url.com");

	private static Resource[] getResources(String catalogNumber) {
		String RESOURCE_TITLE = "Resource title";
		String RESOURCE_DESCRIPTION = "Resource description";
		String RESOURCE_DESCRIPTIOND = "In order to filter search you can pass following json { JSON CONTENT startDate, endDate, page, size}";

		
		URI artifact1URI = URI.create("http://w3id.org/engrd/connector/artifact/catalog/" + catalogNumber + "/resource/1");
		Representation defaultRepresentation1 = new DataRepresentationBuilder(
					URI.create("https://w3id.org/idsa/autogen/representation/catalog/" + catalogNumber + "/resource/1/representation/1"))
				._created_(DateUtil.normalizedDateTime())
				._instance_(Util.asList(getArtifact(
						URI.create("http://w3id.org/engrd/connector/artifact/catalog/" + catalogNumber + "/artifact/1"), 
						"some_file_catalog_" + catalogNumber + "_1" + ".pdf")))
				.build();

		ContractOffer offer1 = createContractOffer(artifact1URI, catalogNumber, "1", "1");
		
		Resource offeredResource1 = (new DataResourceBuilder())
				._title_(Util.asList(new TypedLiteral("Windfarm Turbine data")))
				._description_(Util.asList(new TypedLiteral(RESOURCE_DESCRIPTIOND)))
				._contentType_(DefaultContentType.SCHEMA_DEFINITION)
				._keyword_(Util.asList(new TypedLiteral("Engineering Ingegneria Informatica SpA"),
						new TypedLiteral("broker"), new TypedLiteral("trueConnector")))
				._version_("1.0.0")._language_(Util.asList(DefaultLanguage.EN, DefaultLanguage.IT))
				._modified_(DateUtil.normalizedDateTime())
				._created_(DateUtil.normalizedDateTime())
				._sovereign_(URI.create("https://sovereign.com"))
				._contractOffer_(Util.asList(offer1))
				._representation_(Util.asList(defaultRepresentation1))
				.build();
		
		
		//---------------------------------
		URI artifact2URI = URI.create("http://w3id.org/engrd/connector/artifact/catalog/" + catalogNumber + "/resource/2");
		Representation defaultRepresentation2 = new ImageRepresentationBuilder(
					URI.create("https://w3id.org/idsa/autogen/representation/catalog/" + catalogNumber + "/resource/2/representation/1"))
				._created_(DateUtil.normalizedDateTime())
				._instance_(Util.asList(getArtifact(
						URI.create("http://w3id.org/engrd/connector/artifact/catalog/" + catalogNumber + "/artifact/2"), 
						"some_file_catalog_" + catalogNumber + "_2" + ".pdf")))
				.build();
		
		ContractOffer offer2 = createContractOffer(artifact2URI, catalogNumber, "2" , "1");
		Resource offeredResource2 = (new ImageResourceBuilder())
				._title_(Util.asList(new TypedLiteral(RESOURCE_TITLE)))
				._description_(Util.asList(new TypedLiteral(RESOURCE_DESCRIPTION)))
				._contentType_(DefaultContentType.SCHEMA_DEFINITION)
				._keyword_(Util.asList(new TypedLiteral("Engineering Ingegneria Informatica SpA"),
						new TypedLiteral("broker"), new TypedLiteral("trueConnector")))
				._version_("1.0.0")._language_(Util.asList(DefaultLanguage.EN, DefaultLanguage.IT))
				._modified_(DateUtil.normalizedDateTime())
				._created_(DateUtil.normalizedDateTime())
				._sovereign_(URI.create("https://sovereign.com"))
				._contractOffer_(Util.asList(offer2))
				._representation_(Util.asList(defaultRepresentation2))
				.build();
		return new Resource[] {offeredResource1, offeredResource2};
	}	
	

	// we use java.util.ArrayList instead of java.util.List because 
	// BaseConnectorBuilder._resourceCatalog_() expects java.util.ArrayList<? extends ResourceCatalog>
	private static java.util.ArrayList<ResourceCatalog> getCatalogs() {
		java.util.ArrayList<ResourceCatalog> catalogList = null;
		try {
			catalogList = new ArrayList<>();
			Resource[] resource1 = getResources("1");
			ResourceCatalog catalog1 = null;
			catalog1 = new ResourceCatalogBuilder(URI.create("http://catalog.com/1"))
					._offeredResource_(Util.asList(resource1))
					.build();
			catalogList.add(catalog1);
			Resource[] resource2 = getResources("2");
			ResourceCatalog catalog2 = null;
			catalog2 = new ResourceCatalogBuilder(URI.create("http://catalog.com/2"))
					._offeredResource_(Util.asList(resource2))
					.build();
			catalogList.add(catalog2);
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
		}
		return catalogList;
	}
//	"Not used anymore"
	@Deprecated
	public static Connector getBaseConnector() {
		return new BaseConnectorBuilder(ISSUER_CONNECTOR)
				._maintainer_(MAINTAINER)
				._curator_(CURATOR)
				._resourceCatalog_(getCatalogs())
				._securityProfile_(DefaultSecurityProfile.BASE_SECURITY_PROFILE)
				._inboundModelVersion_(Util.asList(new String[] { INFO_MODEL_VERSION }))
				._title_(Util.asList(new TypedLiteral(SELF_DESCRIPTION_TITLE)))
				._description_(Util.asList(new TypedLiteral(SELF_DESCRIPTION_DESCRIPTION)))
				._outboundModelVersion_(OUTBOUND_INFO_MODEL_VERSION)
				._hasDefaultEndpoint_(new ConnectorEndpointBuilder(DEFAUT_ENDPOINT)
						._accessURL_(ACCESS_URL)
						.build())
				.build();
	}
	
	public static Artifact getArtifact(URI artifactId, String fileName) {
		return new ArtifactBuilder(artifactId)
		._creationDate_(DateUtil.normalizedDateTime())
		._fileName_(fileName)
		.build();
	}
	
	public static Representation getDataRepresentation(URI representationURI, Artifact artifact) {
		return new DataRepresentationBuilder(representationURI)
				._created_(DateUtil.normalizedDateTime())
				._instance_(Util.asList(artifact))
				.build();
	}
	public static Representation getImageRepresentation(URI representationURI, Artifact artifact) {
		return new ImageRepresentationBuilder(representationURI)
				._created_(DateUtil.normalizedDateTime())
				._instance_(Util.asList(artifact))
				._height_(BigDecimal.valueOf(200))
				._width_(BigDecimal.valueOf(450))
				.build();
	}
	public static Representation getTextRepresentation(URI representationURI, Artifact artifact) {
		return new TextRepresentationBuilder(representationURI)
				._created_(DateUtil.normalizedDateTime())
				._instance_(Util.asList(artifact))
				._language_(DefaultLanguage.EN)
				.build();
	}
	
	/**
	 * 
	 * @param targetURI
	 * @param catalogNumber
	 * @param resourceOrder
	 * @param offerOrder
	 * @return
	 */
	public static ContractOffer createContractOffer(URI targetURI, String catalogNumber, String resourceOrder, String offerOrder) {
//		Constraint before = new ConstraintBuilder()
//				._leftOperand_(DefaultLeftOperand.POLICY_EVALUATION_TIME)
//				._operator_(DefaultBinaryOperator.AFTER)
//				._rightOperand_(new RdfResource("2020-10-01T00:00:00Z", URI.create("xsd:datetime")))
//				.build();
//		Constraint after = new ConstraintBuilder()
//				._leftOperand_(DefaultLeftOperand.POLICY_EVALUATION_TIME)
//				._operator_(DefaultBinaryOperator.BEFORE)
//				._rightOperand_(new RdfResource("2025-31-12T23:59:00Z", URI.create("xsd:datetime")))
//				.build();
		
		Permission permission2 = new PermissionBuilder(URI.create("http://example.com/policy/catalog/" + catalogNumber + "/resource/" + resourceOrder +"/restrict-access-interval"))
				._target_(new ArtifactBuilder(targetURI).build())
				._action_(Util.asList(DefaultAction.USE))
//				._constraint_(Util.asList(before, after))
				._title_(new TypedLiteral("Example Usage Policy"))
				._description_(new TypedLiteral("provide-access"))
				.build();
		URI contractOffer = URI.create("https://w3id.org/idsa/autogen/contractOffer/catalog/" + catalogNumber + "/resource/" + resourceOrder + "/offer/" + offerOrder);
		return new ContractOfferBuilder(contractOffer)
				._consumer_(URI.create("https://consumer.com"))
				._provider_(URI.create("https://provider.com"))
				._permission_(Util.asList(permission2))
				._contractDate_(DateUtil.normalizedDateTime())
				._contractStart_(UtilMessageService.START_DATE)
				.build();
	}
	
	/**
	 * Use this method in tests to create basic self description
	 * @return
	 */
	public static Connector createDefaultSelfDescription() {
		return new BaseConnectorBuilder(URI.create("https://w3id.org/engrd/connector/"))
				._maintainer_(URI.create("http://sender.maintainerURI.com"))
				._curator_(URI.create("http://sender.curatorURI.com"))
				._resourceCatalog_(getCatalog())
				._securityProfile_(DefaultSecurityProfile.BASE_SECURITY_PROFILE)
				._inboundModelVersion_(Util.asList(new String[] { UtilMessageService.MODEL_VERSION }))
				._title_(Util.asList(new TypedLiteral("Connector title")))
				._description_(Util.asList(new TypedLiteral("Connector description")))
				._outboundModelVersion_(UtilMessageService.MODEL_VERSION)
				._hasDefaultEndpoint_(new ConnectorEndpointBuilder(URI.create("http://default.endpoint.com"))
						._accessURL_(URI.create("http://default.endpoint.com"))
						.build())
				.build();
	}
	
	private static java.util.List<ResourceCatalog> getCatalog() {
		Artifact defaultArtifact = new ArtifactBuilder(URI.create("http://w3id.org/engrd/connector/artifact/1"))
			._creationDate_(DateUtil.now())
			.build();
		
		Resource offeredResource = (new TextResourceBuilder())
				._title_(Util.asList(new TypedLiteral("Default resource")))
				._description_(Util.asList(new TypedLiteral("Default resource description")))
				._contentType_(DefaultContentType.SCHEMA_DEFINITION)
				._keyword_(Util.asList(new TypedLiteral("Engineering Ingegneria Informatica SpA"),
						new TypedLiteral("TRUEConnector")))
				._version_("1.0.0")._language_(Util.asList(DefaultLanguage.EN, DefaultLanguage.IT))
				._modified_(DateUtil.now())
				._created_(DateUtil.now())
				._contractOffer_(Util.asList(createContractOffer()))
				._representation_(Util.asList(getTextRepresentation(defaultArtifact)))
				.build();
		
		List<ResourceCatalog> catalogList = new ArrayList<>();
		ArrayList<Resource> offeredResources = new ArrayList<>();
		offeredResources.add(offeredResource);
		catalogList.add(new ResourceCatalogBuilder()._offeredResource_(offeredResources).build());
		return catalogList;
	}
	
	private static ContractOffer createContractOffer() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		OffsetDateTime dateTime = OffsetDateTime.now(ZoneOffset.UTC);
		
		Constraint before = new ConstraintBuilder()
				._leftOperand_(DefaultLeftOperand.POLICY_EVALUATION_TIME)
				._operator_(DefaultBinaryOperator.AFTER)
				._rightOperand_(new RdfResource(dateTime.minusDays(7).format(formatter), 
						URI.create("http://www.w3.org/2001/XMLSchema#dateTimeStamp")))
				.build();
		
		Constraint after = new ConstraintBuilder()
				._leftOperand_(DefaultLeftOperand.POLICY_EVALUATION_TIME)
				._operator_(DefaultBinaryOperator.BEFORE)
				._rightOperand_(new RdfResource(dateTime.plusMonths(1).format(formatter), 
						URI.create("http://www.w3.org/2001/XMLSchema#dateTimeStamp")))
				.build();
		
		Permission permission2 = new PermissionBuilder()
				._target_(new ArtifactBuilder(URI.create("http://w3id.org/engrd/connector/artifact/1")).build())
				._title_(new TypedLiteral("Permission"))
				._description_(new TypedLiteral("Permission description"))
				._action_(Util.asList(DefaultAction.USE))
				._constraint_(Util.asList(before, after))
				.build();
		
		return new ContractOfferBuilder()
				._permission_(Util.asList(permission2))
				._contractDate_(DateUtil.now())
				._contractStart_(UtilMessageService.START_DATE)
				._contractEnd_(UtilMessageService.END_DATE)
				.build();
	}
	
	private static Representation getTextRepresentation(Artifact artifact) {
		return new TextRepresentationBuilder()
				._created_(DateUtil.now())
				._instance_(Util.asList(artifact))
				._language_(DefaultLanguage.EN)
				.build();
	}
	
	@Test
//	@Disabled("Used only for development purposes to get self descriptionn document")
	public void getConnector() throws IOException {
		Connector connector = createDefaultSelfDescription();
		assertNotNull(connector);
		assertEquals(2, connector.getResourceCatalog().size());
		
		System.out.println(MultipartMessageProcessor.serializeToJsonLD(connector));
	}
}
