package bisis.circ;

import bisis.export.IsoInstantDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Created by dboberic on 28/07/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PictureBook implements Serializable {
    @JsonSerialize(using = IsoInstantDateSerializer.class)
    private Instant lendDate;
    private int lendNo;
    private int returnNo;
    private int status;
}
