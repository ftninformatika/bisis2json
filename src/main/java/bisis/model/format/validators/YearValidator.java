package bisis.model.format.validators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bisis.model.format.UValidator;
import bisis.model.format.UValidatorException;

/**
 * Class YearValidator comment.
 * 
 * @author mbranko@uns.ns.ac.yu
 */
@SuppressWarnings("serial")
public class YearValidator implements UValidator {

  public YearValidator() {
    targets = new ArrayList<String>();
    targets.add("100c");
    targets.add("100d");    
    log.info("Loading year validator");
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
    if (content.length() != 4)
      throw new UValidatorException("Godina mora imati \u010detiri cifre!\nNepoznatu godinu ozna\u010dite sa ???? umesto [b.g.] ili [s.a.]");
    for (int i = 0; i < 4; i++) {
      char cifra = content.charAt(i);
      if (!(Character.isDigit(cifra) || cifra == '?'))
        throw new UValidatorException(
            "Godinu mogu ozna\u010davati cifre i upitnik!");
    }
  }
  
  private List<String> targets;
  private static Log log = LogFactory.getLog(YearValidator.class.getName());
}
