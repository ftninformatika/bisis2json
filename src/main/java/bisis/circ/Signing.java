package bisis.circ;

import bisis.export.IsoLocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Signing implements Serializable {


  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate signDate;
  @JsonSerialize(using = IsoLocalDateSerializer.class)
  private LocalDate untilDate;
  private String librarian;
  private Double cost;
  private String receipt;
  private String location; //description
}

