package bisis.winisis2bisis;

import bisis.jongo_circ.JoLending;
import bisis.jongo_circ.JoMember;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberLendingsWrapper {

    private JoMember member;
    private List<JoLending> lendings;
}
