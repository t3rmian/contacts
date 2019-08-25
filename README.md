# contacts

This is a contacts loader demo project in Java which provides a way to import
customer contacts from CSV and XML files into a database. The database structure
consists of the following tables:

CUSTOMERS Table:
- ID
- NAME
- SURNAME
- Age (NULL)

CONTACTS:
- ID
- ID_CUSTOMER
- TYPE (integer - 0 - unknown, 1 - email, 2 - phone, 3- jabber)
- CONTACT

Sample input files can be seen in test resources.
The CSV contains Name, Surname, Age, City, Contact1, Contact2, ...

Technical restrictions: input files can be very large and should not be loaded
into memory at once. Data access layer should use JDBC only.

### Food for thought
####### Application architecture
The architecture is divided into 3 parts:
- data - output POJO for database inserts
- parsing logic - without any special DTOs, as the data structure is not complex
- data access layer - manages the connection and inserts records into a database

####### Application parametrization

The application is parametrized in two ways:
- through `data-source.properties` file for configuring database connection during compilation
- through run parameters for passing input file path, file type, batch size or overriding connection config (spring-like)

If you don't specify connection configuration, a standard one will be applied. H2 file database `testdb` will be created
in the same directory (initialized if necessary), accessible with user `sa` and an empty password.

####### Passing input file information

The program will import contacts based on file information provided during application start, through command line parameters (file path, type).
Run `java --jar contacts-loader.jar` for usage.

The input stream is passed directly to the parser, therefore it's possible
to load the data from different sources. E.g. an import from url has been
implemented to verify this feature (check if the path is an url, if so, open the URL stream).

####### Contact type recognition

In case of XML the contact type can be parsed based on node tag values inside `<contacts></contacts>` node.
In case of CSV the scopes of contact types are intersecting (e.g email contact can usually be treated as Jabber contact).
It is impossible to guess some contacts as a human. If this feature is required (we could set the type to UNKNOWN),
it's best to prioritize some types like email->phone->jabber->unknown and document it.

### Implementation
CSV parsing is a custom implementation due to parsing simplicity and performance overhead of external libs.
XML parsing uses SAX parser (not DOM, not StAX) for sequential parsing (memory optimal) and sequential validation.
A simple `customer_contacts.xsd` schema has been defined for validation purposes based on sample input data.

Database inserts are done in batches of user-preferred sizes to improve the performance.
In case of errors a whole batch of users and contacts will be rolled back.
Such records may be valid for re-processing after correction. Batch inserts can be disabled by setting
the `batchSize` parameter to 1.

####### Test coverage
There are a total of 70% lines covered by unit and integration tests. The main logic (without the UI) has an 89% coverage. 

### Notes
1. In case of additional column constraints (like length, min size, etc.), a data validator would come handy to check each record before bulk insert. I would use *JSR 380* here.
2. Depending on the context, some information might be better stored in a sanitized format (e.g. phone number).
3. If the errors, should be saved into a file (e.g. for reprocessing purposes) there is a handy `RecordErrorHandled` interface defined.

### External libraries
Compile scope:
- org.slf4j.slf4j-api,slf4j-log4j12 - standard facade for logging (MIT)
- com.h2database.h2 - H2 database - in memory database for tests and file database for development purposes (MPL 2.0 or EPL 1.0)
- commons-validator.commons-validator - small library for email validation (Apache 2.0)
- org.jxmpp.jxmpp-jid - package for Jabber ID validation due to it's complexity (Apache 2.0)
- commons-cli.commons-cli - command line parser for quick user interface set-up (Apache 2.0)

Test scope:
- org.junit.jupiter.junit-jupiter-api,junit-jupiter-engine - JUnit5 (EPL 2.0)
- org.hamcrest.hamcrest-all - Hamcrest matchers (BSD 2-clause)
- org.mockito.mockito-core - Mockito (MIT)