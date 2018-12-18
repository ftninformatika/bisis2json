package bisis.jongo_records;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JoDupRec {

    private int _id;
    private List<ObjectId> dups;
    private String count;
}
