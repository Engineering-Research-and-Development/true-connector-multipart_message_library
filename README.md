# Multipart Message Processor for IDS Messages

Utility library that handles all IDS Info Model management, Serialization, DeSerialization, converting from Multiaprt String to java and vice versa.

Also has few methods for building some of the IDS Messages, like ArtifactRequestMessage, ArtifactResponseMessage, messages used in Contract Negotiation flow. User is not forced to use those methods, they are here just to provide convenient functionality for development.

Current supported IDSA information model is 4.2.7

```xml
<properties>
	<information.model.version>4.2.7</information.model.version>
</properties>

<dependency>
	<groupId>de.fraunhofer.iais.eis.ids.infomodel</groupId>
	<artifactId>java</artifactId>
	<version>${information.model.version}</version>
</dependency>
<dependency>
    <groupId>de.fraunhofer.iais.eis.ids.infomodel</groupId>
    <artifactId>validation-serialization-provider</artifactId>
    <version>${information.model.version}</version>
</dependency>
```
