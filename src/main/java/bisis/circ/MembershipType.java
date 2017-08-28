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
@AllArgsConstructor
@NoArgsConstructor
public class MembershipType {
    private String library;
    private String description;
    private int period;
}
