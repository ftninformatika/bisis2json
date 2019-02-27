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
public class CircLocation {

    private String library;
    private String locationCode;
    private String description;
    private int lastUserId;
}
