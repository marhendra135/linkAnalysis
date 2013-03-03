package analyze;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.apache.lucene.util.Version;

public class SynonymAnalyzer extends Analyzer {
	/*
	 * This class implement the WordNet Synonym Analyzer
	 * */
	private StandardAnalyzer analyzer = null;
	private SynonymMap map = null;
	private StringReader in1 =null;
	public SynonymAnalyzer() throws IOException, ParseException{
	}
	
	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader){
		BufferedReader in = null;
		Tokenizer tokenizer = null;
		TokenStream result = null;
		
		StringBuffer buff = new StringBuffer();
		String s, s2 = "";
	    analyzer = new StandardAnalyzer(Version.LUCENE_41);
	    WordnetSynonymParser parser = new WordnetSynonymParser(true, true, analyzer);

			try {
				in = new BufferedReader(new FileReader("WordNet/wn_s.pl"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				while ((s = in.readLine()) != null){
					buff.append(s + "\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		    s2 = buff.toString();
		    in1 = new StringReader(s2);


			try {
				parser.add(in1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				map = parser.build();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		tokenizer  = new StandardTokenizer(Version.LUCENE_41, reader);
		result = new SynonymFilter(tokenizer , map, false);


	    return  (new TokenStreamComponents(tokenizer ,result));
	}
}


