package bisis.apps.export.csv;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author badf00d21  11.4.19.
 */
@NoArgsConstructor
@Getter
@Setter
public class CsvItem {
    String rn;
    String mongoid;
    String library;
    String author_full;
    String author_name;
    String author_surname;
    String title;
    String language;
    String publisher;
    String publication_year;
    String udc;
    String ctlg_no;
    String user_id;
    String lend_date;
    String location;

    public String toString() {
        StringBuffer retVal = new StringBuffer();
        retVal.append(rn);
        retVal.append("\t");
        retVal.append(mongoid);
        retVal.append("\t");
        retVal.append(library);
        retVal.append("\t");
        retVal.append(author_full);
        retVal.append("\t");
        retVal.append(author_name);
        retVal.append("\t");
        retVal.append(author_surname);
        retVal.append("\t");
        retVal.append(title);
        retVal.append("\t");
        retVal.append(language);
        retVal.append("\t");
        retVal.append(publisher);
        retVal.append("\t");
        retVal.append(publication_year);
        retVal.append("\t");
        retVal.append(udc);
        retVal.append("\t");
        retVal.append(ctlg_no);
        retVal.append("\t");
        retVal.append(user_id);
        retVal.append("\t");
        retVal.append(lend_date);
        retVal.append("\t");
        retVal.append(location);
        retVal.append("\n");
        return retVal.toString();
    }
}
