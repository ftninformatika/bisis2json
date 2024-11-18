package bisis.model.circ;


import bisis.model.coders.Coder;
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
public class Organization extends Coder {

    private String _id;
    private String address;
    private String city;
    private String zip;
}
