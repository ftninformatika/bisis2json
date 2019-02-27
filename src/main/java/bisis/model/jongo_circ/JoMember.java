package bisis.model.jongo_circ;

import bisis.model.circ.MembershipType;
import bisis.model.circ.Organization;
import bisis.model.circ.UserCategory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@lombok.Getter
@lombok.Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JoMember {

    @MongoObjectId String _id;
    private String inUseBy;
    private Organization organization;
    private String language; //desc
    private String educationLevel; //desc
    private MembershipType membershipType;
    private UserCategory userCategory;
    private JoCorporateMember corporateMember;
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
    private Date birthday;

    private List<JoSigning> signings = new ArrayList<>();
    //private List<Lending> lendings = new ArrayList<>();
    private List<JoDuplicate> duplicates = new ArrayList<>();
    private List<JoPictureBook> picturebooks = new ArrayList<>();
}
