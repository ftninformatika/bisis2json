package bisis.model.jongo_circ;

import lombok.*;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoSigning {

    private Date signDate;
    private Date untilDate;
    private String librarian;
    private Double cost;
    private String receipt;
    private String location; //description
}
