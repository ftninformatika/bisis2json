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
public class WarningCounter extends Coder {

    private String warningType; //WarningType.description
    private String warnYear;
    private Integer lastNo;

}
