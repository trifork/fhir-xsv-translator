package com.trifork.ehealth.npu;

import com.google.common.base.Strings;
import com.opencsv.bean.CsvBindByPosition;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent;

public class NPUListEntry {

  @CsvBindByPosition(position = 1)
  private String listChangeDate;

  @CsvBindByPosition(position = 2)
  private String listChangeComment;

  @CsvBindByPosition(position = 3)
  private String listCreatedDate;

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
  private String listCodeValue;

  @CsvBindByPosition(position = 15)
  private String listItemPositionNo;

  @CsvBindByPosition(position = 16)
  private String contextDependant;

  @CsvBindByPosition(position = 17)
  private String scaleType;

  @CsvBindByPosition(position = 18)
  private String rule;

  @CsvBindByPosition(position = 19)
  private String active;

  public String getListCodeValue() {
    return listCodeValue;
  }

  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public ConceptReferenceComponent asConceptSetComponent() {
    var conceptReferenceComponent = new ConceptReferenceComponent();
    conceptReferenceComponent.setCode(npuCode);
    return conceptReferenceComponent;
  }

  public Date getListChangeDate() {
    return Strings.isNullOrEmpty(listChangeDate) ? null
        : Date.valueOf(LocalDate.parse(listChangeDate, formatter));
  }
}