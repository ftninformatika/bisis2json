package bisis.apps.cobiss2bisis;

import bisis.model.records.Field;
import bisis.model.records.Record;
import bisis.model.records.Subfield;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

/**
 * @author badf00d21  17.9.19.
 */
class Cobiss2BisisRecordGenerator {

    Record generateRecord(JSONObject cobissJson) {
        Record record = new Record();


        for (String propName: cobissJson.keySet()) {
            JSONObject nestedObj = cobissJson.getJSONObject(propName);
            String parsingText = nestedObj.getString("value");
            switch (propName) {
                case "publisherCard": fillPublisherInfo(record, parsingText); break;
            }
        }

        return record;
    }

    private void fillPublisherInfo(Record r, String parsingText) {
        String[] parts = parsingText.split(";");
        for (int i = 0; i < parts.length; i++) {
            String betweenParenthesis = StringUtils.substringBetween(parts[i], "(", ")");
            parts[i] = parts[i].replace("(" + betweenParenthesis+ ")", "");
            String[] chunks = parts[i].split(":|;|,");
            Field _200 = i == 0 ? new Field("200") : new Field("210");
            if (chunks.length > 0) _200.add(new Subfield('a', chunks[0].trim()));
            if (chunks.length > 1 && !chunks[1].trim().matches(".*\\d.*")) _200.add(new Subfield('c', chunks[1].trim()));
            else if (chunks.length > 1 && chunks[1].trim().matches(".*\\d.*")) _200.add(new Subfield('d', chunks[1].trim()));

            if (chunks.length > 2 && !chunks[2].trim().matches(".*\\d.*")) _200.add(new Subfield('c', chunks[2].trim()));
            else if (chunks.length > 2 && chunks[2].trim().matches(".*\\d.*")) _200.add(new Subfield('d', chunks[2].trim()));

            if (betweenParenthesis != null && !betweenParenthesis.equals("")) {
                String[] eg = betweenParenthesis.split(":");
                if (eg.length > 1) _200.add(new Subfield('e', eg[1].trim()));
                if (eg.length > 0) _200.add(new Subfield('g', eg[0].trim()));
            }
            r.add(_200);
        }

    }


}
