package bisis.model.coders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Petar on 1/15/2018.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Counter {

    private String library;
    private String counterName;
    private int counterValue;
}
