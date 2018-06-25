package bisis.coders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jongo.marshall.jackson.oid.MongoId;

/**
 * Created by Petar on 1/15/2018.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Counter {

    @MongoId
    String _id;
    private String library;
    private String counterName;
    private int counterValue;
}
