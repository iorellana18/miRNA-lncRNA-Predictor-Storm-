package inSite_features;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import EDA.EnergyStructure;
import EDA.RNAStructure;

public class EnergyOfAnotherRegionsBolt implements IRichBolt{
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private Map config;

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Tuple tuple) {
        String line;
        String execstr = "RNAcofold -p --noPS"; ///Revisar diferencia con RNAfold LLLLL, diferencias en energía -> free energy of another regions
        StringBuilder miRNA_Region3 = new StringBuilder();
        StringBuilder miRNA_Region5 = new StringBuilder();
        StringBuilder lncRNA_Region3 = new StringBuilder();
        StringBuilder lncRNA_Region5 = new StringBuilder();
        
        RNAStructure RNA = (RNAStructure) tuple.getValueByField("RNA");
        EnergyStructure energy = (EnergyStructure)tuple.getValueByField("Energy");
        
        String miRNA = RNA.getMiRNA();
        String rev_mre = RNA.getMre();
        String Sequence = (String)tuple.getValueByField("sequence");
        String Code = (String)tuple.getValueByField("code");
        float mfe = energy.getMfe();
        float binding = energy.getBinding();
        float open = energy.getDGOpen();
        float accessibility = energy.getDGAccessibility();
        
        int miRNA_length = miRNA.length();
        int i=0, j=0;
        float mfe_Region3 = 0;
        float mfe_Region5 = 0;
        int flag = 0;
        
        
        while(i<miRNA_length){
            if(i<8){
                miRNA_Region5.append(miRNA.charAt(i));
            }else{
                miRNA_Region3.append(miRNA.charAt(i));
            }
            i++;
        }
        while(j<miRNA_length){
            if(j<miRNA_length-8){
                lncRNA_Region3.append(rev_mre.charAt(j));
            }else{
                lncRNA_Region5.append(rev_mre.charAt(j));
            }
            j++;
        }
        
        String seq1=miRNA_Region3+"&"+lncRNA_Region3;
        String seq2=miRNA_Region5+"&"+lncRNA_Region5;
        
        ///Obtiene energía mínima de región 3 
        try{
            Process p2 = Runtime.getRuntime().exec(execstr);

            // Get input 
            BufferedReader input_buffer = new BufferedReader(new InputStreamReader(p2.getInputStream()));

            // Generate response to command
            OutputStream ops = p2.getOutputStream();
            ops.write(seq1.getBytes());
            ops.close(); 
            
            while ((line = input_buffer.readLine()) != null){
                if (line.contains("-")) {
                    String[] parts = line.split("-");
                    String number = parts[1]; //minimum free energy / free energy ensemble /delta G binding
                    if(number.charAt(number.length()-1)== ')' || number.charAt(number.length()-1)==']'){//Limpia número de paréntesis
                        number=number.substring(0,number.length()-1);
                    }
                    if (flag == 0) {//-->Mínimum free energy
                        flag++;
                        mfe_Region3 = Float.parseFloat(number);
                        mfe_Region3 = mfe_Region3*-1.0f; 
                        //System.out.println("Region 3: "+mfe_Region3);       
                    }
                    
                }
            
            }
            input_buffer.close();
            
         
        }catch(IOException e){
        System.out.println("IOException");
        
        }
        flag=0;
        
        // Obtiene energía de región 5 (semilla)
        try{
            Process p3 = Runtime.getRuntime().exec(execstr);
            BufferedReader input_buffer2 = new BufferedReader(new InputStreamReader(p3.getInputStream()));
            OutputStream ops2 = p3.getOutputStream();
            ops2.write(seq2.getBytes());
            ops2.close(); 
            while ((line = input_buffer2.readLine()) != null){
                if (line.contains("-")) {
                    String[] parts = line.split("-");
                    String number = parts[1]; //minimum free energy / free energy ensemble /delta G binding
                    if(number.charAt(number.length()-1)== ')' || number.charAt(number.length()-1)==']'){//Limpia número de paréntesis
                        number=number.substring(0,number.length()-1);
                    }
                    if (flag == 0) {//-->Mínimum free energy
                        flag++;
                        mfe_Region5 = Float.parseFloat(number);
                        mfe_Region5 = mfe_Region5*-1.0f; 
                        //System.out.println("Region 5: "+mfe_Region5);       
                    }
                    
                }
            
            }
        
            input_buffer2.close();
            
        }catch(IOException e){
            System.out.println("IOExeotion");
        }
        
        
        energy = new EnergyStructure(mfe,binding,open,accessibility,mfe_Region3,mfe_Region5);
       //Implementar EDA para Energias
        Values values = new Values(RNA,energy,Sequence,Code);
        this.collector.emit(values);
		
	}

	@Override
	public void prepare(Map config, TopologyContext arg1, OutputCollector collector) {
		this.collector=collector;
		this.config = config;
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("RNA","Energy","Sequence","Code"));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
