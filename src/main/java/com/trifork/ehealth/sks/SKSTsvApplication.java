package com.trifork.ehealth.sks;


import static java.nio.file.Files.writeString;

import ca.uhn.fhir.context.FhirContext;
import com.google.common.io.ByteSource;
import com.joutvhu.fixedwidth.parser.FixedParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.hl7.fhir.r4.model.CodeSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

public class SKSTsvApplication implements CommandLineRunner {

  private static Logger LOG = LoggerFactory
      .getLogger(SKSTsvApplication.class);

  public static void main(String[] args) {
    LOG.info("STARTING THE APPLICATION");
    SpringApplication.run(SKSTsvApplication.class, args);
    LOG.info("APPLICATION FINISHED");
  }


  @Override
  public void run(String... args) throws IOException, InterruptedException {

    var uri = URI.create("https://filer.sundhedsdata.dk/sks/data/skscomplete/SKScomplete.txt");
    var request = HttpRequest.newBuilder(uri).build();
    var content = HttpClient.newHttpClient().send(request, BodyHandlers.ofByteArray()).body();

    var lines = ByteSource.wrap(content).asCharSource(Charset.forName("windows-1252")).readLines();

    var sksEntries = FixedParser.parser()
        .parse(SKSEntry.class, lines.stream());

    var sksCodeSystem = new CodeSystem()
        .setCopyright("SDS").setVersion("").setUrl("https://sundhedsdatastyrelsen.dk/sks");
    sksEntries.map(SKSEntry::asConceptDefinitionComponent)
        .forEach(sksCodeSystem::addConcept);
    sksCodeSystem.setId("sks");

    var parser = FhirContext.forR4().newJsonParser();
    var csAsString = parser
        .encodeResourceToString(sksCodeSystem.setId("sks"));
    writeString(Path.of("sks.json"), csAsString);
  }
}