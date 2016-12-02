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

import EDA.EnergyStructure;
import EDA.RNAStructure;

public class AccessibilityBolt implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private Map mapConf;

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Tuple tuple) {
		RNAStructure RNA = (RNAStructure) tuple.getValueByField("RNA");
		EnergyStructure Energy = (EnergyStructure) tuple.getValueByField("Energy");
		String miRNA = RNA.getMiRNA();
		String lncRNA = RNA.getLncRNA();
		String rev_mre = RNA.getMre();
		float DG_duplex = Energy.getMfe();
		float binding = Energy.getBinding();
		String Sequence = tuple.getValueByField("sequence").toString();
		String code = tuple.getValueByField("code").toString();
		
		
        String window = "";
        int window_length = 70;
        int lncRNA_length = lncRNA.length();
        int mirna_length = miRNA.length();
        //Inicio y fin de miRNA en lncRNA
        int pos = lncRNA.indexOf(rev_mre);
        int pos_f = pos + mirna_length;
        
        //Need to validate the positions
        //fix left side
        
        //Extrae 70 hacia adelante y hacia atras desde la posición de miRNA en lncRNA?
        //if(lncRNA.length()>window_length){
	        if ( ( (pos - window_length) < 0 ) ) {
	            window = lncRNA.substring( 0 , pos_f + window_length );
	        }
	        //fix right side
	        else if ( ( ( pos_f + window_length ) > lncRNA_length ) ){
	            window = lncRNA.substring( pos - window_length, lncRNA_length );
	        }
	        //fix nothing because it's not necessary 
	        else{
	            window = lncRNA.substring( pos - window_length , pos_f + window_length );
	        }
       /* }else{
        	window = lncRNA;
        }*/
        //System.out.println(window);
        String line2;
        String line_;
        
        String exec_1 = "RNAfold -p --noPS"; // minimum free energy / free energy of ensemble // {datos misteriosos} frequency of mfe structure in enseble, enseble diversity
        String exec_2 = "RNAfold -C -p --noPS"; //Creo que es para buscar match en otras partes, revisar
        String constraint = "";
        String x_constraint = "";
        String new_constraint = "";

        //Calculation of deltaG open
        int length = window.length();
        float dg0 = 0; //Ensemble free energy of the target without constraints and with u/p and d/s flanking bases (+- 70)
        float dg1 = 0; //Ensemble free energy of the target with constraints applied
        float dgOpen = 0; //dg0 - dg1
        //End of variables
        
        
        //Calculo de dg0

        try{
            //Start process
            Process p = Runtime.getRuntime().exec(exec_1);
        
            // Get input 
            BufferedReader _input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            // Generate response to command
            OutputStream ops = p.getOutputStream();
            ops.write(window.getBytes());
            ops.close();            

            // Show final output
            ////Free energy without constraints
            while ((line2 = _input.readLine()) != null){
                //System.out.println(line2);
                if ( line2.contains("[") ) {
                    //System.out.println(line2);
                    String[] parts = line2.split("\\[");
                    String number = parts[1];
                    number = number.substring(1,number.length()-1);
                    dg0 = Float.parseFloat(number);
                    dg0 = dg0*-1.0f; // Free energy of ensemble
                }
            }
            _input.close();
        }catch(IOException e){
            System.out.println("error");
        }

        
        //Calculo de dg1 -- largo de ventana
        for (int i = 0; i < length ; i++ ) {
            constraint = constraint + ".";   
        }
        
        int k_up = 70 + mirna_length; 
        int k_down = 70;

        for (int p = k_down; p < k_up ; p++ ) {
            x_constraint = x_constraint + "x";  
        }

        int pos2 = window.indexOf(rev_mre);
        int pos2_f = pos2 + mirna_length;
        
        
        new_constraint = constraint.substring(0,pos2) + x_constraint + constraint.substring(pos2_f,length);
        String dg1_param = window + "\n" + new_constraint;//Notacion x=posicion de semilas, .= ventana
        //System.out.println(dg1_param);System.out.println("");
        try{
            //Start process
            Process p_ = Runtime.getRuntime().exec(exec_2);
        
            // Get input 
            BufferedReader input_ = new BufferedReader(new InputStreamReader(p_.getInputStream()));
            
            // Generate response to command
            OutputStream ops_ = p_.getOutputStream();
            ops_.write(dg1_param.getBytes());
            ops_.close();            

            // Show final output
            // Free energy with constraints
            while ((line_ = input_.readLine()) != null){
                //System.out.println(line_);
                if ( line_.contains("[")) {
                    String[] parts_ = line_.split("\\[");
                    String number_ = parts_[1];
                    number_ = number_.substring(1,number_.length()-1);
                    dg1 = Float.parseFloat(number_);
                    dg1 = dg1*-1.0f;
                   // System.out.println("dg1: " + dg1); //ver que es dg1
                }
            }
            input_.close();
        }catch(IOException e_){
            System.out.println("error");
        }

        dgOpen = dg0 - dg1;
        double formated_dgOpen = Math.round(dgOpen*100.0) / 100.0;
       
       float AccessibilityEnergy= DG_duplex-(float)formated_dgOpen;
        
       Energy = new EnergyStructure(DG_duplex,binding,(float)formated_dgOpen,AccessibilityEnergy);
        
       this.collector.emit(new Values(RNA,Energy,Sequence,code));
       
		
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector collector) {
		this.collector=collector;
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("RNA","Energy","sequence","code"));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return mapConf;
	}

}
