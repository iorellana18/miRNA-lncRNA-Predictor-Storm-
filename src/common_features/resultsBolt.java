package common_features;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import EDA.BindingArea;
import EDA.EnergyStructure;
import EDA.RNAStructure;
import EDA.Stats;

public class resultsBolt implements IRichBolt{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private Map config;
	
	private static Float precision(int decimalPlace, Float d) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_UP);
        return bd.floatValue();
    }

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Tuple tuple) {
		RNAStructure RNA = (RNAStructure) tuple.getValueByField("RNA");
		EnergyStructure Energy = (EnergyStructure)tuple.getValueByField("Energy");
		Stats stats = (Stats)tuple.getValueByField("Stats");
		BindingArea area = (BindingArea)tuple.getValueByField("Area");
		
	    String position = String.valueOf(RNA.getPosition());
        String strDuplex = Float.toString(Energy.getMfe());
        String strAccess = Float.toString(Energy.getDGAccessibility());
        String E3 = Float.toString(Energy.getRegion3());
        String E5 = Float.toString(Energy.getRegion5());
        String matches = Integer.toString(stats.getMatch());
        String miss = Integer.toString(stats.getMiss());
        String AU = Integer.toString(stats.getAU());
        String GC = Integer.toString(stats.getGC());
        String GU = Integer.toString(stats.getGU());
        String c1 = area.getMiRNA_miss();
        String c2 = area.getMiRNA();
        String c3 = area.getLink();
        String c4 = area.getLncRNA();
        String c5 = area.getLncRNA_miss();
        
      //Generate CSV outfile
        FileOutputStream o;
        if (Energy.getDGAccessibility() < 0) {
            try{
                o = new FileOutputStream("/home/ian/Escritorio/results.csv",true);
                //mir_id, lncRNA transcript id, position of seed in transcript, dG duplex, dG binding, dG open, ddG
                o.write( (RNA.getmiRNA_id()+","+RNA.getLncRNA_id()+","+position+","+strDuplex+","+strAccess+","+E3+","+E5+","+matches+","+miss+","+AU+","+GC+","+GU+","+c1+","+c2+","+c3+","+c4+","+c5+"\n").getBytes() );
                o.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        
		
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector collector) {
		this.collector = collector;
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return config;
	}

}
