# Spring Boot Kotlin Axon Project

This project is a Spring Boot application written in Kotlin, using Axon for event sourcing and a PostgreSQL database for persistence.

## Prerequisites

- Docker
- Java 21 or later

## Setting Up PostgreSQL with Docker

To set up a PostgreSQL database using Docker, run the following command:

```sh
docker run --name forwarder-postgres -e POSTGRES_USER=appl -e POSTGRES_PASSWORD=appl -e POSTGRES_DB=forwarder-es -p 5432:5432 -d postgres
```

This command:
- Creates a new PostgreSQL container named `forwarder-postgres`
- Sets up a user `appl` with password `appl`
- Creates an empty database `forwarder-es` owned by `appl`
- Exposes PostgreSQL on port `5432`

To verify the database setup, you can connect to the PostgreSQL container:

```sh
docker exec -it forwarder-postgres psql -U appl -d forwarder-es
```

## Database Configuration in `application.yml`

Configure the database connection in your `src/main/resources/application.yml` file in case you are not using the default values:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/forwarder-es
    username: appl
    password: appl
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
```

## Building and Running the Application

### Compiling and Packaging

To compile and package the application using Gradle, run:

```sh
gradle build
```

This will generate a `JAR` file in the `build/libs` directory.

### Running the Application

Run the application from the command line using:

```sh
java -jar build/libs/<your-app-name>.jar
```

Alternatively, you can run it directly using Gradle:

```sh
gradle bootRun
```

## Testing the Application

To test the application, you can submit reports using the following `curl` commands.

### Submitting a GKV Report

```sh
curl --location 'http://localhost:8080/api/v1/reports' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'reportType=gkv' \
--data-urlencode 'xmlContent=<?xml version="1.0" encoding="ISO-8859-1"?>
<AGTOSV Versionsnummer="1.0.0" 
xsi:schemaLocation="http://www.gkv-datenaustausch.de/XMLSchema/SV_Header_AGTOSV/1.0 SV_Header_AGTOSV_RVBEA.xsd" 
xmlns="http://www.gkv-datenaustausch.de/XMLSchema/SV_Header_AGTOSV/1.0" 
xmlns:ant="http://www.rvbea.de/XMLSchema/RVBEA_Antwort" 
xmlns:rvb="http://www.rvbea.de/XMLSchema/RVBEA_Basis" 
xmlns:rvbea="http://www.rvbea.de/XMLSchema/Steuerungssatz_RVBEA" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<SendungHeaderAGTOSV Versionsnummer="1.0.0">
		<Kommunikationsdaten>
			<Prod_ID>0000001</Prod_ID>
			<Mod_ID>00000001</Mod_ID>
			<Name_Dateiersteller>AGTOSV Header GmbH</Name_Dateiersteller>
			<PLZ_Betrieb>97084</PLZ_Betrieb>
			<Ort_Betrieb>Würzburg</Ort_Betrieb>
			<Strasse_Betrieb>Berner Strasse</Strasse_Betrieb>
			<Hausnummer_Betrieb>1</Hausnummer_Betrieb>
			<Anrede_Ansprechpartner>M</Anrede_Ansprechpartner>
			<Name_Ansprechpartner>Mustermann</Name_Ansprechpartner>
			<Telefonnummer_Ansprechpartner>0931/4655831</Telefonnummer_Ansprechpartner>
			<Faxnummer_Ansprechpartner>0931/4655858</Faxnummer_Ansprechpartner>
			<EMail_Ansprechpartner>mustermann@agtosv-header.de</EMail_Ansprechpartner>
		</Kommunikationsdaten>
	</SendungHeaderAGTOSV>
	<Sendungs_Body>
		<rvbea:Rvbea_AGTORV>
			<ant:DXEB Versionsnummer="3.0.0">
				<ant:Steuerungsdaten>
					<rvb:Ds_Id>1</rvb:Ds_Id>
					<rvb:Bbnras>55150792</rvb:Bbnras>
					<rvb:Bbnrvu>55150053</rvb:Bbnrvu>
					<rvb:Bezugs_Id>1182514</rvb:Bezugs_Id>
					<rvb:Vsnr>11010190U025</rvb:Vsnr>
					<rvb:Azvu>ZUZA-DS01-AG1</rvb:Azvu>
					<rvb:Anfgr>ZUZA</rvb:Anfgr>
					<rvb:Anfgr_Vers>1.0.0</rvb:Anfgr_Vers>
				</ant:Steuerungsdaten>
			</ant:DXEB>
		</rvbea:Rvbea_AGTORV>
	</Sendungs_Body>
</AGTOSV>'
```

### Submitting a KVPV Report

```sh
curl --location 'http://localhost:8080/api/v1/reports' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'reportType=kvpv' \
--data-urlencode 'xmlContent=<?xml version="1.0" encoding="UTF-8"?>
<KVPVDatenuebermittlung>
    <MitteilungspflichtigeStelle>
        <Identifikationsmerkmal>DE123456789</Identifikationsmerkmal>
        <Steuernummer>123/456/78901</Steuernummer>
        <Name>Example Company</Name>
        <KennungType>ELAN123</KennungType>
        <KontaktdatenType>
            <Telefon>+49 123 4567890</Telefon>
            <Email>contact@example.com</Email>
        </KontaktdatenType>
    </MitteilungspflichtigeStelle>
    <Versicherungsvertraege>
        <Versicherungsvertrag>
            <VertragsDaten>
                <OrdnungskriteriumVersicherungType>123456</OrdnungskriteriumVersicherungType>
                <VertragsnummerVersicherungType>789012</VertragsnummerVersicherungType>
            </VertragsDaten>
            <Widerspruch>false</Widerspruch>
        </Versicherungsvertrag>
    </Versicherungsvertraege>
</KVPVDatenuebermittlung>'
```

## Stopping and Removing the PostgreSQL Container

To stop the PostgreSQL container, run:

```sh
docker stop forwarder-postgres
```

To remove it completely:

```sh
docker rm forwarder-postgres
```

