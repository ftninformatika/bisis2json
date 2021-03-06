package bisis.model.librarian;

import bisis.model.format.PubTypes;
import bisis.model.format.UFormat;
import bisis.model.format.UIndicator;
import bisis.model.format.USubfield;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


//@Document(collection = "coders.process_types")
@Getter
@Setter
public class ProcessType implements Serializable {

  //private String _id;
  private Integer id;
  private String name;
  private UFormat pubType;
  private String libName;
  private List<USubfield> initialSubfields;
  private List<USubfield> mandatorySubfields;
  // koristice se za default vrednosti indikatora
  private List<UIndicator> indicators;

  public void setUFormat(Integer pubType){
      this.pubType = PubTypes.getPubType(pubType);
  }

  public static ProcessType getProcessType(String xml) {
    return ProcessTypeBuilder.getProcessType(xml);
  }
  
  public ProcessType() {
    initialSubfields = new ArrayList<USubfield>();
    mandatorySubfields = new ArrayList<USubfield>();
    indicators = new ArrayList<UIndicator>();
  }

  public ProcessType(String name, UFormat pubType,
      List<USubfield> initialSubfields, List<USubfield> mandatorySubfields, List<UIndicator> indicators) {
    this.name = name;
    this.pubType = pubType;
    this.initialSubfields = initialSubfields;
    this.mandatorySubfields = mandatorySubfields;
    this.indicators = indicators;
    
  }
  
  public ProcessType(String name, int pubType,
      List<USubfield> initialSubfields, List<USubfield> mandatorySubfields, List<UIndicator> indicators) {
    this.name = name;
    this.pubType = PubTypes.getPubType(pubType);
    this.initialSubfields = initialSubfields;
    this.mandatorySubfields = mandatorySubfields;
    this.indicators = indicators;
    this.libName = libName;
  }


	public boolean containsSubfield(USubfield usf){
		for(USubfield us:initialSubfields)
			if(us.equals(usf)) return true;
		return false;
	}
	
	public boolean containsSubfield(String subfieldName){		
		for(USubfield us:initialSubfields)
			if(subfieldName.equals(us.getOwner().getName()+us.getName())) 
				return true;
		return false;
	}
	
  public String toXML() {
    StringBuffer retVal = new StringBuffer();
    retVal.append("<?xml version=\"1.0\"?>\n");
    retVal.append("<process-type name=\"");
    retVal.append(name);
    retVal.append("\" pubType=\"");
    retVal.append(pubType.getPubType());    
    retVal.append("\">\n");
    for (USubfield s : initialSubfields) {
      retVal.append(" <initial-subfield name=\"");
      retVal.append(s.getOwner().getName()+s.getName());      
      retVal.append("\"");
      if(s.getDefaultValue()!=null){
      	retVal.append(" defaultValue=\"");
      	retVal.append(s.getDefaultValue());
      	retVal.append("\" ");
      }      	
      retVal.append("/>\n");
    }
    for (USubfield s : mandatorySubfields) {
      retVal.append("  <mandatory-subfield name=\"");
      retVal.append(s.getOwner().getName()+s.getName());
      retVal.append("\"/>\n");
    }
    for (UIndicator ui : indicators){
    	retVal.append("  <indicator field=\"");
    	retVal.append(ui.getOwner().getName());
    	retVal.append("\" ");
    	retVal.append("index=\"");
    	retVal.append(ui.getIndex());
    	retVal.append("\" ");
    	retVal.append("defaultValue=\"");
    	retVal.append(ui.getDefaultValue());
     retVal.append("\"/>\n");   	
    	
    }
    retVal.append("</process-type>\n");
    return retVal.toString();
  }
  
  public String toString() {
    //"return toXML();" //TODO-hardcoded
      return this.libName + " process type";
  }

	
}
