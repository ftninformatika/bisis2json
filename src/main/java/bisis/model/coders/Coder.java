package bisis.model.coders;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Created by dboberic on 26/07/2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coder {
    public String library;
    public String coder_id;
    public String description;
}
