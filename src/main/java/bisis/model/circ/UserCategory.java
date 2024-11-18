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

public class UserCategory extends Coder {
    private int titlesNo;
    private int period;
    private int maxPeriod;

    public UserCategory(String library, String description, int titlesNo, int period, int maxPeriod) {
        this.library = library;
        this.description = description;
        this.titlesNo = titlesNo;
        this.period = period;
        this.maxPeriod = maxPeriod;
    }
}
