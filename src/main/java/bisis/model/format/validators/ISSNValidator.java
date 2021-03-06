package bisis.model.format.validators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bisis.model.format.UValidator;
import bisis.model.format.UValidatorException;

@SuppressWarnings("serial")
public class ISSNValidator implements UValidator {

	public ISSNValidator() {
		targets = new ArrayList<String>();
		targets.add("011a");
    log.info("Loading ISSN validator");
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
	    return retVal;	}	

	public void validate(String content) throws UValidatorException {
		if (content.length() != 9)
      throw new UValidatorException("Du\u017eina ISSN broja mora biti 9!");
    else {
      int i = 0;
      boolean isValid = true;
      while (i < content.length() && isValid) {
        if (i == 4 && content.charAt(i) != '-')
          throw new UValidatorException("Na petom mestu ISSN mora biti '-'!");
        else {
          if (!(Character.isDigit(content.charAt(i))
               || content.charAt(i) == 'X'
               || content.charAt(i) == 'x'
               || (i == 4 && content.charAt(i) == '-')))
            isValid = false;
        }
        i++;
      }
      if (!isValid)
        throw new UValidatorException("Sintaksna gre\u0161ka ISSN broja!");
    }
	}
  
  private static Log log = LogFactory.getLog(ISSNValidator.class.getName());
	
	private List<String> targets;
}
