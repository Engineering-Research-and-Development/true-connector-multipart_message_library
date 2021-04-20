package it.eng.idsa.multipart.processor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import it.eng.idsa.multipart.domain.MultipartMessage;

public class MultipartMessageProcessorMixedTest {

	@Test
	public void parseMultipartMixedString() {
		String messageAsString = "--CQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4Dkn6\r\n" + 
				"Content-Disposition: form-data; name=\"header\"\r\n" + 
				"Content-Type: application/json; charset=UTF-8\r\n" + 
				"Content-Length: 333\r\n" + 
				"\r\n" + 
				"{\r\n" + 
				"  \"@type\" : \"ids:ArtifactResponseMessage\",\r\n" + 
				"  \"issued\" : \"2021-04-08T13:09:42.306Z\",\r\n" + 
				"  \"issuerConnector\" : \"http://true-connector/\",\r\n" + 
				"  \"modelVersion\" : \"4.0.0\",\r\n" + 
				"  \"@id\" : \"https://w3id.org/idsa/autogen/artifactResponseMessage/eb3ab487-dfb0-4d18-b39a-585514dd044f\"\r\n" + 
				"}\r\n" + 
				"--CQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4Dkn6\r\n" + 
				"Content-Disposition: form-data; name=\"payload\"\r\n" + 
				"Content-Length: 50\r\n" + 
				"\r\n" + 
				"{\"catalog.offers.0.resourceEndpoints.path\":\"/pet2\"}\r\n" + 
				"--CQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4Dkn6--";
		MultipartMessage multipartMessage = MultipartMessageProcessor.parseMultipartMessage(messageAsString);
		assertNotNull(multipartMessage);
	}
	
	@Test
	public void parseMultipartMixedStringNoContetnType() {
		String messageAsString = 
				"--W1naK2mLGlKwfavaz4xfARez2cdRVM1\r\n" + 
				" Content-Disposition: form-data; name=\"header\"\r\n" + 
				" Content-Length: 1541\r\n" + 
				"\r\n" + 
				" {\r\n" + 
				"   \"authorizationToken\" : {\r\n" + 
				"     \"@type\" : \"ids:Token\",\r\n" + 
				"     \"@id\" : \"https://w3id.org/idsa/autogen/token/11b37e38-6877-4731-9966-a80f8f37de44\",\r\n" + 
				"     \"tokenFormat\" : {\r\n" + 
				"       \"@id\" : \"https://w3id.org/idsa/code/JWT\"\r\n" + 
				"     },\r\n" + 
				"     \"tokenValue\" : \"eyJ0eXAiOiJKV1QiLCJraWQiOiJkZWZhdWx0IiwiYWxnIjoiUlMyNTYifQ.eyJzY29wZXMiOltdLCJhdWQiOiJJRFNfQ29ubmVjdG9yIiwiaXNzIjoiaHR0cHM6Ly9kYXBzLmFpc2VjLmZyYXVuaG9mZXIuZGUiLCJzdWIiOiJDPUlULE89RW5naW5lZXJpbmcsT1U9UkQsQ049NGM0OTc3N2QtNDcxOC00ZDVjLTlhZmUtMTA1Nzg0OWMxMjU2IiwibmJmIjoxNjE3ODY4ODY3LCJleHAiOjE2MTc4NzI0Njd9.owfBcrYddGkhC3lqlyx-xzR1kkALkFaZe56EsgV5jN-L8QHkoME8loZsztxGsAiUXclgRPXzLhAUtTzcUV332FLcdSwxqigv1oelI8hn8Rf8WDW7hZyNyvowSb5AbeXOKbf2b--_9ke17o-1SfiKGjia7_6-dsJ4GPPX1dWPmzfaqXcMF6T9qIj3DLKVJlVvowKOsECTCtG33LaHAXZHWstLq2bv75Xr0iedBWpaoRLhhk6mGCmKPQaWvCd6bfL51nkWi4rL5oSn7rAIE6dKQwHD1elsXASGzN3X_ufmm1FjRoGxz9KVzDsK5sfdXboxrwopMxZFFGR-a-JwIpVtJg\"\r\n" + 
				"   },\r\n" + 
				"   \"recipientConnector\" : [ \"https://execution-core-container-seller:8889/data\" ],\r\n" + 
				"   \"operationParameterization\" : {\r\n" + 
				"     \"@type\" : \"ids:ParameterAssignment\",\r\n" + 
				"     \"parameter\" : \"https://ids.tno.nl/HTTP\",\r\n" + 
				"     \"@id\" : \"https://w3id.org/idsa/autogen/parameterAssignment/9bd282bf-1770-4e2d-8806-7369d122d3dc\"\r\n" + 
				"   },\r\n" + 
				"   \"issuerConnector\" : \"http://market40.eu/Connector/SCSN-Buyer\",\r\n" + 
				"   \"@type\" : \"ids:InvokeOperationMessage\",\r\n" + 
				"   \"modelVersion\" : \"2.0.0\",\r\n" + 
				"   \"operationReference\" : \"https://ids.tno.nl/openapi/order\",\r\n" + 
				"   \"@id\" : \"https://w3id.org/idsa/autogen/invokeOperationMessage/661a0392-cad3-4e76-bafa-b89adfe999c2\",\r\n" + 
				"   \"issued\" : \"2021-04-08T08:01:05.334Z\"\r\n" + 
				" }\r\n" + 
				"--W1naK2mLGlKwfavaz4xfARez2cdRVM1\r\n" + 
				" Content-Disposition: form-data; name=\"payload\"\r\n" + 
				" Content-Length: 6310\r\n" + 
				"\r\n" + 
				" {\"headers\":{\"content-length\":\"5505\",\"forward-to\":\"https://execution-core-container-seller:8889/data\",\"host\":\"openapi-data-app-buyer:8080\",\"forward-accessurl\":\"https://execution-core-container-seller:8889/data\",\"content-type\":\"application/xml\",\"connection\":\"close\",\"accept\":\"application/json, text/plain, */*\",\"user-agent\":\"axios/0.19.0\"},\"path\":\"/order\",\"method\":\"POST\",\"body\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n<Order xmlns=\\\"urn:oasis:names:specification:ubl:schema:xsd:Order-2\\\" xmlns:cac=\\\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\\\" xmlns:cbc=\\\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" xsi:schemaLocation=\\\"urn:oasis:names:specification:ubl:schema:xsd:Order-2 http://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/maindoc/UBL-Order-2.1.xsd\\\">\\n\\t<!-- SCSN Order example based on UBL Order version 2.1 -->\\n\\t<cbc:ID>Order-1<\\/cbc:ID>\\n    <cbc:IssueDate>2020-01-21<\\/cbc:IssueDate>\\n\\t<cac:BuyerCustomerParty>\\n        <cac:Party>\\n    <cac:PartyIdentification>\\n        <cbc:ID schemeID=\\\"GLN\\\">5790001398644<\\/cbc:ID>\\n    <\\/cac:PartyIdentification>\\n    <cac:PartyName>\\n        <cbc:Name>SCSN Buyer Part<\\/cbc:Name>\\n    <\\/cac:PartyName>\\n    <cac:PartyTaxScheme>\\n        <cbc:CompanyID>NL90390210290S01<\\/cbc:CompanyID>\\n    <cac:TaxScheme/>\\n    <\\/cac:PartyTaxScheme>\\n    <cac:PartyLegalEntity>\\n        <cbc:CompanyID schemeID=\\\"NL:KVK\\\">23432123<\\/cbc:CompanyID>\\n    <\\/cac:PartyLegalEntity>\\n    <cac:PhysicalLocation>\\n        <cbc:Description>https://execution-core-container-buyer:8090/data<\\/cbc:Description>\\n    <\\/cac:PhysicalLocation>\\n    <cac:Contact>\\n        <cac:OtherCommunication>\\n            <cbc:Value>https://execution-core-container-buyer:8090/data<\\/cbc:Value>\\n        <\\/cac:OtherCommunication>\\n    <\\/cac:Contact>\\n<\\/cac:Party>\\n\\t<\\/cac:BuyerCustomerParty>\\n\\t<cac:SellerSupplierParty>\\n        <cac:Party>\\n    <cac:PartyIdentification>\\n        <cbc:ID schemeID=\\\"GLN\\\">23456<\\/cbc:ID>\\n    <\\/cac:PartyIdentification>\\n    <cac:PartyName>\\n        <cbc:Name>SCSN Seller Test<\\/cbc:Name>\\n    <\\/cac:PartyName>\\n    <cac:PartyTaxScheme>\\n        <cbc:CompanyID>NL23456<\\/cbc:CompanyID>\\n    <cac:TaxScheme/>\\n    <\\/cac:PartyTaxScheme>\\n    <cac:PartyLegalEntity>\\n        <cbc:CompanyID schemeID=\\\"NL:KVK\\\">23456<\\/cbc:CompanyID>\\n    <\\/cac:PartyLegalEntity>\\n    <cac:PhysicalLocation>\\n        <cbc:Description>https://execution-core-container-seller:8889/data<\\/cbc:Description>\\n    <\\/cac:PhysicalLocation>\\n    <cac:Contact>\\n        <cac:OtherCommunication>\\n            <cbc:Value>https://execution-core-container-seller:8889/data<\\/cbc:Value>\\n        <\\/cac:OtherCommunication>\\n    <\\/cac:Contact>\\n<\\/cac:Party>\\n\\t<\\/cac:SellerSupplierParty>\\n\\t<cac:AccountingCustomerParty>\\n        <cac:Party>\\n    <cac:PartyIdentification>\\n        <cbc:ID schemeID=\\\"GLN\\\">5790001398644<\\/cbc:ID>\\n    <\\/cac:PartyIdentification>\\n    <cac:PartyName>\\n        <cbc:Name>Accounting Noord-Brabant<\\/cbc:Name>\\n    <\\/cac:PartyName>\\n    <cac:PartyTaxScheme>\\n        <cbc:CompanyID>NL234324324S02<\\/cbc:CompanyID>\\n    <cac:TaxScheme/>\\n    <\\/cac:PartyTaxScheme>\\n    <cac:PartyLegalEntity>\\n        <cbc:CompanyID schemeID=\\\"NL:KVK\\\">90845456<\\/cbc:CompanyID>\\n    <\\/cac:PartyLegalEntity>\\n    <cac:PhysicalLocation>\\n        <cbc:Description>undefined<\\/cbc:Description>\\n    <\\/cac:PhysicalLocation>\\n    <cac:Contact>\\n        <cac:OtherCommunication>\\n            <cbc:Value>undefined<\\/cbc:Value>\\n        <\\/cac:OtherCommunication>\\n    <\\/cac:Contact>\\n<\\/cac:Party>\\n\\t<\\/cac:AccountingCustomerParty>\\n\\t<cac:PaymentTerms>\\n\\t\\t<cbc:ID>124<\\/cbc:ID>\\n\\t\\t<cbc:Note>[Additional information on payment terms]<\\/cbc:Note>\\n\\t\\t<cbc:SettlementDiscountPercent>5<\\/cbc:SettlementDiscountPercent>\\n\\t\\t<cbc:SettlementDiscountAmount currencyID=\\\"EUR\\\">100<\\/cbc:SettlementDiscountAmount>\\n\\t\\t<cac:SettlementPeriod>\\n\\t\\t\\t<cbc:DurationMeasure unitCode=\\\"Days\\\">5.0<\\/cbc:DurationMeasure>\\n\\t\\t<\\/cac:SettlementPeriod>\\n\\t<\\/cac:PaymentTerms>\\n\\t<cac:TransactionConditions>\\n\\t\\t<cbc:Description>Accept within 5 days<\\/cbc:Description>\\n\\t<\\/cac:TransactionConditions>\\n\\t<cac:AnticipatedMonetaryTotal>\\n\\t\\t<cbc:PayableAmount currencyID=\\\"EUR\\\">1000<\\/cbc:PayableAmount>\\n\\t<\\/cac:AnticipatedMonetaryTotal>\\n\\t<cac:OrderLine>\\n\\t\\t<cac:LineItem>\\n\\t\\t\\t<cbc:ID>1<\\/cbc:ID>\\n\\t\\t\\t<cbc:Note>[Free-form text note on orderline]<\\/cbc:Note>\\n\\t\\t\\t<cbc:LineStatusCode>accepted<\\/cbc:LineStatusCode>\\n\\t\\t\\t<cbc:Quantity unitCode=\\\"KGM\\\">1000<\\/cbc:Quantity>\\n\\t\\t\\t<cbc:LineExtensionAmount currencyID=\\\"EUR\\\">175<\\/cbc:LineExtensionAmount>\\n\\t\\t\\t<cac:Delivery>\\n\\t\\t\\t\\t<cbc:ID>9256635<\\/cbc:ID>\\n\\t\\t\\t\\t<cbc:Quantity unitCode=\\\"KGM\\\">50<\\/cbc:Quantity>\\n\\t\\t\\t\\t<cac:DeliveryLocation>\\n\\t\\t\\t\\t\\t<cbc:ID schemeID=\\\"GLN\\\">5790001398644<\\/cbc:ID>\\n\\t\\t\\t\\t<\\/cac:DeliveryLocation>\\n\\t\\t\\t\\t<cac:RequestedDeliveryPeriod>\\n\\t\\t\\t\\t\\t<cbc:EndDate>2020-01-25<\\/cbc:EndDate>\\n\\t\\t\\t\\t<\\/cac:RequestedDeliveryPeriod>\\n                \\n\\t\\t\\t<\\/cac:Delivery>\\n\\t\\t\\t<cac:Price>\\n\\t\\t\\t\\t<cbc:PriceAmount currencyID=\\\"EUR\\\">4<\\/cbc:PriceAmount>\\n\\t\\t\\t\\t<cbc:BaseQuantity unitCode=\\\"KGM\\\">1<\\/cbc:BaseQuantity>\\n\\t\\t\\t\\t<cbc:PriceType>net<\\/cbc:PriceType>\\n\\t\\t\\t\\t<cac:AllowanceCharge>\\n\\t\\t\\t\\t\\t<cbc:ChargeIndicator>false<\\/cbc:ChargeIndicator>\\n\\t\\t\\t\\t\\t<cbc:Amount currencyID=\\\"EUR\\\">0.5<\\/cbc:Amount>\\n\\t\\t\\t\\t<\\/cac:AllowanceCharge>\\n\\t\\t\\t<\\/cac:Price>\\n            <cac:Item>\\n                <cbc:Description>Sheet of steel<\\/cbc:Description>\\n                <cbc:Name>steel<\\/cbc:Name>\\n                <cac:BuyersItemIdentification>\\n                    <cbc:ID>steel<\\/cbc:ID>\\n                <\\/cac:BuyersItemIdentification>\\n                <cac:SellersItemIdentification>\\n                    <cbc:ID>steel-1<\\/cbc:ID>\\n                    <cbc:ExtendedID>A1<\\/cbc:ExtendedID>\\n                <\\/cac:SellersItemIdentification>\\n                <cac:Dimension>\\n                    <cbc:AttributeID>Length<\\/cbc:AttributeID>\\n                    <cbc:Measure unitCode=\\\"MMT\\\">220<\\/cbc:Measure>\\n                <\\/cac:Dimension>\\n            <\\/cac:Item>\\n\\t\\t<\\/cac:LineItem>\\n\\t<\\/cac:OrderLine>\\n<\\/Order>\",\"parameters\":\r\n" + 
				"--W1naK2mLGlKwfavaz4xfARez2cdRVM1--";
		
		MultipartMessage multipartMessage = MultipartMessageProcessor.parseMultipartMessage(messageAsString);
		assertNotNull(multipartMessage);
	}
}
