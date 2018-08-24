package bisis.jongo_circ;

import bisis.circ.Warning;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class JoWarning implements Serializable {
    private Date warningDate;
    private String warningType;
    private String warnNo;
    private Date deadline;
    private String note;

    public JoWarning(Warning warning) {
        warningDate = warning.getWarningDate() != null ? Date.from(warning.getWarningDate()) : null;
        warningType = warning.getWarningType();
        warnNo = warning.getWarnNo();
        deadline = warning.getDeadline() != null ? Date.from(warning.getDeadline()) : null;
        note = warning.getNote();
    }

}