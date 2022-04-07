package com.trifork.ehealth.sks;


import static java.nio.file.Files.writeString;

import ca.uhn.fhir.context.FhirContext;
import com.google.common.io.Files;
import com.joutvhu.fixedwidth.parser.FixedParser;
import com.trifork.ehealth.npu.NPUDefinitionEntry;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
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
  public void run(String... args) throws IOException {

    var lines = Files.asCharSource(
        new File("src/main/resources/SKScomplete.txt"),
        Charset.forName("windows-1252")).readLines();

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