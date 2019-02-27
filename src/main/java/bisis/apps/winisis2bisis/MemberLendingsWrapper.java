package bisis.apps.winisis2bisis;

import bisis.model.jongo_circ.JoLending;
import bisis.model.jongo_circ.JoMember;
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
