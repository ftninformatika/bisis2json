package bisis.circ;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
  
  private Integer sysId;
  private Integer organizationId;
  private Integer languages;
  private Integer educationLevel;
  private Integer membershipType;
  private Integer userCategory;
  private Integer groups;
  private String userId;
  private String firstName;
  private String lastName;
  private String parentName;
  private String address;
  private String city;
  private Integer zip;
  private String phone;
  private String email;
  private String jmbg;
  private Integer docId;
  private String docNo;
  private String docCity;
  private String country;
  private String gender;
  private String age;
  private String secAddress;
  private Integer secZip;
  private String secCity;
  private String secPhone;
  private String note;
  private String interests;
  private Integer warningInd;
  private String occupation;
  private String title;
  private String indexNo;
  private Integer classNo;
  private String pass;
  private String blockReason;
  
  private List<Lending> lending = new ArrayList<>();
  private List<Signing> signing = new ArrayList<>();
  
  public User() {
  }

  public Integer getSysId() {
    return sysId;
  }

  public void setSysId(Integer sysId) {
    this.sysId = sysId;
  }

  public Integer getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(Integer organizationId) {
    this.organizationId = organizationId;
  }

  public Integer getLanguages() {
    return languages;
  }

  public void setLanguages(Integer languages) {
    this.languages = languages;
  }

  public Integer getEducationLevel() {
    return educationLevel;
  }

  public void setEducationLevel(Integer educationLevel) {
    this.educationLevel = educationLevel;
  }

  public Integer getMembershipType() {
    return membershipType;
  }

  public void setMembershipType(Integer membershipType) {
    this.membershipType = membershipType;
  }

  public Integer getUserCategory() {
    return userCategory;
  }

  public void setUserCategory(Integer userCategory) {
    this.userCategory = userCategory;
  }

  public Integer getGroups() {
    return groups;
  }

  public void setGroups(Integer groups) {
    this.groups = groups;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Integer getZip() {
    return zip;
  }

  public void setZip(Integer zip) {
    this.zip = zip;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getJmbg() {
    return jmbg;
  }

  public void setJmbg(String jmbg) {
    this.jmbg = jmbg;
  }

  public Integer getDocId() {
    return docId;
  }

  public void setDocId(Integer docId) {
    this.docId = docId;
  }

  public String getDocNo() {
    return docNo;
  }

  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public String getSecAddress() {
    return secAddress;
  }

  public void setSecAddress(String secAddress) {
    this.secAddress = secAddress;
  }

  public Integer getSecZip() {
    return secZip;
  }

  public void setSecZip(Integer secZip) {
    this.secZip = secZip;
  }

  public String getSecCity() {
    return secCity;
  }

  public void setSecCity(String secCity) {
    this.secCity = secCity;
  }

  public String getSecPhone() {
    return secPhone;
  }

  public void setSecPhone(String secPhone) {
    this.secPhone = secPhone;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getInterests() {
    return interests;
  }

  public void setInterests(String interests) {
    this.interests = interests;
  }

  public Integer getWarningInd() {
    return warningInd;
  }

  public void setWarningInd(Integer warningInd) {
    this.warningInd = warningInd;
  }

  public String getOccupation() {
    return occupation;
  }

  public void setOccupation(String occupation) {
    this.occupation = occupation;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getIndexNo() {
    return indexNo;
  }

  public void setIndexNo(String indexNo) {
    this.indexNo = indexNo;
  }

  public Integer getClassNo() {
    return classNo;
  }

  public void setClassNo(Integer classNo) {
    this.classNo = classNo;
  }

  public String getPass() {
    return pass;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }

  public String getBlockReason() {
    return blockReason;
  }

  public void setBlockReason(String blockReason) {
    this.blockReason = blockReason;
  }

  public List<Lending> getLending() {
    return lending;
  }

  public void setLending(List<Lending> lending) {
    this.lending = lending;
  }

  public List<Signing> getSigning() {
    return signing;
  }

  public void setSigning(List<Signing> signing) {
    this.signing = signing;
  }

  public String getDocCity() {
    return docCity;
  }

  public void setDocCity(String docCity) {
    this.docCity = docCity;
  }
  
  private static final long serialVersionUID = 1L;
}
