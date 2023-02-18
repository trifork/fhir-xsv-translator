package com.trifork.ehealth.sks;

import com.google.common.base.Strings;
import com.joutvhu.fixedwidth.parser.annotation.FixedField;
import com.joutvhu.fixedwidth.parser.annotation.FixedObject;
import com.joutvhu.fixedwidth.parser.domain.KeepPadding;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IntegerType;

//See https://sundhedsdatastyrelsen.dk/-/media/sds/filer/rammer-og-retningslinjer/klassisfikationer/sks-download/sks-tabelstruktur.pdf?la=da

@FixedObject
public class SKSEntry {

  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");


  @FixedField(label = "RecArt", start = 0, length = 3)
  private String recArt;

  @FixedField(label = "SKSkode", start = 3, length = 20, keepPadding = KeepPadding.DROP)
  private String sksCode;

  @FixedField(label = "DatoFra", start = 23, length = 8)
  private String dateFrom;

  @FixedField(label = "DatoÆndring", start = 31, length = 8)
  private String dateChanged;

  @FixedField(label = "DatoTil", start = 39, length = 8)
  private String dateTo;

  @FixedField(label = "Kodetekst", start = 47, length = 120, keepPadding = KeepPadding.DROP)
  private String text;

  @FixedField(label = "DVal", start = 187, length = 1)
  private Integer valid;

  @Override
  public String toString() {
    return "SKSEntry{" +
            "formatter=" + formatter +
            ", recArt='" + recArt + '\'' +
            ", sksCode='" + sksCode + '\'' +
            ", dateFrom='" + dateFrom + '\'' +
            ", dateChanged='" + dateChanged + '\'' +
            ", dateTo='" + dateTo + '\'' +
            ", text='" + text + '\'' +
            ", valid=" + valid +
            '}';
  }

  public ConceptDefinitionComponent asConceptDefinitionComponent() {
    ConceptDefinitionComponent conceptDefinitionComponent = new ConceptDefinitionComponent()
        .setCode(sksCode)
        .setDefinition(text);

    if (!Strings.isNullOrEmpty(dateChanged)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("DatoÆndring")
              .setValue(new DateTimeType().setValue(
                  Date.valueOf(LocalDate.parse(dateChanged, formatter)))));
    }

    if (!Strings.isNullOrEmpty(dateFrom)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("DatoFra")
              .setValue(new DateTimeType().setValue(
                  Date.valueOf(LocalDate.parse(dateFrom, formatter)))));
    }

    if (!Strings.isNullOrEmpty(dateTo)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("DatoTil")
              .setValue(new DateTimeType().setValue(
                  Date.valueOf(LocalDate.parse(dateTo, formatter)))));
    }

    if (valid != null) {
      conceptDefinitionComponent
              .addProperty(new ConceptPropertyComponent().setCode("DVal")
                      .setValue(new IntegerType(valid)));
    }
    return conceptDefinitionComponent;
  }
}
