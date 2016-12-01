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

import EDA.EnergyStructure;
import EDA.RNAStructure;

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
		
	    String position = String.valueOf(RNA.getPosition());
	    float dg_duplex = Energy.getMfe();
	    float accessibilityEnergy = Energy.getDGAccessibility();
        String strDuplex = Float.toString(dg_duplex);
        String strAccess = Float.toString(accessibilityEnergy);
        String E3 = Float.toString(Energy.getRegion3());
        String E5 = Float.toString(Energy.getRegion5());
        
      //Generate CSV outfile
        FileOutputStream o;
        if (accessibilityEnergy < 0) {
            try{
                o = new FileOutputStream("/home/ian/Escritorio/results.csv",true);
                //mir_id, lncRNA transcript id, position of seed in transcript, dG duplex, dG binding, dG open, ddG
                o.write( (RNA.getmiRNA_id()+","+RNA.getLncRNA_id()+","+position+","+strDuplex+","+strAccess+","+E3+","+E5+"\n").getBytes() );
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
