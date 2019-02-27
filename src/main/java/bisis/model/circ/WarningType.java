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
public class WarningType {
    private String library;
    private String description;
    private String template;
}
