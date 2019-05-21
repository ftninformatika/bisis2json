package bisis.apps.misc;

import bisis.model.records.Record;
import lombok.Getter;
import lombok.Setter;

/**
 * @author badf00d21  21.5.19.
 */
@Getter
@Setter
public class RowItem {

    public static String TABLE_HEADER = "R.br\tInv.br.\tSignatura\tAutor, naslov, mesto i godina izdavanja\tCena\tNapomena\n";

    public RowItem(String rbr, String invBroj, Record r){
        this.rBr = rbr;
        this.invBroj = invBroj;
        this.signatura = RecordUtilsData.getSignatura(RecordUtilsData.findInvUnit(invBroj, r));
        this.glavniSadrzaj = RecordUtilsData.getMainContent(r);
        this.cena = RecordUtilsData.getCena(RecordUtilsData.findInvUnit(invBroj, r));
        this.napomena = RecordUtilsData.getNapomena(RecordUtilsData.findInvUnit(invBroj, r));
    }

    private String rBr;
    private String invBroj;
    private String signatura;
    private String glavniSadrzaj;
    private String cena;
    private String napomena;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(rBr + "\t");
        sb.append("\'" + invBroj + "\'\t");
        sb.append(signatura + "\t");
        sb.append(glavniSadrzaj.replace(";", "") + "\t");
        sb.append(cena + "\t");
        sb.append(napomena + "\n");
        return sb.toString();
    }

}
