package bisis.winisis2bisis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WField {

    private String name;
    private String content;
    private List<WSubField> subfields = new ArrayList<>();
}
