package bisis.model.circ;

import bisis.model.coders.Coder;
import lombok.*;

/**
 * Created by dboberic on 27/07/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Place extends Coder {
    private String city;
    private String zip;
}
