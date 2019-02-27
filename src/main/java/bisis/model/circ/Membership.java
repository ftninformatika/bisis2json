package bisis.model.circ;

import lombok.*;

/**
 * Created by dboberic on 27/07/2017.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Membership {
    private String library;
    private String memberType; // description
    private String userCateg; //desc
    private Double cost;
}
