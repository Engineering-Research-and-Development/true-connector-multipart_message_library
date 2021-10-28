package it.eng.idsa.multipart.processor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import it.eng.idsa.multipart.builder.MultipartMessageBuilder;
import it.eng.idsa.multipart.domain.MultipartMessage;
import it.eng.idsa.multipart.exception.MultipartMessageProcessorException;
import it.eng.idsa.multipart.util.MultipartMessageKey;

/**
 * @author Milan Karajovic and Gabriele De Luca
 */

/**
 * The MultipartMessageProcessor
 */
public class MultipartMessageProcessor {

	private static final Logger logger = LoggerFactory.getLogger(MultipartMessageProcessor.class);

    private static final String REGEX_BOUNDARY = "(.*?)boundary=(.*);.*";
    private static final String REGEX_NAME = "(.*?)name=\"(.*)\"(.*?)";
    private static final Predicate<String> predicateLineContentType = (line) -> line.trim().startsWith(MultipartMessageKey.CONTENT_TYPE.label.toLowerCase());
    private static final Predicate<String> predicateLineContentDisposition = (line) -> line.trim().startsWith(MultipartMessageKey.CONTENT_DISPOSITION.label.toLowerCase());
    private static final Predicate<String> predicateLineContentLength = (line) -> line.trim().startsWith(MultipartMessageKey.CONTENT_LENGTH.label.toLowerCase());
    private static final Predicate<String> predicateLineContentTransferEncoding = (line) -> line.trim().startsWith(MultipartMessageKey.CONTENT_TRANSFER_ENCODING.label.toLowerCase());
    private static final Predicate<String> predicateLineEmpty = (line) -> line.trim().isEmpty();
    private static final char[] BOUNDARY_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final String DEFAULT_CONTENT_TYPE = "multipart/mixed; boundary=CQWZRdCCXr5aIuonjmRXF-QzcZ2Kyi4Dkn6;charset=UTF-8";
    private static final String DEFAULT_CONTENT_DISPOSITION = MultipartMessageKey.CONTENT_DISPOSITION.label + ": form-data; name=";

    private static Serializer serializer;
	
	static {
		serializer =  new Serializer();
	}
	
	public static Message getMessage(Object header) {
		Message message = null;
		try {
			message = serializer.deserialize(String.valueOf(header), Message.class);
		} catch (IOException e) {
			logger.error("Error while deserializing message", e);
			throw new MultipartMessageProcessorException("Error while deserializing message");
		}
		return message;
	}
	
    public static MultipartMessage parseMultipartMessage(String message) {
        return parseMultipartMessage(message, null);
    }

    public static MultipartMessage parseMultipartMessage(String message, String contentType) {

        Optional<String> boundaryFromMessage;
        Optional<String> boundaryFromContentType = Optional.of("");

        // Get boundary from the message
        boundaryFromMessage = getMessageBoundaryFromMessage(message);
        if (boundaryFromMessage.isPresent()) {
            logger.info("Boundary from the multipart message is: " + boundaryFromMessage.get());
        } else {
            logger.info("Boundary does not exist in the multipart message");
            throw new MultipartMessageProcessorException("Boundary does not exist in the multipart message");
        }

        String BOUNDARY = boundaryFromMessage.get();

        // Get boundary from the Content-Type
        if (contentType != null) {
            boundaryFromContentType = getMessageBoundaryFromContentType(contentType);
            if (boundaryFromContentType.isPresent()) {
                logger.info("Boundary from the content type is: " + boundaryFromContentType.get());
                if (!BOUNDARY.substring(2).equals(boundaryFromContentType.get())) {
                    // Overide boundary in the ContentType with the boundary in the multipart message
                    contentType = replaceContentTypeWithNewBoundary(BOUNDARY, contentType);
                }
            } else {
                logger.info("Boundary does not exist in the content type");
                throw new MultipartMessageProcessorException("Boundary does not exist in the content type");
            }
        }

        Predicate<String> predicateLineBoundary = (line) -> line.startsWith(BOUNDARY);
        List<List<String>> multipartMessageParts = getMultipartMessagesParts(predicateLineBoundary, message);

        MultipartMessage multipartMessage = createMultipartMessage(multipartMessageParts, contentType);

        return multipartMessage;
    }

    /**
     * Converts multipart message to string, without header ContentType
     * For more options regarding including header Content-Type check {@link #multipartMessagetoString(MultipartMessage message, boolean includeHttpHeaders, Boolean includeJsonLd) {}
     * @param message to be converted
     * @return
     */
    public static String multipartMessagetoString(MultipartMessage message) {
        return multipartMessagetoString(message, true, null);
    }
    
    /**
     * Converts multipart message to string, without header ContentType, with http headers present
     * @param message
     * @param includeHttpHeaders
     * @return
     */
    public static String multipartMessagetoString(MultipartMessage message, boolean includeHttpHeaders) {
    	return multipartMessagetoString(message, includeHttpHeaders, null);
    }

    /**
     * Converts multipart message to string
     * @param message to be converted to string
     * @param includeHttpHeaders if present, overriding default ones
     * @param includeJsonLd if true - content type is application/ld+json</br>if false - ContentType=application/json; charset=UTF-8</br> if null - Content-Type is not present
     * @return
     */
    public static String multipartMessagetoString(MultipartMessage message, boolean includeHttpHeaders, Boolean includeJsonLd) {

        StringBuilder multipartMessageString = new StringBuilder();
        String boundary = generateBoundary();
        final String SEPARTOR_BOUNDARY = "--" + boundary;
        final String END_SEPARTOR_BOUNDARY = "--" + boundary + "--";
        boolean payloadTester = ((message.getPayloadContent() == null) ? false : (!message.getPayloadContent().isEmpty()));
        boolean signatureTester = ((message.getSignatureContent() == null) ? false : (!message.getSignatureContent().isEmpty()));

        // Append httpHeaders
        String httpHeadersString;
        if (includeHttpHeaders) {
            if (message.getHttpHeaders().isEmpty()) {
                httpHeadersString = setDefaultHttpHeaders(message.getHttpHeaders());
            } else {
                setContentTypeInMultipartMessage(message, SEPARTOR_BOUNDARY);
                httpHeadersString = message.getHttpHeaders()
                        .entrySet()
                        .parallelStream()
                        .map(e -> e.getValue().toString())
                        .collect(Collectors.joining(System.lineSeparator()));
            }
            multipartMessageString.append(httpHeadersString + System.lineSeparator());
            multipartMessageString.append(System.lineSeparator());
        }

        // Append separator boundary
        multipartMessageString.append(SEPARTOR_BOUNDARY + System.lineSeparator());

        // Append headerHeader
        String headerHeaderString;
        if (message.getHeaderHeader().isEmpty()) {
            headerHeaderString = setDefaultHeaders(message.getHeaderContentString(), MultipartMessageKey.NAME_HEADER.label, includeJsonLd);
        } else {
            headerHeaderString = message.getHeaderHeader()
                    .entrySet()
                    .parallelStream()
                    .flatMap(e -> Stream.of(e.getKey() + ": " + e.getValue()))
                    .collect(Collectors.joining(System.lineSeparator()));
        }
        multipartMessageString.append(headerHeaderString + System.lineSeparator());

        // Append headerContent
        multipartMessageString.append(message.getHeaderContentString() + System.lineSeparator());

        // Append payload
        if (payloadTester) {
            // Append separator boundary
            multipartMessageString.append(SEPARTOR_BOUNDARY + System.lineSeparator());

            // Append payloadHeader
            String payloadHeader;
            if (message.getPayloadHeader().isEmpty()) {
                payloadHeader = setDefaultPartHeader(message.getPayloadContent(), MultipartMessageKey.NAME_PAYLOAD.label);
            } else {
                payloadHeader = message.getPayloadHeader()
                        .entrySet()
                        .parallelStream()
                        .flatMap(e -> Stream.of(e.getKey() + ": " + e.getValue()))
                        .collect(Collectors.joining(System.lineSeparator()));
            }
            multipartMessageString.append(payloadHeader + System.lineSeparator());
            multipartMessageString.append(System.lineSeparator());

            // Append payloadContent
            multipartMessageString.append(message.getPayloadContent() + System.lineSeparator());
        }

        // Append signature
        if (signatureTester) {
            // Append separator boundary
            multipartMessageString.append(SEPARTOR_BOUNDARY + System.lineSeparator());

            // Append signatureHeader
            String signatureHeaderString;
            if (message.getSignatureHeader().isEmpty()) {
                signatureHeaderString = setDefaultPartHeader(message.getSignatureContent(), MultipartMessageKey.NAME_SIGNATURE.label);
            } else {
                signatureHeaderString = message.getSignatureHeader()
                        .entrySet()
                        .parallelStream()
                        .flatMap(e -> Stream.of(e.getKey() + ": " + e.getValue()))
                        .collect(Collectors.joining(System.lineSeparator()));
            }
            multipartMessageString.append(signatureHeaderString + System.lineSeparator());
            multipartMessageString.append(System.lineSeparator());

            // Append signatureContent
            multipartMessageString.append(message.getSignatureContent() + System.lineSeparator());
        }

        // Append end separator boundary
        multipartMessageString.append(END_SEPARTOR_BOUNDARY);

        return multipartMessageString.toString();
    }

    private static String setDefaultHttpHeaders(Map<String, String> httpHeaders) {
        StringBuffer defaultContentType = new StringBuffer();
        defaultContentType.append(httpHeaders.getOrDefault(MultipartMessageKey.CONTENT_TYPE.label, "multipart/mixed"));
        httpHeaders.put(MultipartMessageKey.CONTENT_TYPE.label, defaultContentType.toString());

        String defaultHttpHeadersToString = httpHeaders
                .entrySet()
                .parallelStream()
                .flatMap(e -> Stream.of(e.getKey() + ": " + e.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));
        return defaultHttpHeadersToString;
    }

    private static String setDefaultHeaders(String headerContentString, String partName, Boolean includeJsonLD) {
        StringBuffer defaultHeaderHeaderString = new StringBuffer();
        defaultHeaderHeaderString.append(DEFAULT_CONTENT_DISPOSITION + "\"" + partName + "\"" + System.lineSeparator());
        defaultHeaderHeaderString.append(MultipartMessageKey.CONTENT_LENGTH.label + ": " + headerContentString.length() + System.lineSeparator());
        if(includeJsonLD != null && includeJsonLD) {
        	defaultHeaderHeaderString.append(MultipartMessageKey.CONTENT_TYPE.label + ": " + "application/ld+json" + System.lineSeparator());
        } else if(includeJsonLD != null && !includeJsonLD){
        	defaultHeaderHeaderString.append(MultipartMessageKey.CONTENT_TYPE.label + ": " + ContentType.APPLICATION_JSON + System.lineSeparator());
        }
        return defaultHeaderHeaderString.toString();
    }
    
    private static String setDefaultPartHeader(String headerContentString, String partName) {
        StringBuffer defaultHeaderHeaderString = new StringBuffer();
        defaultHeaderHeaderString.append(DEFAULT_CONTENT_DISPOSITION + "\"" + partName + "\"" + System.lineSeparator());
        defaultHeaderHeaderString.append(MultipartMessageKey.CONTENT_LENGTH.label + ": " + headerContentString.length() + System.lineSeparator());
        return defaultHeaderHeaderString.toString();
    }

    private static void setContentTypeInMultipartMessage(MultipartMessage message, String boundary) {
        Optional<Entry<String, String>> contentTypeLine = Optional.empty();
        if (message.getHttpHeaders().containsKey(MultipartMessageKey.CONTENT_TYPE.label)) {
            contentTypeLine = message.getHttpHeaders()
                    .entrySet()
                    .parallelStream()
                    .filter(e -> (predicateLineContentType.test(e.getKey().toLowerCase())))
                    .findFirst();
        }

        if (contentTypeLine.isEmpty()) {
            setDefaultContentType(message, boundary);
        } else {
            setNewBoundaryInContentType(message, boundary, contentTypeLine);
        }
    }

    private static void setNewBoundaryInContentType(MultipartMessage message, String boundary,
                                                    Optional<Entry<String, String>> contentTypeLineEntry) {
        String contentTypeLine = contentTypeLineEntry.get().getValue();
        String contentTypeLineWithNewBoundary = replaceContentTypeWithNewBoundary(boundary, contentTypeLine);
        // Set new Content-Type with the new boundary
        message.getHttpHeaders()
                .entrySet()
                .parallelStream()
                .filter(e -> (predicateLineContentType.test(e.getKey().toLowerCase())))
                .findFirst()
                .get()
                .setValue(contentTypeLineWithNewBoundary);
    }

    private static void setDefaultContentType(MultipartMessage message, String boundary) {
        String contentTypeLineWithNewBoundary = replaceContentTypeWithNewBoundary(boundary, DEFAULT_CONTENT_TYPE);
        message.getHttpHeaders().put(MultipartMessageKey.CONTENT_TYPE.label, contentTypeLineWithNewBoundary);
    }

    private static String replaceContentTypeWithNewBoundary(String boundary, String contentTypeLine) {
        Pattern pattern = Pattern.compile(REGEX_BOUNDARY);
        Matcher matcher = pattern.matcher(contentTypeLine);
        matcher.find();
        StringBuilder stringBuilder = new StringBuilder(matcher.group(2));
        String contentTypeLineWithNewBoundary = contentTypeLine.replace(stringBuilder, boundary.substring(2));
        return contentTypeLineWithNewBoundary;
    }

    private static MultipartMessage createMultipartMessage(List<List<String>> multipartMessageParts, String contentType) {
        
    	MultipartMessageBuilder multipartMessageBuilder = new MultipartMessageBuilder();

        if (contentType != null) {
            Map<String, String> httpHeader = new HashMap<String, String>() {
                private static final long serialVersionUID = -9200584634022265945L;

                {
                    put(MultipartMessageKey.CONTENT_TYPE.label, contentType);
                }
            };
            multipartMessageBuilder.withHttpHeader(httpHeader);
        }

        multipartMessageParts.parallelStream()
                .forEach
                        (
                                part -> {
                                    String partName = getMultipartMessagePartName(part);
                                    Map<String, String> partHeader = getPartHeader(part);
                                    String partContent = getPartContent(part);
									fillMultipartMessage(multipartMessageBuilder, partName, partHeader, partContent);
                                }
                        );
        MultipartMessage multipartMessage = multipartMessageBuilder.build();
        return multipartMessage;
    }

    private static String getMultipartMessagePartName(List<String> part) {
        Pattern pattern = Pattern.compile(REGEX_NAME);
        String lineContentType = part.parallelStream()
                .filter(row -> predicateLineContentDisposition.test(row.toLowerCase())).findFirst()
                .get();
        Matcher matcher = pattern.matcher(lineContentType);
        matcher.find();
        String partName = matcher.group(2).toLowerCase();
        return partName;
    }

    /**
     * Configure predicate lines in order to only extract the headers
     * @param part
     * @return
     */
    private static Map<String, String> getPartHeader(List<String> part) {
        Map<String, String> partHeader = new HashMap<String, String>();

        String partContetDisposition = part.parallelStream().filter(line -> predicateLineContentDisposition.test(line.toLowerCase())).findFirst().get();
        partHeader.put(MultipartMessageKey.CONTENT_DISPOSITION.label, partContetDisposition.split(":")[1].trim());

        String partContentLength = part.parallelStream().filter(line -> predicateLineContentLength.test(line.toLowerCase())).findFirst().get();
        partHeader.put(MultipartMessageKey.CONTENT_LENGTH.label, partContentLength.split(":")[1].trim());

		Optional<String> partContentType = part.parallelStream()
				.filter(line -> predicateLineContentType.test(line.toLowerCase())).findFirst();
		if (partContentType.isPresent()) {
			partHeader.put(MultipartMessageKey.CONTENT_TYPE.label, partContentType.get().split(":")[1].trim());
		}
		
		Optional<String> partContentTransferEncoding = part.parallelStream()
				.filter(line -> predicateLineContentTransferEncoding.test(line.toLowerCase())).findFirst();
		if (partContentTransferEncoding.isPresent()) {
			partHeader.put(MultipartMessageKey.CONTENT_TRANSFER_ENCODING.label, partContentTransferEncoding.get().split(":")[1].trim());
		}

        return partHeader;
    }

    /**
     * Configure predicate lines to skip in order to return only JSON part without headers
     * @param part
     * @return
     */
    private static String getPartContent(List<String> part) {
        OptionalInt startPostionContent = IntStream.range(0, part.size())
                .filter(index ->
                        (
                                (
                                        !predicateLineContentDisposition.test(part.get(index).toLowerCase()) &&
                                        !predicateLineContentLength.test(part.get(index).toLowerCase()) &&
                                        !predicateLineContentTransferEncoding.test(part.get(index).toLowerCase()) &&
                                        !predicateLineContentType.test(part.get(index).toLowerCase()) &&
                                        !predicateLineEmpty.test(part.get(index))
                                )
                        )
                )
                .findFirst();

        String partContent = null;
        if (startPostionContent.isPresent()) {
            partContent = IntStream.range(startPostionContent.getAsInt(), part.size())
                    .mapToObj(index -> part.get(index) + System.getProperty("line.separator"))
                    .collect(Collectors.joining());

        }

        return partContent;
    }

    private static void fillMultipartMessage(MultipartMessageBuilder multipartMessageBuilder, String partName,
                                             Map<String, String> partHeader, String partContent) {

        if (partName.equals(MultipartMessageKey.NAME_HEADER.label)) {
            multipartMessageBuilder.withHeaderHeader(partHeader);
            multipartMessageBuilder.withHeaderContent(partContent.trim());
        } else {
            if (partName.equals(MultipartMessageKey.NAME_PAYLOAD.label)) {
                multipartMessageBuilder.withPayloadHeader(partHeader);
                multipartMessageBuilder.withPayloadContent(partContent.trim());
            } else {
                if (partName.equals(MultipartMessageKey.NAME_SIGNATURE.label)) {
                    multipartMessageBuilder.withSignatureHeader(partHeader);
                    multipartMessageBuilder.withSignatureContent(partContent.trim());
                }
            }
        }
    }

    private static Optional<String> getMessageBoundaryFromContentType(String contentType) {
        String boundary = null;
        Pattern pattern = Pattern.compile(REGEX_BOUNDARY);
        Matcher matcher = pattern.matcher(contentType);
        matcher.find();
        boundary = matcher.group(2);
        return Optional.ofNullable(boundary);
    }

    public static Optional<String> getMessageBoundaryFromMessage(String message) {
        return message.lines()
        		.filter(line -> line.startsWith("--"))
                .findFirst();
    }

    private static List<List<String>> getMultipartMessagesParts(Predicate<String> predicateLineBoundary, String multipart) {
        // Devide multipart message on the lines
        Stream<String> lines = multipart.lines();

        // create list of the lines from the multipart message
        List<String> linesInMultipartMessage = lines.collect(Collectors.toList());

        // Find all boundary postions
        List<Integer> positionBoundaries = findPostionBoundaries(linesInMultipartMessage, predicateLineBoundary);

        // Find the position of the first boundary
        Integer postionStartBoundary = positionBoundaries.parallelStream().findFirst().get();
        // Find the position of the last boundary
        Integer postionLastBoundary = positionBoundaries.parallelStream().reduce((a, b) -> b).get();

        // Prepare list for the spleeting
        List<String> linesMultipartMessagePreparedForSpliting = getLinesMultipartMessagePreparedForSpliting(
                linesInMultipartMessage, postionStartBoundary, postionLastBoundary);

        // create each of the part of the multipart messages to be lists of the lines
        List<List<String>> multipartMessageParts = createMultipartMessageParts(predicateLineBoundary, linesMultipartMessagePreparedForSpliting);

        return multipartMessageParts;
    }

    private static List<String> getLinesMultipartMessagePreparedForSpliting(List<String> linesInMultipartMessage,
                                                                            Integer postionStartBoundary, Integer postionLastBoundary) {
        List<String> linesMultipartMessagePreparedForSpliting = IntStream.range(0, linesInMultipartMessage.size())
                .mapToObj(index ->
                        (
                                index > postionStartBoundary && index < postionLastBoundary) ? linesInMultipartMessage.get(index) : null
                )
                .filter(element -> element != null)
                .collect(Collectors.toList());
        return linesMultipartMessagePreparedForSpliting;
    }

    private static List<Integer> findPostionBoundaries(List<String> linesInMultipartMessage, Predicate<String> predicateLineBoundary) {
        // Find postion of the all boundary
        List<Integer> list = IntStream.range(0, linesInMultipartMessage.size())
                .mapToObj(index ->
                        (
                                predicateLineBoundary.test(linesInMultipartMessage.get(index).replace(System.lineSeparator(), ""))
                        )
                                ? index : null
                )
                .filter(element -> element != null)
                .collect(Collectors.toList());
        return list;
    }

    private static List<List<String>> createMultipartMessageParts(Predicate<String> predicateLineBoundary,
                                                                  List<String> linesMultipartMessagePreparedForSpliting) {
        int[] indexesSepartor =
                Stream.of(IntStream.of(-1), IntStream.range(0, linesMultipartMessagePreparedForSpliting.size())
                        .filter(i -> predicateLineBoundary.test(linesMultipartMessagePreparedForSpliting.get(i))), IntStream.of(linesMultipartMessagePreparedForSpliting.size()))
                        .flatMapToInt(s -> s).toArray();
        return
                IntStream.range(0, indexesSepartor.length - 1)
                        .mapToObj(i -> linesMultipartMessagePreparedForSpliting.subList(indexesSepartor[i] + 1, indexesSepartor[i + 1]))
                        .collect(Collectors.toList());
    }

    private static String generateBoundary() {
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        int count = rand.nextInt(11) + 30;
        IntStream.range(0, count)
                .forEach(i ->
                        buffer.append(BOUNDARY_CHARS[rand.nextInt(BOUNDARY_CHARS.length)])
                );
        return buffer.toString();
    }

    public static String serializeToPlainJson(Object object) throws IOException {
        String serializePlainJson = serializer.serializePlainJson(object);
        return removeTimezoneFromIssued(serializePlainJson);
    }
    
    /**
     * Serialize message to JsonLD format, plus applying time zone handling UTC -> Z 
     * @param object
     * @return
     * @throws IOException
     */
    public static String serializeToJsonLD(Object object) throws IOException {
        String serializePlainJson = serializer.serialize(object);
        return removeTimezoneFromIssued(serializePlainJson);
    }


    private static String removeTimezoneFromIssued(String objectJson) {
        // 2020-08-27T09:30:41.962UTC -> 2020-08-27T09:30:41.962Z
        // The first one is generated by jackson problems with Timezone!!!
        String[] lines = objectJson.split(System.lineSeparator());
        if(null != lines && lines.length >0) {
            for (String line : lines) {
                if (line.contains("ids:issued") && line.contains("UTC")) {
                    String originalLine = line;
                    line = line.replaceFirst("UTC", "Z");
                    return objectJson.replace(originalLine, line);
                }
            }
        }
        return objectJson;
    }
    
}
