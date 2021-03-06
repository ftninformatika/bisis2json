package bisis.utils.textsrv;

import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.Filter;


@SuppressWarnings("serial")
public class BisisFilter extends Filter {
	
	private List<String> inventarni;
  
	public BisisFilter(List<String> inventarniBrojevi){
		inventarni = inventarniBrojevi;
	}
	
	public BitSet bits(IndexReader reader) throws IOException {
	    BitSet bits = new BitSet(reader.maxDoc());

	    int[] docs = new int[1];
	    int[] freqs = new int[1];
	    Iterator<String> it = inventarni.iterator(); 
	    while (it.hasNext()){
	      String broj = it.next();
	      if (broj != null) {
	        TermDocs termDocs =
	            reader.termDocs(new Term("IN", broj));
	        int count = termDocs.read(docs, freqs);
	        if (count == 1) {
	          bits.set(docs[0]);
	        }
	      }
	    }
	    return bits;
	 }
}
