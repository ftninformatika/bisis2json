package bisis.circ;

import bisis.export.IsoLocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lending implements Serializable {

  private Integer id;
  private String ctlgNo;

  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate lendDate;

  private Integer location;

  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate returnDate;

  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate resumeDate;

  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate deadline;

  private String librarianLend;
  private String librarianReturn;
  private String librarianResume;
 

    
  private static final long serialVersionUID = 1L;
}
