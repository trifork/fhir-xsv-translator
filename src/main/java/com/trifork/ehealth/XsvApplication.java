package com.trifork.ehealth;

import ca.uhn.fhir.context.FhirContext;
import com.opencsv.bean.CsvToBeanBuilder;
import com.trifork.ehealth.npu.Row;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.hl7.fhir.r4.model.CodeSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class XsvApplication
    implements CommandLineRunner {

  private static Logger LOG = LoggerFactory
      .getLogger(XsvApplication.class);

  public static void main(String[] args) {
    LOG.info("STARTING THE APPLICATION");
    SpringApplication.run(XsvApplication.class, args);
    LOG.info("APPLICATION FINISHED");
  }

  @Override
  public void run(String... args) throws IOException, InterruptedException {

    var uri = URI.create("https://labterm.dk/Download/DownloadTable/NPUdefinitionEN211028.csv");
    var request = HttpRequest.newBuilder(uri).build();
    var content = HttpClient.newHttpClient().send(request, BodyHandlers.ofByteArray()).body();

    List<Row> rows = new CsvToBeanBuilder(
        new StringReader(new String(content, "windows-1252")))
        .withSkipLines(1)
        .withIgnoreQuotations(false)
        .withSeparator(';')
        .withType(Row.class)
        .build()
        .parse();

    var npuCodeSystem = new CodeSystem()
        .setCopyright("https://www.npu-terminology.org/")
        .setVersion("EN211028");

    rows.stream().map(Row::asConceptDefinitionComponent).forEach(npuCodeSystem::addConcept);

    var csAsString = FhirContext.forR4().newJsonParser().encodeResourceToString(npuCodeSystem);

    Files.writeString(Path.of("output.json"),csAsString);
  }
}