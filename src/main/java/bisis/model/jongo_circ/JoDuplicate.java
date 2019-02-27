package bisis.model.jongo_circ;

import lombok.*;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoDuplicate {

    private Date dupDate;
    private int dupNo;
}
