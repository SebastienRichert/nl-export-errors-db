package com.neotys.errors.export;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

class ErrorEntry {

	private final static String responseNotStoredMessage = "If the response is not stored, then the content of the response will be the same as shown in NeoLoad :\n<< Response not stored. NeoLoad did not save the details of this error. For more information, see the first occurrences of this error.>>";

	final int pageID;
	final int errorId;
	final int timestamp;
	final int userInstance;
	final String iteration;
	final boolean isRequestError;
	final String responseCode;
	String requestStatusLine;
	String requestMessage;
	String requestHeaders;
	final String response;
	final int previousObjectID;
	final String previousResponseCode;
	String previousRequestStatusLine;
	String previousRequestMessage;
	String previousRequestHeaders;
	final String previousResponse;
	final int loadGeneratorID;
	final String population;
	final String userID;
	final String pageName;
	final int transactionID;
	final int duration;
	final int ttfb;
	final int size;
	final int nbAssertions;
	final String[] assertions;

	ErrorEntry(final DataInput dataIn) throws IOException {
		pageID = dataIn.readInt();
		errorId = dataIn.readInt();
		timestamp = dataIn.readInt();
		userInstance = dataIn.readInt();
		iteration = VirtualUserStep.make(dataIn.readInt()).toString() + dataIn.readInt();
		isRequestError = dataIn.readBoolean();
		responseCode = IO.readString(dataIn);
		requestStatusLine = IO.readString(dataIn);
		if (requestStatusLine == null) {
			requestStatusLine = "";
		}
		requestMessage = IO.readString(dataIn);
		if (requestMessage == null) {
			requestMessage = "";
		}
		requestHeaders = IO.readString(dataIn);
		if (requestHeaders == null) {
			requestHeaders = "";
		}
		String responseStatusLine = IO.readString(dataIn);
		if (responseStatusLine == null) {
			responseStatusLine = "";
		}
		final String responseData = IO.readString(dataIn);

		String responseHeaders = IO.readString(dataIn);
		if (responseHeaders == null) {
			responseHeaders = "";
		}
		if (responseData == null) {
			response = responseNotStoredMessage;
		} else {
			response = (new StringBuilder()).append(responseStatusLine).append("\n").append(responseHeaders).append("\n").append(
					responseData).toString();
		}
		previousObjectID = dataIn.readInt();
		previousResponseCode = IO.readString(dataIn);
		previousRequestStatusLine = IO.readString(dataIn);
		if (previousRequestStatusLine == null) {
			previousRequestStatusLine = "";
		}
		previousRequestMessage = IO.readString(dataIn);
		if (previousRequestMessage == null) {
			previousRequestMessage = "";
		}
		previousRequestHeaders = IO.readString(dataIn);
		if (previousRequestHeaders == null) {
			previousRequestHeaders = "";
		}
		String previousResponseStatusLine = IO.readString(dataIn);
		if (previousResponseStatusLine == null) {
			previousResponseStatusLine = "";
		}
		String previousResponseData = IO.readString(dataIn);
		if (previousResponseData == null) {
			previousResponseData = "";
		}
		String previousResponseHeaders = IO.readString(dataIn);
		if (previousResponseHeaders == null) {
			previousResponseHeaders = "";
		}
		previousResponse = (new StringBuilder()).append(previousResponseStatusLine).append("\n").append(previousResponseHeaders).append(
				"\n").append(previousResponseData).toString();

		loadGeneratorID = dataIn.readInt();
		population = IO.readString(dataIn);
		userID = IO.readString(dataIn);
		pageName = IO.readString(dataIn);
		transactionID = dataIn.readInt();
		duration = dataIn.readInt();
		ttfb = dataIn.readInt();
		size = dataIn.readInt();
		nbAssertions = dataIn.readByte();
		assertions = new String[nbAssertions];
		for (int i = 0 ; i < nbAssertions ; i++) {
			final String key = IO.readString(dataIn);
			final Number runtimeElementsSize = NumberType.readNumber(dataIn);
			final String[] runtimeElements = new String[runtimeElementsSize.intValue()];
			for (int j = 0 ; j < runtimeElementsSize.intValue() ; j++) {
				runtimeElements[j] = IO.readString(dataIn);
			}
			assertions[i] = "assertion-key " + key + " value " + Arrays.toString(runtimeElements);
		}
	}

	private final File getErrorEntryFolder(final File outputFolder) {
		final String folderName = userID + "_" + userInstance + "_" + iteration + "_" + loadGeneratorID + "_" + timestamp;
		File folder = new File(outputFolder, folderName);
		if (!folder.exists()) {
			folder.mkdir();
			return folder;
		}
		int i = 1;
		String newName = folderName + "_" + i;
		while ((new File(outputFolder, newName)).exists()) {
			i++;
			newName = folderName + "_" + i;
		}
		folder = new File(outputFolder, newName);
		folder.mkdir();
		return folder;
	}

	public void writeFiles(final File outputFolder) throws IOException {
		final File errorEntryFolder = getErrorEntryFolder(outputFolder);

		writeHTTPContentFile(errorEntryFolder, "request.txt", requestStatusLine + "\n"+ requestHeaders + "\n" + requestMessage);
		writeHTTPContentFile(errorEntryFolder, "response.txt", response);
		writeHTTPContentFile(errorEntryFolder, "previous-request.txt", previousRequestStatusLine + "\n" + previousRequestHeaders + "\n" + previousRequestMessage);
		writeHTTPContentFile(errorEntryFolder, "previous-response.txt", previousResponse);

		writeErrorEntryDetailsFile(errorEntryFolder);
	}

	private static void writeHTTPContentFile(final File errorEntryFolder, final String fileName, final String content) throws IOException {
		final File file = new File(errorEntryFolder, fileName);
		file.createNewFile();
		Files.write(Paths.get(file.getPath()), content.getBytes(), StandardOpenOption.WRITE);
	}

	private void writeErrorEntryDetailsFile(final File errorEntryFolder) throws IOException {
		final File file = new File(errorEntryFolder, "details.txt");
		file.createNewFile();
		final StringBuilder sb = new StringBuilder();
		sb.append("timestamp :").append(timestamp).append("\n");
		sb.append("userID :").append(userID).append("\n");
		sb.append("userInstance :").append(userInstance).append("\n");
		sb.append("iteration :").append(iteration).append("\n");
		sb.append("isRequestError :").append(isRequestError).append("\n");
		sb.append("responseCode :").append(responseCode).append("\n");
		sb.append("previousObjectID :").append(previousObjectID).append("\n");
		sb.append("previousResponseCode :").append(previousResponseCode).append("\n");
		sb.append("loadGeneratorID :").append(loadGeneratorID).append("\n");
		sb.append("population :").append(population).append("\n");
		if (pageName != null) {
			sb.append("pageName :").append(pageName).append("\n");
		}
		sb.append("pageID :").append(pageID).append("\n");
		sb.append("transactionID :").append(transactionID).append("\n");
		sb.append("duration :").append(duration).append("\n");
		sb.append("ttfb :").append(ttfb).append("\n");
		sb.append("size :").append(size).append("\n");

		sb.append("errorId :").append(errorId).append("\n");
		sb.append("nbAssertions :").append(nbAssertions).append("\n");
		for (final String assersion : assertions) {
			sb.append(assersion).append("\n");
		}

		Files.write(Paths.get(file.getPath()), sb.toString().getBytes(), StandardOpenOption.WRITE);
	}
}
