package bisis.utils.textsrv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import bisis.model.prefixes.PrefixConfigFactory;
import bisis.model.prefixes.PrefixConverter;
import bisis.model.prefixes.PrefixValue;
import bisis.model.records.Record;
import bisis.utils.LatCyrUtils;
import bisis.utils.StringUtils;

public class Indexer {

  public Indexer() {
  }
  
  public Indexer(String indexPath) {
    this.indexPath = indexPath;
  }
  
  public String getIndexPath() {
    return indexPath;
  }

  public void setIndexPath(String indexPath) {
    this.indexPath = indexPath;
  }
  
  /**
   * Adds a new record to the index.
   * @param rec Record to be added
   * @return true if successful
   */
  public boolean add(Record rec) {
    try {
      IndexWriter iw = getIndexWriter(); 
      iw.addDocument(getDocument(rec));
      iw.close();
    } catch (IOException ex) {
      log.fatal(ex);
      return false;
    }
    return true;
  }
  
  /**
   * Updates a record in the index
   * @param rec Record to update
   * @return true if successful
   */
  public boolean update(Record rec) {
    if (!delete(rec))
      return false;
    return add(rec);
  }
  
  /**
   * Deletes a record from the index
   * @param rec Record to delete
   * @return true if successful
   */
  public boolean delete(Record rec) {
    return delete(rec.getRecordID());
  }
  
  /**
   * Deletes a record from the index
   * @param recordID Record ID
   * @return true if successful
   */
  public boolean delete(int recordID) {
    try {
      IndexReader indexReader = IndexReader.open(indexPath);
      Term term = new Term("ID", Integer.toString(recordID));
      indexReader.deleteDocuments(term);
      indexReader.close();
      return true;
    } catch (IOException ex) {
      log.fatal(ex);
      return false;
    }
  }
  
  /**
   * Optimizes the Lucene index.
   * @return true if successful
   */
  public boolean optimize() {
    try {
      IndexWriter indexWriter = getIndexWriter();
      indexWriter.optimize();
      return true;
    } catch (IOException ex) {
      log.fatal(ex);
      return false;
    }
  }
  
  /**
   * Constructs a Lucene document containing prefixes from the given record.
   * @param rec Source record
   * @return A new Lucene document
   */
  protected Document getDocument(Record rec) {
    Document doc = new Document();
    Field id=new Field("ID",
    		    Integer.toString(rec.getRecordID()), 
    	        Field.Store.YES, 
    	        Field.Index.UN_TOKENIZED, 
    	        Field.TermVector.NO);
    
    doc.add(id);
    
    Set<String> sortPrefixes = PrefixConfigFactory.getPrefixConfig().getSortPrefixes();
    Iterator<PrefixValue> prefixes = PrefixConverter.toPrefixes(rec, null).iterator();
    while (prefixes.hasNext()) {
      PrefixValue pref = prefixes.next();
      String value = LatCyrUtils.toLatin(pref.value);
      String valueWithoutAccent=LatCyrUtils.removeAccents(value);
      Field f = null;
      Field f1 = null;
      if(nontokenized.contains(pref.prefName)){
        f=new Field(pref.prefName, value.toLowerCase(),
            Field.Store.YES, 
            Field.Index.UN_TOKENIZED, 
            Field.TermVector.NO);      
      } else if(isbnList.contains(pref.prefName)){ //zbog ISBN
    	  value=StringUtils.clearDelimiters(value, delims);
    	  value=value.replace(" ", "");
    	  f=new Field(pref.prefName, "0start0 " + value.toLowerCase() + " 0end0", 
    	            Field.Store.YES, 
    	            Field.Index.TOKENIZED, 
    	            Field.TermVector.WITH_POSITIONS_OFFSETS); 
    	  
      }else {
    	  value=StringUtils.clearDelimiters(value, delims);// da bi izbacio sve znakove interpukcije osim za UDK,ISBN, ISSN
    	  valueWithoutAccent=StringUtils.clearDelimiters(valueWithoutAccent, delims);
          f=new Field(pref.prefName, "0start0 " + value.toLowerCase() + " 0end0", 
          Field.Store.YES, 
          Field.Index.TOKENIZED, 
          Field.TermVector.WITH_POSITIONS_OFFSETS);
          
          f1=new Field(pref.prefName, "0start0 " + valueWithoutAccent.toLowerCase() + " 0end0", 
                  Field.Store.YES, 
                  Field.Index.TOKENIZED, 
                  Field.TermVector.WITH_POSITIONS_OFFSETS); 
          doc.add(f1);
      }
      doc.add(f);
      
      if (sortPrefixes.contains(pref.prefName)) {
        if (doc.getField(pref.prefName+"_sort") == null)
          doc.add(new Field(pref.prefName+"_sort", value.toLowerCase(), 
              Field.Store.YES, 
              Field.Index.UN_TOKENIZED, 
              Field.TermVector.NO));
      }
    }
    return doc;
  }
  
  
  /**
   * Returns a new Lucene index writer. Creates the index if necessary.  
   * @return
   */
  protected IndexWriter getIndexWriter() {
    try {
      boolean createIndex = true;
      File testIndexPath = new File(indexPath);
      if (!testIndexPath.exists())
        testIndexPath.mkdirs();
      if (testIndexPath.isDirectory()) {
        if (testIndexPath.list().length > 0)
          createIndex = false;
        return new IndexWriter(indexPath, new WhitespaceAnalyzer(), createIndex);
      }
    } catch (Exception ex) {
      log.fatal(ex);
    }
    return null;
  }
  
  protected String indexPath;
  private static List<String> nontokenized=new ArrayList<String>();
  private static List<String> isbnList=new ArrayList<String>();
  static{
	  nontokenized.add("DC");
	 // nontokenized.add("SN"); //za isbn i issn izbacujemo crtice
	 // nontokenized.add("SP");
	//  nontokenized.add("SC");
	//  nontokenized.add("BN");
	  nontokenized.add("675a");
	//  nontokenized.add("010a");
	//  nontokenized.add("011a");
	  nontokenized.add("IN");
	
  }
  static{

	  isbnList.add("SN"); //za isbn i issn izbacujemo crtice
	  isbnList.add("SP");
	  isbnList.add("SC");
	  isbnList.add("BN");;
	  isbnList.add("010a");
	  isbnList.add("011a");	
  }
  
  private static String delims = "*?,;:\"()[]{}+/.!-" ;
  private static Log log = LogFactory.getLog(Indexer.class);
}
