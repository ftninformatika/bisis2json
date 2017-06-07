package bisis.export;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IsoLocalDateSerializer extends JsonSerializer<LocalDate> {

  @Override
  public void serialize(LocalDate date, JsonGenerator jgen, SerializerProvider provider) throws IOException {
    String dateValue = date.atStartOfDay().format(formatter);
    String text = "{ \"$date\" : \"" + dateValue + "+01:00\" }";
    jgen.writeRawValue(text);
  }

  DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
}
