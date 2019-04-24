package com.neotys.errors.export;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author srichert
 */
public class Main {
	
	private static final String MIN_TIME_MS = "minTimeMs";
	private static final String MAX_TIME_MS = "maxTimeMs";

	public static void main(final String[] args) {
		final CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(getCMDOptions(), args);
		} catch (final ParseException exp) {
			System.err.println("Invalid syntax	: " + exp.getMessage());
			System.exit(1);
			return;
		}

		if (cmd == null || !cmd.hasOption("db") || !cmd.hasOption("out")) {
			System.err.println("Invalid syntax	: argument -db and -out are expected.");
			System.exit(1);
			return;
		}

		final String filePath = cmd.getOptionValue("db");
		final File outputFolder = new File(cmd.getOptionValue("out"));
		if (!outputFolder.exists()) {
			outputFolder.mkdir();
		}
		final long minTimeMs;
		if(cmd.hasOption(MIN_TIME_MS)){
			final String minTimeMsString = cmd.getOptionValue(MIN_TIME_MS);
			if(minTimeMsString == null){
				System.err.println("Invalid syntax: argument -" + MIN_TIME_MS + " needs to have a time in milliseconds");
				System.exit(1);
				return;
			}
			try{
				minTimeMs = Long.parseLong(minTimeMsString);
				System.out.println("-" + MIN_TIME_MS + "=" + minTimeMs);
			} catch (final NumberFormatException nfe){
				System.err.println("Invalid syntax: argument -" + MIN_TIME_MS + " is not a Long: " + minTimeMsString + ". " + nfe.getMessage());
				System.exit(1);
				return;
			}			
		} else {
			minTimeMs = -1;
		}
		final long maxTimeMs;
		if(cmd.hasOption(MAX_TIME_MS)){
			final String maxTimeMsString = cmd.getOptionValue(MAX_TIME_MS);
			if(maxTimeMsString == null){
				System.err.println("Invalid syntax: argument -" + MAX_TIME_MS + " needs to have a time in milliseconds");
				System.exit(1);
				return;
			}
			try{
				maxTimeMs = Long.parseLong(maxTimeMsString);
				System.out.println("-" + MAX_TIME_MS + "=" + maxTimeMs);
			} catch (final NumberFormatException nfe){
				System.err.println("Invalid syntax: argument -" + MAX_TIME_MS + " is not a Long: " + maxTimeMsString + ". " + nfe.getMessage());
				System.exit(1);
				return;
			}			
		} else {
			maxTimeMs = -1;
		}
		
		System.out.println("Start reading errors db: " + filePath);
		
		final File errorsDB = new File(filePath);
		InputStream is = null;
		DataInput dataIn = null;
		try {
			is = new BufferedInputStream(new FileInputStream(errorsDB));
			dataIn = new DataInputStream(is);

			try {
				final String version = IO.readString(dataIn);
				System.out.println("Database version = " + version);
				int counterRead = 0;
				int counterWrite = 0;
				try {
					while (true) {
						final ErrorEntry errorEntry = new ErrorEntry(dataIn);
						counterRead++;
						// Check MIN_TIME_MS border
						if(minTimeMs != -1 && errorEntry.timestamp < minTimeMs){
							continue;
						}
						if(maxTimeMs != -1 && errorEntry.timestamp > maxTimeMs){
							continue;
						}
						errorEntry.writeFiles(outputFolder);
						counterWrite++;
						
					}
				} catch (final EOFException eofe) {
					System.out.println("End of file reached : " + counterRead + " entries has been read, " + counterWrite + " entries has been written.");
				}

			} catch (final IOException ioe) {
				System.out.println("IOException : an error occured while reading or writing data :" + ioe.getMessage());
			}

		} catch (final FileNotFoundException fnfe) {
			System.out.println("FileNotFoundException : the file does not exist, or is a directory rather than a regular file, or for some other reason cannot be opened for reading.\n"
					+ fnfe.getMessage());
		} catch (final SecurityException se) {
			System.out.println("SecurityException : a security manager exists and its checkRead method denies read access to the file.\n"
					+ se.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}

	}



	@SuppressWarnings("static-access")
	private static Options getCMDOptions() {
		final Option db = OptionBuilder.withArgName("file").hasArg().withDescription("errors.db file").create("db");
		final Option out = OptionBuilder.withArgName("file").hasArg().withDescription("output directory").create("out");
		final Option minTimeMs = OptionBuilder.withArgName("long").hasArg().withDescription("min time in milliseconds").create(MIN_TIME_MS);
		final Option maxTimeMs = OptionBuilder.withArgName("long").hasArg().withDescription("max time in milliseconds").create(MAX_TIME_MS);

		final Options options = new Options();
		options.addOption(db);
		options.addOption(out);
		options.addOption(minTimeMs);
		options.addOption(maxTimeMs);
		return options;
	}
}
