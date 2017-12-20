package bisis.circ;

import lombok.*;

/**
 * Created by dboberic on 27/07/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WarningCounter {

    private String library;
    private String warningType; //WarningType.description
    private String warnYear;
    private Integer lastNo;

}
