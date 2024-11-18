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
public class Membership extends Coder {
    private String memberType; // description
    private String userCateg; //desc
    private Double cost;
}
