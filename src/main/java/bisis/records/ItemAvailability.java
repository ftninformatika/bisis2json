package bisis.records;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemAvailability implements java.io.Serializable{


    private Boolean borrowed;
    private String ctlgNo;
    private String recordID;
}
