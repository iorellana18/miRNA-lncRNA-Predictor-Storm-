package usach.miRNA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.storm.shade.com.twitter.chill.Base64.OutputStream;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class EnergyBolt implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Tuple tuple) {
			String miRNA_id = tuple.getString(0);
			String miRNA = tuple.getString(1);
			String rev_mre = tuple.getString(2); // miRNA Reecognition Element
			String lncRNA_id = tuple.getString(3);
			String lncRNA = tuple.getString(4);
			int position = tuple.getInteger(5);
			
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
	        OutputStream ops = (OutputStream) p2.getOutputStream();
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
	                System.out.println(seq); 
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
	        
	       
	       collector.emit(new Values(miRNA_id,miRNA,lncRNA_id,lncRNA,rev_mre,position,dg_binding,dg_duplex,Sequence,code));
		
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector collector) {
		this.collector=collector;
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("miRNA_id","miRNA","lncRNA_id","lncRNA","rev_mre","position","dg_binding","dg_duplex","sequence","code"));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
