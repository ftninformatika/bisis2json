package bisis.model.jongo_records;

import bisis.model.records.Sveska;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class JoSveska implements Serializable {

    private int sveskaID;
    private String invBroj;
    private String status;

    private Date datumStatusa;

    private int stanje;
    private String signatura;
    private BigDecimal cena;
    private String brojSveske;
    private String knjiga;
    private String inventator;
    private JoGodina parent;
    private int version;

    public JoSveska() {
    }

    public JoSveska(Sveska sveska) {
        this.sveskaID = sveska.getSveskaID();
        this.invBroj = sveska.getInvBroj();
        this.status = sveska.getStatus();
        this.datumStatusa = sveska.getDatumStatusa();
        this.stanje = sveska.getStanje();
        this.signatura = sveska.getSignatura();
        this.cena = sveska.getCena();
        this.brojSveske = sveska.getBrojSveske();
        this.knjiga = sveska.getKnjiga();
        this.inventator = sveska.getInventator();
        this.parent = new JoGodina(sveska.getParent());
        this.version = sveska.getVersion();
    }

  /*public Sveska(int sveskaID, String invBroj, String status, String signatura,
      BigDecimal cena, String brojSveske, Godina parent, int stanje) {
    this.sveskaID = sveskaID;
    this.invBroj = invBroj;
    this.status = status;
    this.signatura = signatura;
    this.cena = cena;
    this.brojSveske = brojSveske;
    this.parent = parent;
    this.stanje = stanje;
  }*/

    public String getBrojSveske() {
        return brojSveske;
    }
    public void setBrojSveske(String brojSveske) {
        this.brojSveske = brojSveske;
    }
    public BigDecimal getCena() {
        return cena;
    }
    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }
    public String getInvBroj() {
        return invBroj;
    }
    public void setInvBroj(String invBroj) {
        this.invBroj = invBroj;
    }
    public String getSignatura() {
        return signatura;
    }
    public void setSignatura(String signatura) {
        this.signatura = signatura;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Date getDatumStatusa(){
        return datumStatusa;
    }
    public void setDatumStatusa(Date datumStatusa){
        this.datumStatusa = datumStatusa;
    }
    public JoGodina getParent() {
        return parent;
    }
    public void setParent(JoGodina parent) {
        this.parent = parent;
    }
    public int getSveskaID() {
        return sveskaID;
    }
    public void setSveskaID(int sveskaID) {
        this.sveskaID = sveskaID;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public int getStanje() {
        return stanje;
    }
    public void setStanje(int stanje) {
        this.stanje = stanje;
    }

    public String getKnjiga() {
        return knjiga;
    }

    public void setKnjiga(String knjiga) {
        this.knjiga = knjiga;
    }

    public String getInventator() {
        return inventator;
    }

    public void setInventator(String inventator) {
        this.inventator = inventator;
    }

    public JoSveska copy(){
        JoSveska svRet = new JoSveska();
        svRet.setBrojSveske(getBrojSveske());
        svRet.setCena(getCena());
        svRet.setDatumStatusa(getDatumStatusa());
        svRet.setInvBroj(getInvBroj());
        svRet.setParent(getParent());
        svRet.setSignatura(getSignatura());
        svRet.setStanje(getStanje());
        svRet.setStatus(getStatus());
        svRet.setSveskaID(getSveskaID());
        svRet.setVersion(getVersion());
        svRet.setKnjiga(getKnjiga());
        svRet.setInventator(getInventator());
        return svRet;
    }



    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }


}