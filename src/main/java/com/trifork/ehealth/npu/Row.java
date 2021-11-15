package com.trifork.ehealth.npu;

import com.google.common.base.Strings;
import com.opencsv.bean.CsvBindByPosition;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.StringType;

public class Row {

  @CsvBindByPosition(position = 1)
  private String changeDate;

  @CsvBindByPosition(position = 2)
  private String changeComment;

  @CsvBindByPosition(position = 3)
  private String createdDate;

  @CsvBindByPosition(position = 4)
  private String npuCode;

  @CsvBindByPosition(position = 5)
  private String shortDefinition;

  @CsvBindByPosition(position = 6)
  private String system;

  @CsvBindByPosition(position = 7)
  private String sysSpec;

  @CsvBindByPosition(position = 8)
  private String prefix;

  @CsvBindByPosition(position = 9)
  private String component;

  @CsvBindByPosition(position = 10)
  private String compSpec;

  @CsvBindByPosition(position = 21)
  private String active;

  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public ConceptDefinitionComponent asConceptDefinitionComponent() {
    ConceptDefinitionComponent conceptDefinitionComponent = new ConceptDefinitionComponent()
        .setCode(npuCode)
        .setDisplay(shortDefinition);

    if (!Strings.isNullOrEmpty(changeDate)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("ChangeDate")
              .setValue(new DateTimeType().setValue(
                  Date.valueOf(LocalDate.parse(changeDate, formatter)))));
    }

    if (!Strings.isNullOrEmpty(changeComment)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("ChangeComment")
              .setValue(new StringType().setValue(changeComment)));
    }

    if (!Strings.isNullOrEmpty(createdDate)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("CreatedDate")
              .setValue(new DateTimeType().setValue(
                  Date.valueOf(LocalDate.parse(createdDate, formatter)))));
    }

    if (!Strings.isNullOrEmpty(system)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("System")
              .setValue(new StringType().setValue(system)));
    }

    if (!Strings.isNullOrEmpty(sysSpec)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("SysSpec")
              .setValue(new StringType().setValue(sysSpec)));
    }

    if (!Strings.isNullOrEmpty(prefix)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("Prefix")
              .setValue(new StringType().setValue(prefix)));
    }

    if (!Strings.isNullOrEmpty(component)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("Component")
              .setValue(new StringType().setValue(component)));
    }

    if (!Strings.isNullOrEmpty(compSpec)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("CompSpec")
              .setValue(new StringType().setValue(compSpec)));
    }

    if (!Strings.isNullOrEmpty(active)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("Active")
              .setValue(new BooleanType().setValue(!active.equalsIgnoreCase("0"))));
    }

    return conceptDefinitionComponent;
  }

}
