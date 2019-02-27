package bisis.model.circ;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Petar on 11/27/2017.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CircConfig {

    private String library;
    private String description;

    private String circOptionsXML;
    private String validatorOptionsXML;
}
