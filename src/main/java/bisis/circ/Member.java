package bisis.circ;

import bisis.export.IsoInstantDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member implements Serializable {

  private String inUseBy;
  private Organization organization;
  private String language; //desc
  private String educationLevel; //desc
  private MembershipType membershipType;
  private UserCategory userCategory;
  private CorporateMember corporateMember;
  private String userId;
  private String firstName;
  private String lastName;
  private String parentName;
  private String address;
  private String city;
  private String zip;
  private String phone;
  private String email; // ovo je username za prijavljivanje
  private String password;
  private String jmbg;
  private Integer docId;
  private String docNo;
  private String docCity;
  private String country;
  private String gender;
  private String age;
  private String secAddress;
  private String secZip;
  private String secCity;
  private String secPhone;
  private String note;
  private String interests;
  private Integer warningInd;
  private String occupation;
  private String title;
  private String indexNo;
  private Integer classNo;
  private String blockReason;
  private String pin;
  private String oldNumbers;
  @JsonSerialize(using = IsoInstantDateSerializer.class)
  private Instant birthday;

  private List<Signing> signings = new ArrayList<>();
  //private List<Lending> lendings = new ArrayList<>();
  private List<Duplicate> duplicates = new ArrayList<>();
  private List<PictureBook> picturebooks = new ArrayList<>();
  

  
  private static final long serialVersionUID = 1L;
}
