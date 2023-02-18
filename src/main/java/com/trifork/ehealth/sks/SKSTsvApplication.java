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
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        sksCodeSystem.setId("sks");
        Collection<CodeSystem.ConceptDefinitionComponent> sksEntriesAsList = sksEntries.map(SKSEntry::asConceptDefinitionComponent).collect(Collectors.toList());

        sksEntriesAsList = removeDuplicates(sksEntriesAsList);
//        sksEntriesAsList.forEach(sksCodeSystem::addConcept);


/*
        var sksEntriesAsList = new LinkedList<CodeSystem.ConceptDefinitionComponent>();
        sksEntriesAsList.add(new CodeSystem.ConceptDefinitionComponent().setCode("A"));
        sksEntriesAsList.add(new CodeSystem.ConceptDefinitionComponent().setCode("AB"));
        sksEntriesAsList.add(new CodeSystem.ConceptDefinitionComponent().setCode("AAB"));
        sksEntriesAsList.add(new CodeSystem.ConceptDefinitionComponent().setCode("B"));
        sksEntriesAsList.add(new CodeSystem.ConceptDefinitionComponent().setCode("ABB"));
        sksEntriesAsList.add(new CodeSystem.ConceptDefinitionComponent().setCode("B1"));

*/


        sksEntriesAsList.forEach(e -> {
            var listOfConcepts = sksCodeSystem.getConcept();

            var wentDeep = false;
            for (CodeSystem.ConceptDefinitionComponent c : listOfConcepts) {
                if (e.getCode().startsWith(c.getCode())) {
                    goDeep(e, c.getConcept());
                    wentDeep = true;
                    return;
                }
            }
            if (!wentDeep)
                sksCodeSystem.addConcept(e);

        });



        var parser = FhirContext.forR4().newJsonParser();
        var csAsString = parser
                .encodeResourceToString(sksCodeSystem.setId("sks"));
        writeString(Path.of("sks.json"), csAsString);
    }

    private Collection<CodeSystem.ConceptDefinitionComponent> removeDuplicates(Collection<CodeSystem.ConceptDefinitionComponent> sksEntriesAsList) {

        //var possibleDuplicates = sksEntriesAsList.stream().filter(c -> c.getProperty().stream().anyMatch(p -> "DVal".equalsIgnoreCase(p.getCode()) && p.getValueIntegerType().getValue() == 3)).collect(Collectors.toMap(CodeSystem.ConceptDefinitionComponent::getCode, Function.identity()));

        return sksEntriesAsList.stream()
                .collect(Collectors.toMap(CodeSystem.ConceptDefinitionComponent::getCode, Function.identity(),
                (existing, replacement) ->
                {
                    if(existing.getProperty().stream().anyMatch(p -> "DVal".equalsIgnoreCase(p.getCode()) && p.getValueIntegerType().getValue() == 3))
                        return existing;
                    return replacement;
                }
                )).values();


    }

    private void goDeep(CodeSystem.ConceptDefinitionComponent e, List<CodeSystem.ConceptDefinitionComponent> concept) {

        var wentDeep = false;
        for (CodeSystem.ConceptDefinitionComponent c : concept) {
            if (e.getCode().startsWith(c.getCode()))
            {
                goDeep(e, c.getConcept());
                wentDeep = true;
                return;
            }
        }
        if (!wentDeep)
            concept.add(e);
    }

}