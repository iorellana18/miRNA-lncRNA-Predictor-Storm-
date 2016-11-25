package usach.miRNA;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class RNASpout implements IRichSpout{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	private TopologyContext context;
	private Map conf;
	Stack stack = new Stack();
	
	@Override
	public void ack(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fail(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nextTuple() {
        while(stack.isEmpty()==false){
        	Values values = new Values(stack.pop());
        	this.collector.emit("streamSpout",values);
        }
	
	}

	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.context=context;
		this.collector=collector;
		this.conf = conf;
		
		
		
		//Lee archivos .fa y los guarda en buffer
        FileReader miRNA_Reader, lnc_Reader;
        ArrayList<String> miRNA = new ArrayList();
        ArrayList<String> lncRNA = new ArrayList();
        try {
                miRNA_Reader = new FileReader("/home/ian/Escritorio/mature_small.fa");
                lnc_Reader = new FileReader("/home/ian/Escritorio/lnc_small.fa");
        } catch (FileNotFoundException e) {
                throw new RuntimeException("Error reading file");
        }
        
        BufferedReader miRNA_buffer= new BufferedReader(miRNA_Reader);
        BufferedReader lnc_buffer = new BufferedReader(lnc_Reader);
        
        FileOutputStream o;
        try{
                o = new FileOutputStream("/home/ian/Escritorio/results.csv");
                //mir_id, lncRNA transcript id, position of seed in transcript, dG duplex, dG binding, dG open, ddG
                o.write( ("miRNA ID,lncRNA ID,Position of Binding,Mínimum Free Energy,Accessibility Energy,Energy Region 3,Energy Region 5,Matches,Missmatches,AU,GC,GU,Nucloetids on Bulge,Cadena1,Cadena2,Cadena3,Cadena4,Cadena5\n").getBytes() );
                o.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        
        String temp, conc="";
        try{
            //Lee lineas de archivo miRNA y obtiene id y cadena
            while((temp = miRNA_buffer.readLine()) != null){
                    if ( !String.valueOf(temp.charAt(0)).equals(">") ){
                           
                            String to_add = conc + "LLLL" + temp;
                            miRNA.add(to_add);
                    }
                    else{
                            String[] parts = temp.split("\\s+");
                            conc = parts[0].substring(1);
                    }
            }
			System.out.println("miRNA readed!");
			System.out.println("total miRNAs: " + miRNA.size());
	    
                        
            //Lee archivo lncRNA y obtiene id y cadena
            while((temp = lnc_buffer.readLine()) != null ){
                    if ( !String.valueOf(temp.charAt(0)).equals(">") ){
                            String replacement = temp.replaceAll("T","U");
                            String to_add = conc + "LLLL" + replacement;
                            lncRNA.add(to_add);
                    }
                    else{
                            String[] parts2 = temp.split("\\|");
                            conc = parts2[0].substring(1);
                    }
            }
			System.out.println("lncRNA readed!");
			System.out.println("total lncRNAs: " + lncRNA.size());
			miRNA_buffer.close();
			lnc_buffer.close();
			
		}catch(Exception e){
			throw new RuntimeException("Error reading tuple",e);
		}finally{
                //Envía todos los lncRNA con cada miRNA        
			System.out.println("Sending data...");
			int k=0;
			for(int i = 0 ; i < miRNA.size() ; i++){
				for(int j = 0; j < lncRNA.size() ; j++){k++;
					//Formato: miRNA_id LLLL miRNA_code LLLL lncRNA_id LLLL lncRNA_code
					String str_to_send = miRNA.get(i) + "LLLL" + lncRNA.get(j);
					stack.push(str_to_send);
                
				}
			}	
		} 
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("streamSpout",new Fields("cadena"));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return this.conf;
	}

}
