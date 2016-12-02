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
	
	public static Float precision(int decimalPlace, Float d) {
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

        
      //Generate CSV outfile
        FileOutputStream o;
        if (Energy.getDGAccessibility() < 0) {
            try{
                o = new FileOutputStream("/home/ian/Escritorio/results.csv",true);
                //mir_id, lncRNA transcript id, position of seed in transcript, dG duplex, dG binding, dG open, ddG
                o.write( (RNA.toString()+Energy.toString()+stats.toString()+area.toString()+"\n").getBytes() );
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
