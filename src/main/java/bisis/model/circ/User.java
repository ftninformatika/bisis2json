package bisis.model.circ;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

  private Integer sysId;
  private Organization organization;
  private String languages;
  private String educationLevel;
  private MembershipType membershipType;
  private UserCategory userCategory;
  private CorporateMember corporateMember;
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
  private List<Duplicate> duplicates = new ArrayList<>();
  private List<PictureBook> picturebooks = new ArrayList<>();
  

  
  private static final long serialVersionUID = 1L;
}
