package bisis.apps.cobiss2bisis;

import bisis.model.records.Author;
import bisis.model.records.Field;
import bisis.model.records.Record;
import bisis.model.records.Subfield;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author badf00d21  17.9.19.
 */
class Cobiss2BisisRecordGenerator {

    Record generateRecord(JSONObject cobissJson) {
        Record record = new Record();

        for (String propName: cobissJson.keySet()) {
            JSONObject nestedObj = cobissJson.getJSONObject(propName);
            String parsingText = nestedObj.getString("value");
            record.setPubType(0);
            if (parsingText.equals("26454535"))
                System.out.println("stop");
            if (parsingText == null || parsingText.trim().equals("")) continue;
            switch (propName) {
                case "publisherCard": fillPublisherInfo(record, parsingText); break;
                case "author700701": fillAuthorInfo(record, parsingText, false); break;
                case "titleCard": fillTitleMainInfo(record, parsingText); break;
                case "uniformTitle": fillUniformTitle(record, parsingText); break;
                case "parallelTitle": fillParallelTitle(record, parsingText); break;
                case "otherTitles": fillOtherTitles(record, parsingText); break;
                case "languageCard": fillLanguage(record, parsingText); break;
                case "articlePubDate": fillArticlePubDate(record, parsingText); break;
                case "edition": fillEdition(record, parsingText); break;
                case "ph_descriptionCard": fillPhDescription(record, parsingText); break;
                case "otherAuthor702": fillAuthorInfo(record, parsingText, true); break;
                case "seriesCard": fillSeriesCard(record, parsingText); break;
                case "content": fillContent(record, parsingText); break;
                case "isbnCard": fillIsbn(record, parsingText); break;
                case "notesCard": fillNotes(record, parsingText); break;
                case "subjectCard": fillSubject(record, parsingText); break;
                case "udkCard": fillUDK(record, parsingText); break;
                case "supplement": fillSupplement(record, parsingText); break;
                case "frequency": fillFrequency(record, parsingText); break;
                case "bound": fillBound(record, parsingText); break;
                case "measure": fillMeasure(record, parsingText); break;
                case "erCharacteristic": fillerCharacteristic(record, parsingText); break;
                case "subjectCardUncon": fillsubjectCardUncon(record, parsingText); break;
                case "continues": fillContinues(record, parsingText); break;
                case "issnL": fillissnL(record, parsingText); break;
                case "urlCard": fillUrlCard(record, parsingText); break;
                case "Award": fillAward(record, parsingText); break;
                case "captionTitle": fillcaptionTitle(record, parsingText); break;
                case "otherAuthors712": fillotherAuthors712(record, parsingText); break;
                case "Doi": fillDoi(record, parsingText); break;
                case "summaryCard": fillSummaryCard(record, parsingText); break;
                case "linkPubl": fillLinkPubl(record, parsingText); break;
                case "ismn": fillIsmn(record, parsingText); break;
                case "author710and711": fillAuthor710and711(record, parsingText); break;
                case "cobissid": record.setCreator(new Author(parsingText, "")); break;
            }
        }
        return record;
    }

    private Field getOrCreateField(String fName, Record record) {
        if (fName == null || fName.length() != 3) {
            return null;
        }
        Field f = record.getField(fName);
        if (f == null) {
            f = new Field(fName);
            record.add(f);
        }
        return f;
    }

    private void fillAuthor710and711(Record r, String parsingText) {
        Field _710 = getOrCreateField("710", r);
        _710.add(new Subfield('a',  parsingText));
    }

    private void fillIsmn(Record r, String parsingText) {
        Field _071 = getOrCreateField("071", r);
        _071.add(new Subfield('a',  parsingText));
    }

    private void fillLinkPubl(Record r, String parsingText) {
        Field _300 = getOrCreateField("300", r);
        if (parsingText.contains(">") && parsingText.contains(">")) {
            parsingText = "U:" + parsingText.substring(parsingText.indexOf('>') + 1, parsingText.lastIndexOf('<') - 1);
        }
        _300.add(new Subfield('a',  parsingText));
    }

    private void fillSummaryCard(Record r, String parsingText) {
        Field _330 = getOrCreateField("330", r);
        _330.add(new Subfield('a',  parsingText));
    }

    private void fillDoi(Record r, String parsingText) {
        Field _041 = getOrCreateField("041", r);
        _041.add(new Subfield('a',  parsingText));
    }

    private void fillotherAuthors712(Record r, String parsingText) {
        Field _712 = getOrCreateField("712", r);
        _712.add(new Subfield('a',  parsingText));
    }

    private void fillcaptionTitle(Record r, String parsingText) {
        Field _514 = getOrCreateField("514", r);
        _514.add(new Subfield('a',  parsingText));
    }

    private void fillAward(Record r, String parsingText) {
        Field _300 = getOrCreateField("300", r);
        _300.add(new Subfield('a',  parsingText));
    }


    private void fillUrlCard(Record r, String parsingText) {
        Field _856 = getOrCreateField("856", r);
        _856.add(new Subfield('u',  parsingText));
    }

    private void fillissnL(Record r, String parsingText) {
        Field _300 = getOrCreateField("300", r);
        _300.add(new Subfield('a',  parsingText));
    }

    private void fillContinues(Record r, String parsingText) {
        Field _300 = getOrCreateField("300", r);
        _300.add(new Subfield('a',  "Je nastavak: " + parsingText));
    }

    private void fillsubjectCardUncon(Record r, String parsingText) {
        Field _610 = getOrCreateField("610", r);
        _610.add(new Subfield('a',  parsingText));
    }

    private void fillMeasure(Record r, String parsingText) {
        Field _206 = getOrCreateField("206", r);
        _206.add(new Subfield('a',  parsingText));
    }

    private void fillBound(Record r, String parsingText) {
        Field _371 = getOrCreateField("371", r);
        _371.add(new Subfield('a', "Privezano: " + parsingText));
    }

    private void fillerCharacteristic(Record r, String parsingText) {
        Field _300 = getOrCreateField("300", r);
        _300.add(new Subfield('a', parsingText));
    }

    private void fillFrequency(Record r, String parsingText) {
        Field _300 = getOrCreateField("300", r);
        _300.add(new Subfield('a', parsingText));
    }

    private void fillSupplement(Record r, String parsingText) {
        Field _421 = getOrCreateField("421", r);
        Field _200Sec = new Field("200");
        _200Sec.add(new Subfield('1', parsingText));
        _421.add(new Subfield('1'));
        _421.getSubfield('1').setSecField(_200Sec);

    }

    private void fillUDK(Record r, String parsingText) {
        String[] parts = parsingText.split("<br>|<br/>");
        for (String p: parts) {
            Field _675 = new Field("675");
            _675.add(new Subfield('a', p.trim()));
            r.add(_675);
        }
    }

    private void fillSubject(Record r, String parsingText) {
        String[] parts = parsingText.split("<br>|<br/>|<br />");
        for (String p: parts) {
            if (!p.trim().equals("")) {
                Field _610 = new Field("610");
                _610.add(new Subfield('a', p.trim()));
                r.add(_610);
            }
        }
    }

    private void fillNotes(Record r, String parsingText) {
        parsingText =parsingText
                .replace("&gt;", ">")
                .replace("&lt;", "<")
                .replace("&gt", ">")
                .replace("&lt", "<");;
        String[] parts = parsingText.split("<br>|<br/>");
        for (String p: parts) {
            if (!p.trim().equals("")) {
                Field _300 = new Field("300");
                _300.add(new Subfield('a', p.trim()));
                r.add(_300);
            }
        }
    }

    private void fillIsbn(Record r, String parsingText) {
        Field _010 = new Field("010");
        String betweenParenthesis = StringUtils.substringBetween(parsingText, "(", ")");
        parsingText = parsingText.replace("(" + betweenParenthesis + ")", "");
        if (!parsingText.equals("")) _010.add(new Subfield('a', parsingText.trim()));
        if (betweenParenthesis != null && !betweenParenthesis.equals("")) _010.add(new Subfield('b', betweenParenthesis));
        r.add(_010);
    }

    private void fillContent(Record r, String parsingText) {
        String brReg = "<br>|<br/>|<br />";
        parsingText = parsingText.replaceFirst(brReg, "");
        parsingText = parsingText.replace(brReg, ";");
        Field _327 = new Field("327");
        if (!parsingText.trim().equals("")) {
            _327.add(new Subfield('a', parsingText));
            r.add(_327);
        }
    }

    private void fillPhDescription(Record r, String parsingText) {
        String delimReg = " : | ; | \\+ ";
        String[] parts = parsingText.split(delimReg);
        Field _215 = new Field("215");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                _215.add(new Subfield('a', parts[i].trim()));
                continue;
            }
            char delim = ' ';
            try {
                int delimIndex = parsingText.indexOf(parts[i]) - 2;
                delim = parsingText.charAt(delimIndex);
            }
            catch (Exception e) {}
            switch (delim) {
                case ':': _215.add(new Subfield('c', parts[i].trim()));break;
                case ';': _215.add(new Subfield('d', parts[i].trim()));break;
                case '+': _215.add(new Subfield('e', parts[i].trim()));break;
            }
        }
        r.add(_215);
    }

    private void fillSeriesCard(Record r, String parsingText) {
        String delimReg = " ; | / | : |, ";
        String items[] = parsingText.split("<br>|<br/>");
        char previousDelim = ' ';
        for (String item: items) {
            String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
            String[] parts = item.split(delimReg);
            List delimiters = Arrays.asList(item.split(String.format(WITH_DELIMITER, delimReg)))
                    .stream().filter(i -> i.matches(delimReg)).collect(Collectors.toList());
            Field _225 = new Field("225");
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) { _225.add(new Subfield('a', parts[i])); continue; }
                char leftDelim = ' ';
                char rightDelim = ' ';
                try {
                    leftDelim = delimiters.get(i - 1).toString().trim().charAt(0);
                    rightDelim = delimiters.get(i).toString().trim().charAt(0);
                }
                catch (Exception e) {}
                switch (leftDelim) {
                    case ':': _225.add(new Subfield('e', parts[i].trim())); break;
                    case '.': _225.add(new Subfield('i', parts[i].trim())); break;
                    case '/': _225.add(new Subfield('f', parts[i].trim())); break;
//                    case ',': _225.add(new Subfield('x', parts[i].trim())); break;
                    case ';': _225.add(new Subfield('v', parts[i].trim())); break;
                }
                if (leftDelim == '.' && rightDelim == ',' && previousDelim == ',' && _225.getSubfieldContent('i') == null)
                    _225.add(new Subfield('i', parts[i].trim()));
                else if (leftDelim == '.' && rightDelim != ',' && _225.getSubfieldContent('i') == null)
                    _225.add(new Subfield('i', parts[i].trim()));
                previousDelim = rightDelim;
            }
            r.add(_225);
        }
    }

    private void fillEdition(Record r, String parsingText) {
        String delimitersReg = " = | ; |, | / ";
        String[] parts = parsingText.split(delimitersReg);
        Field _205 = null;
        if (parts.length > 0) {
            _205 = new Field("205");
            _205.add(new Subfield('a', parts[0]));
        }
        if (parts.length > 1) {
            for (int i = 1; i < parts.length; i++) {
                char delim = ' ';
                try {
                    int delimIndex = parsingText.indexOf(parts[i]) - 2;
                    delim = parsingText.charAt(delimIndex);
                }
                catch (Exception e) {}
                switch (delim) {
                    case '=' : _205.add(new Subfield('d', parts[i].trim())); break;
                    case ',' : _205.add(new Subfield('b', parts[i].trim())); break;
                    case '/' : _205.add(new Subfield('f', parts[i].trim())); break;
                    case ';' : _205.add(new Subfield('g', parts[i].trim())); break;
                }
            }
        }
        if (_205 != null) r.add(_205);
    }

    private void fillArticlePubDate(Record r, String parsingText) {
        if (r.getField("100") == null) r.add(new Field("100"));
        Field _100 = r.getField("100");
        _100.add(new Subfield('c', parsingText));
    }

    private void fillLanguage(Record r, String parsingText) {
        String[] parts = parsingText.split(",");
        Field _101 = new Field("101");
        Field _100 = null;
        if (parts.length > 1) _100 = new Field("100");
        for (int i = 0; i < parts.length; i ++) {
            String lan = Declarative2CodeMapper.mapLanguage2Coder(parts[i].trim());
            if (lan == null) continue;
            if (i != 0) _100.add(new Subfield('a', lan));
            else _101.add(new Subfield('a', lan));
        }
        if (_101.getSubfieldContent('a') != null) r.add(_101);
        if (_100 != null && _100.getSubfieldContent('a') != null)
            r.add(_100);
    }

    private void fillUniformTitle(Record r, String text) {
        String[] parsingTextParts = text.split("<br>|<br/>|<br />");
        for (String parsingText: parsingTextParts) {
            String[] parts = parsingText.split("\\. ");
            Field _500 = new Field("500");
            String a = "";
            String m = null;
            for (int i = 0; i < parts.length; i++) {
                if (parts.length > 1 && i == parts.length - 1) m = Declarative2CodeMapper.mapLanguage2Coder(parts[i]);
                else a += parts[i];
            }
            _500.add(new Subfield('a', a));
            if (m != null) _500.add(new Subfield('m', m));
            r.add(_500);
        }
    }

    private void fillParallelTitle(Record r, String text) {
        String[] pasingTextParts = text.split("\"<br>|<br/>|<br />\"");
        for (String parsingText: pasingTextParts) {
            Field _510 = new Field("510");
            _510.add(new Subfield('a', parsingText));
            r.add(_510);
        }
    }

    private void fillOtherTitles(Record r, String text) {
        String[] parsingTextParts = text.split("<br>|<br/>|<br />");
        for (String parsingText: parsingTextParts) {
            Field _540 = new Field("540");
            _540.add(new Subfield('a', parsingText));
            r.add(_540);
        }
    }

    private void fillTitleMainInfo(Record r, String parsingText) {
        String[] parts = parsingText.split(" / | : | ; | \\. | = | , ");
        if (r.getField("200") == null) r.add(new Field("200"));
        // ovo je slucaj kada je vec podesen indikator na polju 200 zbog autora
        Field _200 = r.getField("200");
        char lastLDelimiter = ' ';
        char lastRDelimiter = ' ';
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("Opasni proračun"))
                System.out.println("Lisa Luc");
            if (i == 0) _200.add(new Subfield('a', parts[0]));
            else {
                char leftDelimiter = ' ';
                try {
                    int leftDelimiterIndex = parsingText.indexOf(parts[i]) - 2;
                    leftDelimiter = parsingText.charAt(leftDelimiterIndex);
                } catch (Exception e) {}
                char rightDelimiter = ' ';
                try {
                    int rightDelimiterIndex = parsingText.indexOf(parts[i]) + parts[i].length() + 1;
                    rightDelimiter = parsingText.charAt(rightDelimiterIndex);
                } catch (Exception e) {}
//TODO: problemi sa potpoljima: h, i
                if (leftDelimiter == ':') _200.add(new Subfield('e', parts[i].trim()));
                else if (leftDelimiter == ';' && rightDelimiter == '/') _200.add(new Subfield('a', parts[i].trim()));
//                else if (leftDelimiter == '.' && rightDelimiter == ',') _200.add(new Subfield('h', parts[i].trim()));
                else if (rightDelimiter == '/') {

//                    _200.add(new Subfield('h', parts[i].trim()));
                    String[] hi = parts[i].split(", ");
                    if (hi.length > 1) {
                        _200.add(new Subfield('h', hi[0]));
                        hi[0] = "";
                        String  iContent = String.join("", hi);
                        if (!iContent.trim().equals("")) _200.add(new Subfield('i', iContent));
                    }
                }
                else if (leftDelimiter == ',') _200.add(new Subfield('i', parts[i].trim()));
                else if (leftDelimiter == '.' && rightDelimiter != ',') _200.add(new Subfield('i', parts[i].trim()));
                else if (leftDelimiter == '=') _200.add(new Subfield('d', parts[i].trim()));
                else if (leftDelimiter == '/') _200.add(new Subfield('f', parts[i].trim()));
                else if ((lastLDelimiter == '/' && leftDelimiter == ';')
                        || (lastLDelimiter == ';' && leftDelimiter == ';')&& !parts[i].equals(""))
                    _200.add(new Subfield('g', parts[i].trim()));
//                System.out.println(leftDelimiter);
//                System.out.println(rightDelimiter);
                lastLDelimiter = leftDelimiter;
                lastRDelimiter = rightDelimiter;
            }
        }
    }

    private void fillAuthorInfo(Record r, String parsingText, boolean otherAuthorsMode) {
        String[] parts = parsingText.split("<br>|<br/>");
        for (int i = 0; i < parts.length; i++) {
            String[] translatedParts = parts[i].split("=");
            for (int j = 0; j < translatedParts.length; j++) {
                translatedParts[j] = translatedParts[j].trim();
                Field field = new Field();

                if (parts.length < 4 && j == 0) field.setName(!otherAuthorsMode ? "700" : "702");
                // Specijalan slucaj kada treba podesiti indikator u 200
                else if (parts.length >= 4 && j == 0) {
                    field.setName(!otherAuthorsMode ? "701" : "702");
                    Field _200f = r.getField("200");

                    if (_200f != null && !otherAuthorsMode)
                        _200f.setInd1('1');
                    else if (_200f == null)
                        r.add(new Field("200",'1', ' '));
                }
                else field.setName(!otherAuthorsMode ? "900" : "902");

                String[] abf = translatedParts[j].split(",");
                if (abf.length > 0) field.add(new Subfield('a', abf[0]));

                if (abf.length > 1 && !abf[1].replace("-","").trim().matches(".*\\d.*"))
                    field.add(new Subfield('b',abf[1].trim()));
                if (abf.length > 1 && abf[1].replace("-","").trim().matches(".*\\d.*"))
                    field.add(new Subfield('f',abf[1].trim()));

                if (abf.length > 2 && !abf[2].replace("-","").trim().matches(".*\\d.*"))
                    field.add(new Subfield('b',abf[2].trim()));
                if (abf.length > 2 && abf[2].replace("-","").trim().matches(".*\\d.*"))
                    field.add(new Subfield('f',abf[2].trim()));

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
                if (eg.length > 0) _210.add(new Subfield('e', eg[0].trim()));
                if (eg.length > 1) _210.add(new Subfield('g', eg[1].trim()));
            }
            r.add(_210);
        }

    }


}
