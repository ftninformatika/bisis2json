package bisis.model.circ;

import bisis.apps.export.IsoInstantDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * Created by dboberic on 28/07/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Duplicate implements Serializable {
    @JsonSerialize(using = IsoInstantDateSerializer.class)
    private Instant dupDate;
    private int dupNo;
}
