package bisis.winisis2bisis;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WinIsisMemberTxt {

    Map<String, String> subfields = new HashMap<>();

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{\n");
        for (Map.Entry<String, String> entry: subfields.entrySet()) {
            stringBuffer.append(entry.getKey() + ":" + entry.getValue() + "\n");
        }

        stringBuffer.append("}");
        return stringBuffer.toString();
    }
}
