package common_features;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import EDA.BindingArea;
import EDA.EnergyStructure;
import EDA.Print;
import EDA.RNAStructure;
import EDA.Stats;

public class resultsBolt implements IRichBolt{

	
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private Map config;
	List<Print> lista = new ArrayList<Print>();
	
	

	@Override
	public void cleanup() {
		
	}

	boolean hola=false;
	int counter=0;
	int counter2=0;
	@Override
	public void execute(Tuple tuple) {
		if(tuple.getValueByField("RNA").toString().equals("LastTuple")){
			Collections.sort(lista, new Comparator<Print>(){
	            public int compare( Print o1, Print o2){
	            return o1.getOrder() - o2.getOrder();
	          }
	        });
			for(int i=0;i<lista.size();i++){
				//Generate CSV outfile
		        FileOutputStream o;
		            try{
		                o = new FileOutputStream("output/results.csv",true);
		                //mir_id, lncRNA transcript id, position of seed in transcript, dG duplex, dG binding, dG open, ddG
		                o.write( (lista.get(i).getPrint()).getBytes() );
		                o.close();
		            }catch(IOException e){
		                e.printStackTrace();
		            }
		        
			}
		}else{
		RNAStructure RNA = (RNAStructure) tuple.getValueByField("RNA");
		EnergyStructure Energy = (EnergyStructure)tuple.getValueByField("Energy");
		Stats stats = (Stats)tuple.getValueByField("Stats");
		BindingArea area = (BindingArea)tuple.getValueByField("Area");
		String toPrint=RNA.toString()+Energy.toString()+stats.toString()+area.toString()+"\n";
		Print objectToPrint = new Print(toPrint,Energy.getFormatMfe());
		lista.add(objectToPrint);
		
      
        
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
