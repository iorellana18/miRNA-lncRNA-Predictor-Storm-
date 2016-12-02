package inSite_features;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import EDA.BindingArea;
import EDA.EnergyStructure;
import EDA.RNAStructure;
import EDA.Stats;

public class MatchCounterBolt implements IRichBolt{

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
		EnergyStructure Energy = (EnergyStructure)tuple.getValueByField("Energy");
		String code = (String)tuple.getValueByField("Code");
		String sequence = (String)tuple.getValueByField("Sequence");
		
		 String[] split = code.split(" ");
	        String[] cleanCode = split[0].split("&");
	        String miRNA_code = cleanCode[0]+"|";
	        String lncRNA_code = new StringBuilder(cleanCode[1]).reverse().toString()+"|";
	        
	        split = sequence.split("&");
	        String miRNA_mre_Sequence = split[0];
	        String lncRNA_mre_Sequence = new StringBuilder(split[1]).reverse().toString();
	        //System.out.println(miRNA_mre_Sequence);
	        //System.out.println(lncRNA_mre_Sequence);
	        
	        
	        int i=0, j=0, k=0,match=0,miss=0, GU_match=0, GC_match=0, AU_match=0;
	        int miRNA_length=miRNA_code.length(), lncRNA_length=lncRNA_code.length();
	        int miRNA_bulge=0, lncRNA_bulge=0, nucleotidsOnBulge=0;
	        boolean newBulge=true;
	        StringBuilder builder = new StringBuilder();
	        StringBuilder builder2 = new StringBuilder();
	        StringBuilder meanChain = new StringBuilder();
	        
	        StringBuilder meanmiRNA = new StringBuilder();
	        StringBuilder meanlncRNA = new StringBuilder();
	        StringBuilder meanmiRNA_bulge = new StringBuilder();
	        StringBuilder meanlncRNA_bulge = new StringBuilder();

	        while(miRNA_code.charAt(k)!='|'){
	            
	           if(miRNA_code.charAt(k)==')'){
	               builder.append('|');
	               builder2.append('|');
	               break;
	            
	            }else{
	               builder.append(miRNA_code.charAt(k));
	               builder2.append(lncRNA_code.charAt(k));
	           }
	           k++;
	        }
	           
	        miRNA_code = builder.toString()+"|";
	        lncRNA_code = builder2.toString()+"|";
	        
	        
	        //System.out.println(sequence);
	        //System.out.println(code+"\n");
	        //System.out.println(miRNA_code);
	       
	        while(i<miRNA_length && j<lncRNA_length && miRNA_code.charAt(i)!='|' && lncRNA_code.charAt(j)!='|'){
	            
	            if(miRNA_code.charAt(i) == lncRNA_code.charAt(j) || miRNA_code.charAt(i)=='(' && lncRNA_code.charAt(j)== ')'){
	                newBulge=true;
	                if(miRNA_code.charAt(i)=='('){
	                    meanmiRNA.append(miRNA_mre_Sequence.charAt(i)); meanlncRNA.append(lncRNA_mre_Sequence.charAt(j));
	                    meanmiRNA_bulge.append(" "); meanlncRNA_bulge.append(" ");
	                    match++;
	                    switch(miRNA_mre_Sequence.charAt(i)){
	                        case 'A':{
	                            if(lncRNA_mre_Sequence.charAt(j) == 'U'){
	                                AU_match++; meanChain.append("|"); 
	                            }
	                            break;
	                        }
	                        case 'C':{
	                            if(lncRNA_mre_Sequence.charAt(j) == 'G'){
	                                GC_match++; meanChain.append("|"); 
	                            }
	                            break;
	                        }
	                        case 'G':{ 
	                           if(lncRNA_mre_Sequence.charAt(j) == 'C'){
	                                GC_match++; meanChain.append("|"); 
	                            }else if(lncRNA_mre_Sequence.charAt(j) == 'U'){
	                                GU_match++; meanChain.append(":"); 
	                            }
	                            break;
	                        }
	                        case 'U':{ 
	                            if(lncRNA_mre_Sequence.charAt(j) == 'G'){
	                                GU_match++; meanChain.append(":"); 
	                            }else if(lncRNA_mre_Sequence.charAt(j) == 'A'){
	                                AU_match++; meanChain.append("|"); 
	                            }
	                            break;
	                        }
	                        default:{
	                            System.out.println("Error, la cadena contiene caracter erroneo: "+miRNA_mre_Sequence.charAt(i));
	                            break;
	                        }
	                    }
	                }else{
	                    newBulge=true;
	                    meanChain.append(" ");meanmiRNA.append(" "); meanlncRNA.append(" ");
	                    meanmiRNA_bulge.append(miRNA_mre_Sequence.charAt(i));meanlncRNA_bulge.append(lncRNA_mre_Sequence.charAt(j));
	                    miss++;
	                }
	            }else{
	                if(miRNA_code.charAt(i)=='.'){
	                    j--;  nucleotidsOnBulge++;
	                    meanChain.append(" ");meanmiRNA.append(" "); meanlncRNA.append(" ");
	                    meanmiRNA_bulge.append(miRNA_mre_Sequence.charAt(i));meanlncRNA_bulge.append("-");
	                    if(newBulge){
	                        miRNA_bulge++;
	                        newBulge=false;
	                    }
	                    if(meanChain.charAt(i-1)=='x'){
	                       miRNA_bulge--;
	                    }
	                }else if(lncRNA_code.charAt(j)=='.'){
	                    i--; nucleotidsOnBulge++;
	                    meanChain.append(" ");meanmiRNA.append(" "); meanlncRNA.append(" ");
	                    ;meanmiRNA_bulge.append("-"); meanlncRNA_bulge.append(lncRNA_mre_Sequence.charAt(j));
	                    if(newBulge){
	                        lncRNA_bulge++;
	                        newBulge=false;
	                    }
	                    if(meanChain.charAt(i-1)=='x'){
	                       lncRNA_bulge--;
	                    }
	                }
	             
	            }
	            i++;
	            j++;
	        }
	        /*System.out.println("this: "+miRNA_code.charAt(i));
	        if(miRNA_code.charAt(i)=='|'){
	            while(j<lncRNA_length){
	                meanlncRNA.append(lncRNA.charAt(j));
	            }
	            j++;
	        }*/
	        //System.out.print(match+" "+miss);
	        //System.out.println("\n"+lncRNA_code+"\n");
	       /* System.out.println("G:U: "+GU_match);
	        System.out.println("A:U: "+AU_match);
	        System.out.println("C:G: "+GC_match);
	        System.out.println("Nucleotids on bulge "+nucleotidsOnBulge);
	        System.out.println("miRNA bulges: "+miRNA_bulge);
	        System.out.println("lncRNA bulges: "+lncRNA_bulge);
	        System.out.println("Nucleotids not matched: "+((miss)*2+nucleotidsOnBulge));*/
	    
	        String cadena1= meanmiRNA_bulge.toString();
	        String cadena2= meanmiRNA.toString();
	        String cadena3= meanChain.toString();
	        String cadena4= meanlncRNA.toString();
	        String cadena5=meanlncRNA_bulge.toString();
	        miss=miss*2+nucleotidsOnBulge;
	        
	        
	       Stats stats = new Stats(match,miss,GU_match,GC_match,AU_match);
	       BindingArea area = new BindingArea(cadena1,cadena2,cadena3,cadena4,cadena5);
	       
	       Values values = new Values(RNA,Energy,stats,area);
	       collector.emit(values);
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector collector) {
		this.collector=collector;
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("RNA","Energy","Stats","Area"));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
