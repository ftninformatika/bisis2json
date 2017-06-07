package bisis.circ;

import bisis.export.IsoLocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.time.LocalDate;

public class Lending implements Serializable {

  private Integer id;
  private String ctlgNo;

  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate lendDate;

  private Integer location;

  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate returnDate;

  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate resumeDate;

  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate deadline;

  private String librarianLend;
  private String librarianReturn;
  private String librarianResume;
 
  public Lending() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCtlgNo() {
    return ctlgNo;
  }

  public void setCtlgNo(String ctlgNo) {
    this.ctlgNo = ctlgNo;
  }

  public LocalDate getLendDate() {
    return lendDate;
  }

  public void setLendDate(LocalDate lendDate) {
    this.lendDate = lendDate;
  }

  public Integer getLocation() {
    return location;
  }

  public void setLocation(Integer location) {
    this.location = location;
  }

  public LocalDate getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
  }

  public LocalDate getResumeDate() {
    return resumeDate;
  }

  public void setResumeDate(LocalDate resumeDate) {
    this.resumeDate = resumeDate;
  }

  public LocalDate getDeadline() {
    return deadline;
  }

  public void setDeadline(LocalDate deadline) {
    this.deadline = deadline;
  }

  public String getLibrarianLend() {
    return librarianLend;
  }

  public void setLibrarianLend(String librarianLend) {
    this.librarianLend = librarianLend;
  }

  public String getLibrarianReturn() {
    return librarianReturn;
  }

  public void setLibrarianReturn(String librarianReturn) {
    this.librarianReturn = librarianReturn;
  }

  public String getLibrarianResume() {
    return librarianResume;
  }

  public void setLibrarianResume(String librarianResume) {
    this.librarianResume = librarianResume;
  }
    
  private static final long serialVersionUID = 1L;
}
