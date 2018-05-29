package bisis.circ;

import bisis.export.IsoInstantDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * Created by dboberic on 28/07/2017.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Warning implements Serializable {
    @JsonSerialize(using = IsoInstantDateSerializer.class)
    private Instant warningDate;
    private String warningType;
    private String warnNo;
    @JsonSerialize(using = IsoInstantDateSerializer.class)
    private Instant deadline;
    private String note;

}
