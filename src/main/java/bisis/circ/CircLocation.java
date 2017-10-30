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
public class CircLocation {
    private String _id;
    private String library;
    private String locationCode;
    private String description;
    private int lastUserId;
}
