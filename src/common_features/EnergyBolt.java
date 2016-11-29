package common_features;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.io.OutputStream;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import EDA.RNAStructure;

public class EnergyBolt implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private Map config;
	
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Tuple tuple) {
			RNAStructure RNA = (RNAStructure)tuple.getValueByField("RNA");
			String miRNA = RNA.getMiRNA();
			String rev_mre = RNA.getMre(); // miRNA Reecognition Element
			
	        String line;
	        String execstr = "RNAcofold -p --noPS"; ///Revisar diferencia con RNAfold LLLLL, diferencias en energía -> free energy of another regions
	        String seq = miRNA+"&"+rev_mre;
	        float dg_duplex = 0;
	        float dg_binding = 0;
	        int flag = 0;
	        
	        String Sequence="", code="";
	        
	          
	        try{
	        Process p2 = Runtime.getRuntime().exec(execstr);
	          
	        // Get input 
	        BufferedReader input_buffer = new BufferedReader(new InputStreamReader(p2.getInputStream()));
	        
	        // Generate response to command
	        OutputStream ops =  p2.getOutputStream();
	        ops.write(seq.getBytes());
	        ops.close();            
	        // Show final output
	        
	        
	        while ((line = input_buffer.readLine()) != null){
	            
	            if (line.contains("-")) {
	                String[] parts = line.split("-");
	                String number = parts[1]; //minimum free energy / free energy ensemble /delta G binding
	                if(number.charAt(number.length()-1)== ')' || number.charAt(number.length()-1)==']'){//Limpia número de paréntesis
	                    number=number.substring(0,number.length()-1);
	                }
	            if (flag == 0) {//-->Mínimum free energy
	                flag++;
	                dg_duplex = Float.parseFloat(number);
	                dg_duplex = dg_duplex*-1.0f;
	               // System.out.println("\n"+seq+"\n"); 
	                Sequence=seq;
	                code=parts[0];
	            }
	            else if (flag == 1){ //-->Free energy of ensemble
	                flag++;
	                dg_binding = Float.parseFloat(number);
	                dg_binding = dg_binding*-1.0f;
	                 
	            };
	          }
	            
	        }
	        input_buffer.close();
	        
	    }catch(IOException e){
	        System.out.println("IOException");
	    }
	        System.out.println("\n"+RNA.getLncRNA_id()+"\n");
	        
	       Values values = new Values(RNA,dg_binding,dg_duplex,Sequence,code);
	       this.collector.emit("energyStream",tuple,values);
		
	}

	@Override
	public void prepare(Map config, TopologyContext arg1, OutputCollector collector) {
		this.collector=collector;
		this.config = config;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("energyStream",new Fields("RNA","dg_binding","dg_duplex","sequence","code"));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return config;
	}

}
