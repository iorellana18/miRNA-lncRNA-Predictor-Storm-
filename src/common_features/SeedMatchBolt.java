package common_features;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import EDA.RNAStructure;

public class SeedMatchBolt implements IRichBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private Map mapConf;

	public String complementSeq(String seq) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < seq.length(); i++) {
			if (seq.charAt(i) == 'G') {
				builder.append('C');
			}
			if (seq.charAt(i) == 'C') {
				builder.append('G');
			}
			if (seq.charAt(i) == 'A') {
				builder.append('U');
			}
			if (seq.charAt(i) == 'U') {
				builder.append('A');
			}
		}
		return builder.toString();
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(Tuple tuple) {

		// Obtiene cadena de spout
		String[] parts =tuple.getValueByField("cadena").toString().split("LLLL");

		String miRNA_id = parts[0];
		String miRNA = parts[1];
		String lncRNA_id = parts[2];
		String lncRNA = parts[3];

		String rev_lncRNA = new StringBuilder(lncRNA).reverse().toString();
		String miRNA_seed = miRNA.substring(1, 8);

		int lncRNA_size = rev_lncRNA.length();
		int seed_size = miRNA_seed.length();
		int inf = 0;
		int sup = seed_size;
		int position = 0; // Position of the binding (seed)
		String rev_mre, mre;

		// Obtiene miRNA Recognition Element
		while (sup <= lncRNA_size) {
			String lncRNA_window = rev_lncRNA.substring(inf, sup); // 3'-5'
			String lncRNA_comp = complementSeq(lncRNA_window); // 3'-5'
			if (miRNA_seed.equals(lncRNA_comp)) { // seed: 5'-3' y lncRNA_com en
													// 3'-5'
				// Condiciones de borde (izq y derecha)
				if ((sup + (miRNA.length() - 8)) <= rev_lncRNA.length() && (inf - 1) >= 0) {
					// String lncRNACompleteWindow =
					// rev_lncRNA.substring(inf-1,sup+miRNA.length()-8);
					// miRNA Recognition Element
					mre = rev_lncRNA.substring((inf - 1), (sup + (miRNA.length() - 8))); // MRE
																							// 3'-5'
					rev_mre = new StringBuilder(mre).reverse().toString(); // MRE
																			// 5'-3'
					position = lncRNA_size - sup;
					// mre y pos son los diferentes en cada emit
					// formato de energía requiere mre en dirección 5' - 3'
					// ->rev_mre
					
					RNAStructure RNA = new RNAStructure(miRNA_id,miRNA,lncRNA_id,lncRNA,rev_mre,position);
					Values values = new Values(RNA);
					this.collector.emit("seedStream",tuple,values);
					
				}

			}

			inf += 1;
			sup += 1;

		}

	}

	@Override
	public void prepare(Map mapConf, TopologyContext arg1, OutputCollector collector) {
		this.collector = collector;
		this.mapConf = mapConf;

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("seedStream", new Fields("RNA"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return mapConf;
	}

}
