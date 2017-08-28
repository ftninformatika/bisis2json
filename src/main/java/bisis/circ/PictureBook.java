package bisis.circ;

import bisis.export.IsoLocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;
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
    @JsonSerialize(using = IsoLocalDateSerializer.class)
    private LocalDate lendDate;
    private int lendNo;
    private int returnNo;
    private int status;
}
