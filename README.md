# nl-export-errors-db

## Introduction

This is a program to export errors from database, in a very specific format.

Download the [latest release](https://github.com/SebastienRichert/nl-export-errors-db/releases/latest).

## Usage

Arguments: 

-db: input errors database (mandatory)

-out: output folder (mandatory)

-minTimeMs: the minimun time in milliseconds (optional)

-maxTimeMs: the maximum time in milliseconds (optional)

## Example

For example with a 1 hour test:

- export all entries:
prompt>java -jar  ExportErrorsDB.jar -db "<path-to-test-result>\errors.db" -out "<output-folder>" 

- export only entries after 30 minutes:
prompt>java -jar  ExportErrorsDB.jar -db "<path-to-test-result>\errors.db" -out "<output-folder>" -minTime 1800000

- export only entries before 30 minutes: 
prompt>java -jar  ExportErrorsDB.jar -db "<path-to-test-result>\errors.db" -out "<output-folder>" -maxTime 1800000

- export only entries between 10 minutes, and 50 minutes: 
prompt>java -jar  ExportErrorsDB.jar -db "<path-to-test-result>\errors.db" -out "<output-folder>" -minTime 600000 -max 3000000


## Output

There is one folder per error entry.
The name of the folder is : <userID>_<userInstance>_<<wbr>iteration>_<loadGeneratorId>_<<wbr>timestamp>

with :
- <userID> an internal unique ID for this VirtualUser, a 36 characters string, for example f745febc-3f0e-460c-bfd8-<wbr>a9a3a75d1b97
- <userInstance> the instance of this virtual user for the load generator, an integer, starting from 0
- <iteration> the current iteration name : Init, or Actions1, or Actions2, ... or End
- <loadGeneratorId> the unique ID of the load generator, an integer,
- <timestamp> the timestamp of the time between test start and occurrence of error, in millisecond, for example 60000 if error occured after 1 minute.

For example, if virtual user named "VU" has the UID "f745febc-3f0e-460c-bfd8-<wbr>a9a3a75d1b97", has an error in the first iteration of the Action container
for first load generator after 1 minute, then the folder associated to this error entry will be :
f745febc-3f0e-460c-bfd8-<wbr>a9a3a75d1b97_0_Action1_0_60000

The folder contains 5 files :
- request.txt for details of the request sent to the server,
- response.txt for details of the server response,
- previous-request.txt for details of the previous request sent to the server,
- previous-response.txt for details of the response of the previous request sent to the server,
- details.txt with the below format :
	* timestamp: 60000
	* userID: f745febc-3f0e-460c-bfd8-<wbr>a9a3a75d1b97
	* userInstance: 0
	* iteration: Action1
	* isRequestError: false
	* responseCode: 200
	* loadGeneratorID: 0
	* population: Population1
	* transactionId: 0
	* duration: 1000
	* ttfb: 1000
	* size: 1000
	* previousRequestID: 13
	* nbAssertions: 2
	* assertion-key:size.not.equal value: [247, 5555]
	* assertion-key:size.lower value: [247, 3333]

If the response is not stored, then the content of the response will be the same as shown in NeoLoad :

<< Response not stored. NeoLoad did not save the details of this error. For more information, see the first occurrences of this error.>>

## Limitations

The program is a Windows 32 bits executable to be used with Java 7 or higher.

This program only work from NeoLoad version 5.5.0 to version 6.0 (included).
We do not guarantee compatibility with future NeoLoad version : program might not work from NeoLoad 6.0 because internal errors database format might change.

Here are the technical limits of this program :
- we won't have the name of the virtual user : only an ID is provided "userID". If you want to retrieve the name of virtual user given the unique ID, you will have to open file repository.xml inside file project.zip of the test result, and search in the XML file the ID. It should be an attribute of a "virtual-user" XML node, which contains the human readable virtual user name.
- we won't have the name of the load generator : only an ID is provided "loadGeneratorID". If you want to retrieve the name of the load generator given the unique ID, you will have to open file project.xml inside the zip file project.zip of the test result, and search in the XML file the ID. It should be an attribute of a "objectids-entry" XML node, with attribute "type" equals "LG_HOST", and containing the real name of the load generator.
- we won't have the name of the previons request : only an ID is provided "previousRequestID". If you want to retrieve the name of the load generator given the unique ID, you will have to open file project.xml inside the zip file project.zip of the test result, and search in the XML file the ID.
- we won't have a human readable details of the assertion as shown in NeoLoad.
- we don't have the time is absolute value because the error database only contains relative times.


 
 