package bisis.model.prefixes;

import bisis.model.prefixes.def.DefaultPrefixConfig;

/**
 * Produces PrefixMap objects according to the prefix.map system property.
 * 
 * @author mbranko@uns.ns.ac.yu
 */
public class PrefixConfigFactory {
  
  public static PrefixConfig getPrefixConfig() {
    return new DefaultPrefixConfig();
  }
}
