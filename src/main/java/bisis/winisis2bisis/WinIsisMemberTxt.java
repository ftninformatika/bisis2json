package bisis.winisis2bisis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WinIsisMemberTxt {

    List<WField> subfields = new ArrayList<>();

    public String get(String sfName) {
        String retVal = null;

        if (sfName.length() == 3) {
            subfields.stream().filter(f -> f.getName().equals(sfName)).findFirst();
        }
        else if (sfName.length() == 4) {

        }

        return retVal;
    }


//    @Override
//    public String toString() {
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("{\n");
//        for (Map.Entry<String, String> entry: subfields.entrySet()) {
//            stringBuffer.append(entry.getKey() + ":" + entry.getValue() + "\n");
//        }
//
//        stringBuffer.append("}");
//        return stringBuffer.toString();
//    }

}
