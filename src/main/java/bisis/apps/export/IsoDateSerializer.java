package bisis.apps.export;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class IsoDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        String dateValue;
        if (date instanceof java.sql.Date)
            dateValue = ((java.sql.Date)date).toLocalDate().atStartOfDay(ZoneOffset.ofHours(1)).toInstant().atOffset(ZoneOffset.ofHours(1)).toLocalDateTime().format(formatter);
        else
            dateValue = date.toInstant().atOffset(ZoneOffset.ofHours(1)).toLocalDateTime().format(formatter);
        String text = "{ \"$date\" : \"" + dateValue + "Z\" }";
        jgen.writeRawValue(text);
    }

    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
}
