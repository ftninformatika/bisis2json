package bisis.circ;

import bisis.export.IsoInstantDateSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lending implements Serializable {

  private String userId;
  private String ctlgNo;

  //@JsonSerialize(using = IsoLocalDateSerializer.class)
  @JsonSerialize(using = IsoInstantDateSerializer.class)
  private Instant lendDate;

  private String location; //desc

  @JsonSerialize(using = IsoInstantDateSerializer.class)
  private Instant returnDate;

  @JsonSerialize(using = IsoInstantDateSerializer.class)
  private Instant resumeDate;

  @JsonSerialize(using = IsoInstantDateSerializer.class)
  private Instant deadline;

  private String librarianLend;
  private String librarianReturn;
  private String librarianResume;


  private List<Warning> warnings = new ArrayList<>();
 

    
  private static final long serialVersionUID = 1L;
}
