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
@AllArgsConstructor
@NoArgsConstructor
public class MembershipType extends Coder {
    private int period;

    public MembershipType(String library, String description, int period) {
        this.library = library;
        this.description = description;
        this.period = period;
    }
}
