package bisis.model.jongo_circ;


import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoPictureBook {

    private Date lendDate;
    private int lendNo;
    private int returnNo;
    private int status;
}
