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
                case "author700701": fillAuthorInfo(record, parsingText); break;
                case "titleCard": fillTitleMainInfo(record, parsingText); break;
            }
        }

        return record;
    }

    private void fillTitleMainInfo(Record r, String parsingText) {
        String[] parts = parsingText.split("/|,|:|;|\\.|=|;");
        if (r.getField("200") == null) r.add(new Field("200"));
        // ovo je slucaj kada je vec podesen indikator na polju 200 zbog autora
        Field _200 = r.getField("200");
        char lastLDelimiter = ' ';
        char lastRDelimiter = ' ';
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) _200.add(new Subfield('a', parts[0]));
            else {
                int leftDelimiterIndex = parsingText.indexOf(parts[i]) - 1;
                int rightDelimiterIndex = parsingText.indexOf(parts[i]) + parts[i].length();
                char leftDelimiter = ' ';
                try {
                    leftDelimiter = parsingText.charAt(leftDelimiterIndex);
                } catch (Exception e) {}
                char rightDelimiter = ' ';
                try {
                    rightDelimiter = parsingText.charAt(rightDelimiterIndex);
                } catch (Exception e) {}
//TODO: fix this...
                if (leftDelimiter == ':') _200.add(new Subfield('e', parts[i].trim()));
                else if (leftDelimiter == ';' && rightDelimiter == '/') _200.add(new Subfield('a', parts[i].trim()));
                else if (leftDelimiter == '.' && rightDelimiter == ',') _200.add(new Subfield('h', parts[i].trim()));
                else if (leftDelimiter == ',') _200.add(new Subfield('i', parts[i]));
                else if (leftDelimiter == '.' && rightDelimiter != ',') _200.add(new Subfield('i', parts[i].trim()));
                else if (leftDelimiter == '=') _200.add(new Subfield('d', parts[i].trim()));
                else if (leftDelimiter == '/') _200.add(new Subfield('f', parts[i].trim()));
                else if (lastLDelimiter == '/' && leftDelimiter != ';' && !parts[i].trim().equals("")) _200.add(new Subfield('g', parts[i].trim()));
//                System.out.println(leftDelimiter);
//                System.out.println(rightDelimiter);
                lastLDelimiter = leftDelimiter;
                lastRDelimiter = rightDelimiter;
            }
        }
    }

    private void fillAuthorInfo(Record r, String parsingText) {
        String[] parts = parsingText.split("<br>|<br/>");
        for (int i = 0; i < parts.length; i++) {
            String[] trnanslatedParts = parts[i].split("=");
            for (int j = 0; j < trnanslatedParts.length; j++) {
                Field field = new Field();
                if (parts.length < 4 && j == 0) field.setName("700");
                // Specijalan slucaj kada treba podesiti indikator u 200
                else if (parts.length >= 4 && j == 0) {
                    field.setName("701");
                    if (r.getField("200") != null) r.getField("200").setInd1('1');
                    else r.add(new Field("200",'1', ' '));
                }
                else field.setName("900");
                String[] abf = trnanslatedParts[j].split(",");
                if (abf.length > 0) field.add(new Subfield('a', abf[0]));

                if (abf.length > 1 && !abf[1].replace("-","").trim().matches(".*\\d.*")) field.add(new Subfield('b',abf[1]));
                if (abf.length > 1 && abf[1].replace("-","").trim().matches(".*\\d.*")) field.add(new Subfield('f',abf[1]));

                if (abf.length > 2 && !abf[2].replace("-","").trim().matches(".*\\d.*")) field.add(new Subfield('b',abf[2]));
                if (abf.length > 2 && abf[2].replace("-","").trim().matches(".*\\d.*")) field.add(new Subfield('f',abf[2]));

                r.add(field);
            }
        }
    }

    private void fillPublisherInfo(Record r, String parsingText) {
        String[] parts = parsingText.split(";");
        for (int i = 0; i < parts.length; i++) {
            String betweenParenthesis = StringUtils.substringBetween(parts[i], "(", ")");
            parts[i] = parts[i].replace("(" + betweenParenthesis+ ")", "");
            String[] chunks = parts[i].split(":|;|,");
            Field _210 = new Field("210");
            if (chunks.length > 0) _210.add(new Subfield('a', chunks[0].trim()));
            if (chunks.length > 1 && !chunks[1].trim().matches(".*\\d.*")) _210.add(new Subfield('c', chunks[1].trim()));
            else if (chunks.length > 1 && chunks[1].trim().matches(".*\\d.*")) _210.add(new Subfield('d', chunks[1].trim()));

            if (chunks.length > 2 && !chunks[2].trim().matches(".*\\d.*")) _210.add(new Subfield('c', chunks[2].trim()));
            else if (chunks.length > 2 && chunks[2].trim().matches(".*\\d.*")) _210.add(new Subfield('d', chunks[2].trim()));

            if (betweenParenthesis != null && !betweenParenthesis.equals("")) {
                String[] eg = betweenParenthesis.split(":");
                if (eg.length > 1) _210.add(new Subfield('e', eg[1].trim()));
                if (eg.length > 0) _210.add(new Subfield('g', eg[0].trim()));
            }
            r.add(_210);
        }

    }


}
