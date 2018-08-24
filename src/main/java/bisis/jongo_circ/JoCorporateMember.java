package bisis.jongo_circ;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoCorporateMember {
    private String library;
    private String userId;
    private String instName;
    private Date signDate;
    private String address;
    private String city;
    private Integer zip;
    private String phone;
    private String email;
    private String fax;
    private String secAddress;
    private Integer secZip;
    private String secCity;
    private String secPhone;
    private String contFirstName;
    private String contLastName;
    private String contEmail;

}
