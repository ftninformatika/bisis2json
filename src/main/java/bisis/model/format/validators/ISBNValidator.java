package bisis.model.format.validators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bisis.model.format.UValidator;
import bisis.model.format.UValidatorException;

@SuppressWarnings("serial")
public class ISBNValidator implements UValidator {

	public ISBNValidator() {
		targets = new ArrayList<String>();
		targets.add("010a");
    log.info("Loading ISBN validator");
	}

	public List<String> getTargets() {		 
		return targets;
	}

	public String isValid(String content) {
		String retVal = "";
	    try {
	      validate(content);
	    } catch (UValidatorException ex) {
	      retVal = ex.getMessage();
	    }
	    return retVal;		
	}

	public void validate(String content) throws UValidatorException {
		int l = content.length();
    if (l != 10 && l != 13 && l != 17)
      throw new UValidatorException("Du\u017eina ISBN broja mora biti 13 ili 17!");
    else {
      int i = 0;
      int numPov = 0;
      boolean isValid = true;
      while (i < l && isValid) {
        if (!(Character.isDigit(content.charAt(i))
             || content.charAt(i) == '-'
             || content.charAt(i) == 'X'
             || content.charAt(i) == 'x'))
         isValid = false;
        if (content.charAt(i) == '-')
          numPov++;
        i++;
      }
      if ((numPov != 3 && numPov != 0 && l == 13) || 
          (numPov != 4 && l == 17) || 
          !isValid)
        throw new UValidatorException("Sintaksna gre\u0161ka ISBN broja!");
    }    
	}
	
	private List<String> targets;
  private static Log log = LogFactory.getLog(ISBNValidator.class.getName());

}
