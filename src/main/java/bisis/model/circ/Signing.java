package bisis.model.circ;

import bisis.apps.export.IsoInstantDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Signing implements Serializable {


  @JsonSerialize(using = IsoInstantDateSerializer.class)
  private Instant signDate;
  @JsonSerialize(using = IsoInstantDateSerializer.class)
  private Instant untilDate;
  private String librarian;
  private Double cost;
  private String receipt;
  private String location; //description
}

