package bisis.circ;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by dboberic on 27/07/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    private String _id;
    private String library;
    private String name;
    private String address;
    private String city;
    private String zip;
}
