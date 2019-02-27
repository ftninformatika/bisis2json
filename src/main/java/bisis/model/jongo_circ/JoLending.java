package bisis.model.jongo_circ;

import bisis.model.circ.Lending;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoLending implements Serializable {

    public JoLending(Lending l) {
        userId = l.getUserId();
        ctlgNo = l.getCtlgNo();
        lendDate = l.getLendDate() != null ? Date.from(l.getLendDate()) : null;
        location = l.getLocation();
        returnDate = l.getReturnDate() != null ? Date.from(l.getReturnDate()) : null;
        resumeDate = l.getResumeDate() != null ? Date.from(l.getResumeDate()) : null;
        deadline = l.getDeadline() != null ? Date.from(l.getDeadline()) : null;
        librarianLend = l.getLibrarianLend();
        librarianReturn = l.getLibrarianReturn();
        librarianResume = l.getLibrarianResume();
        warnings = l.getWarnings().stream().map(w -> new JoWarning(w)).collect(Collectors.toList());
    }

    private String userId;
    private String ctlgNo;
    private Date lendDate;
    private String location; //desc
    private Date returnDate;
    private Date resumeDate;
    private Date deadline;
    private String librarianLend;
    private String librarianReturn;
    private String librarianResume;
    private List<JoWarning> warnings = new ArrayList<>();

}