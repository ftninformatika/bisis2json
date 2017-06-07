package bisis.circ;

import bisis.export.IsoLocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Signing implements Serializable {

  private Integer id;

  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate signDate;

  private Integer location;

  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate untilDate;

  private BigDecimal cost;
  private String receiptId;
  private String librarian;
  
  public Signing() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public LocalDate getSignDate() {
    return signDate;
  }

  public void setSignDate(LocalDate signDate) {
    this.signDate = signDate;
  }

  public Integer getLocation() {
    return location;
  }

  public void setLocation(Integer location) {
    this.location = location;
  }

  public LocalDate getUntilDate() {
    return untilDate;
  }

  public void setUntilDate(LocalDate untilDate) {
    this.untilDate = untilDate;
  }

  public BigDecimal getCost() {
    return cost;
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  public String getReceiptId() {
    return receiptId;
  }

  public void setReceiptId(String receiptId) {
    this.receiptId = receiptId;
  }

  public String getLibrarian() {
    return librarian;
  }

  public void setLibrarian(String librarian) {
    this.librarian = librarian;
  }
  
  private static final long serialVersionUID = 1L;
}
