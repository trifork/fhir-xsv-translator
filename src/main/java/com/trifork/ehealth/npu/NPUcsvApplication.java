package com.trifork.ehealth.npu;

import static java.nio.file.Files.writeString;

import ca.uhn.fhir.context.FhirContext;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NPUcsvApplication
    implements CommandLineRunner {

  public static final String COPYRIGHT = "The International Union of Pure and Applied Chemistry (IUPAC) and the International Federation of Clinical Chemistry and laboratory medicine (IFCC)";
  public static final String VERSION = "EN211028";
  public static final String SYSTEM = "https://www.npu-terminology.org/";

  private static Logger LOG = LoggerFactory
      .getLogger(NPUcsvApplication.class);

  public static void main(String[] args) {
    LOG.info("STARTING THE APPLICATION");
    SpringApplication.run(NPUcsvApplication.class, args);
    LOG.info("APPLICATION FINISHED");
  }

  @Override
  public void run(String... args) throws IOException, InterruptedException {

    var parser = FhirContext.forR4().newJsonParser();

    var valueSets = createNPUValueSets();

    valueSets.stream().forEach(valueSet -> {

      try {
        writeString(Path.of(valueSet.getId() + ".json"),
            parser.encodeResourceToString(valueSet));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    var npuCodeSystem = createNPUCodeSystem();
    var csAsString = parser
        .encodeResourceToString(npuCodeSystem.setId("npu"));
    writeString(Path.of("npu.json"), csAsString);

  }

  private Collection<ValueSet> createNPUValueSets() throws IOException, InterruptedException {
    var uri = URI.create("https://labterm.dk/Download/DownloadTable/NPUlistEN211028.csv");
    var request = HttpRequest.newBuilder(uri).build();
    var content = HttpClient.newHttpClient().send(request, BodyHandlers.ofByteArray()).body();

    List<NPUListEntry> npuListEntries = new CsvToBeanBuilder(
        new StringReader(new String(content, "windows-1252")))
        .withSkipLines(1)
        .withIgnoreQuotations(false)
        .withSeparator(';')
        .withType(NPUListEntry.class)
        .build()
        .parse();

    npuListEntries.stream().forEach(npuListEntry -> {
      var valueSet = valueSetHashMap.computeIfAbsent(npuListEntry.getListCodeValue(),
          (k) -> {
            var v = new ValueSet()
                .setDate(npuListEntry.getListChangeDate())
                .setCopyright(COPYRIGHT);
                v.getCompose().addInclude(new ConceptSetComponent().setSystem(SYSTEM));
            v.setId(k);
            return v;
          });
      valueSet.getCompose().getIncludeFirstRep().addConcept(npuListEntry.asConceptSetComponent());
    });
    return valueSetHashMap.values();
  }

  private HashMap<String, ValueSet> valueSetHashMap = new HashMap<>();

  private CodeSystem createNPUCodeSystem() throws IOException, InterruptedException {
    var uri = URI.create("https://labterm.dk/Download/DownloadTable/NPUdefinitionEN211028.csv");
    var request = HttpRequest.newBuilder(uri).build();
    var content = HttpClient.newHttpClient().send(request, BodyHandlers.ofByteArray()).body();

    List<NPUDefinitionEntry> npuDefinitionEntries = new CsvToBeanBuilder(
        new StringReader(new String(content, "windows-1252")))
        .withSkipLines(1)
        .withIgnoreQuotations(false)
        .withSeparator(';')
        .withType(NPUDefinitionEntry.class)
        .build()
        .parse();

    var npuCodeSystem = new CodeSystem()
        .setCopyright(COPYRIGHT)
        .setVersion(VERSION)
        .setUrl(SYSTEM)
        ;

    npuDefinitionEntries.stream().map(NPUDefinitionEntry::asConceptDefinitionComponent)
        .forEach(npuCodeSystem::addConcept);
    npuCodeSystem.setId("npu");

    return npuCodeSystem;
  }
}