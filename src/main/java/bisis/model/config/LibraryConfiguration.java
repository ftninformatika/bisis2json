package bisis.model.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Petar on 6/30/2017.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LibraryConfiguration {

    private String _id;
    private String libraryName;
    private String libraryFullName;


    private String pincodeEnabled;
    private String pincodeLibrary;

    private String locale;


    public String toString(){
        return libraryFullName;
    }
}
