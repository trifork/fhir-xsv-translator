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

public class NPUDefinitionEntry {

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

  @CsvBindByPosition(position = 11)
  private String kindOfProperty;

  @CsvBindByPosition(position = 12)
  private String proc;

  @CsvBindByPosition(position = 13)
  private String unit;

  @CsvBindByPosition(position = 14)
  private String speciality;

  @CsvBindByPosition(position = 15)
  private String subSpeciality;

  @CsvBindByPosition(position = 16)
  private String type;

  @CsvBindByPosition(position = 17)
  private String codeValue;

  @CsvBindByPosition(position = 18)
  private String contextDependant;

  @CsvBindByPosition(position = 19)
  private String scaleType;

  @CsvBindByPosition(position = 20)
  private String rule;

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

    addStringProperty(conceptDefinitionComponent, changeComment, "ChangeComment");

    if (!Strings.isNullOrEmpty(createdDate)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("CreatedDate")
              .setValue(new DateTimeType().setValue(
                  Date.valueOf(LocalDate.parse(createdDate, formatter)))));
    }

    addStringProperty(conceptDefinitionComponent, system, "System");

    addStringProperty(conceptDefinitionComponent, sysSpec, "SysSpec");

    addStringProperty(conceptDefinitionComponent, prefix, "Prefix");

    addStringProperty(conceptDefinitionComponent, component, "Component");

    addStringProperty(conceptDefinitionComponent, compSpec, "CompSpec");

    addStringProperty(conceptDefinitionComponent, kindOfProperty, "Kind-of-property");

    addStringProperty(conceptDefinitionComponent, proc, "Proc");

    addStringProperty(conceptDefinitionComponent, unit, "Unit");

    addStringProperty(conceptDefinitionComponent, speciality, "Speciality");

    addStringProperty(conceptDefinitionComponent, subSpeciality, "Sub-speciality");

    addStringProperty(conceptDefinitionComponent, type, "Type");

    addStringProperty(conceptDefinitionComponent, codeValue, "Code value");

    addStringProperty(conceptDefinitionComponent, contextDependant, "Context dependant");

    addStringProperty(conceptDefinitionComponent, scaleType, "Scale type");

    addStringProperty(conceptDefinitionComponent, rule, "Rule");

    if (!Strings.isNullOrEmpty(active)) {
      conceptDefinitionComponent
          .addProperty(new ConceptPropertyComponent().setCode("Active")
              .setValue(new BooleanType().setValue(!active.equalsIgnoreCase("0"))));
    }

    return conceptDefinitionComponent;
  }

  private void addStringProperty(ConceptDefinitionComponent conceptDefinitionComponent,
      String field,
      String fieldName) {
    if (Strings.isNullOrEmpty(field)) {
      return;
    }

    conceptDefinitionComponent
        .addProperty(new ConceptPropertyComponent().setCode(fieldName)
            .setValue(new StringType().setValue(field)));

  }

}



