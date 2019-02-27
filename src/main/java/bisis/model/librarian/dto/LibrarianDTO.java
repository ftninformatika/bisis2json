package bisis.model.librarian.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Created by Petar on 8/10/2017.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Document(collection = "librarians")
public class LibrarianDTO {

    private String username;
    private String password;
    private ArrayList<String> authorities = new ArrayList<>();
    private String ime;
    private String prezime;
    private String email;
    private String napomena;
    private boolean obrada;
    private boolean cirkulacija;
    private boolean administracija;
    private LibrarianContextDTO context = new LibrarianContextDTO();
    private String biblioteka;
    private ProcessTypeDTO curentProcessType;

}
