package bisis.jongo_circ;

import bisis.circ.Lending;
import bisis.circ.Warning;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        warnings = l.getWarnings();
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
    private List<Warning> warnings = new ArrayList<>();

}