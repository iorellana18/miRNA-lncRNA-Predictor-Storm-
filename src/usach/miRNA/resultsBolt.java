package usach.miRNA;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class resultsBolt implements IRichBolt{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	
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
	   
	    String miRNA_id = tuple.getString(0);
	    String lncRNA_id = tuple.getString(2);
	    String position = tuple.getInteger(5).toString();
	    float dg_duplex = tuple.getFloat(6);
	    float dg_Open = tuple.getFloat(7);
	     
        float result_ddg = dg_duplex -dg_Open; 
        String result = Float.toString(precision(2,result_ddg));//DG duplex(minimum free energy) - DG open
        String strDuplex = Float.toString(dg_duplex);
        
        //Generate CSV outfile
        FileOutputStream o;
        if (result_ddg < 0) {
            try{
                o = new FileOutputStream("/home/ian/Escritorio/results.csv",true);
                //mir_id, lncRNA transcript id, position of seed in transcript, dG duplex, dG binding, dG open, ddG
                o.write( (miRNA_id+","+lncRNA_id+","+position+","+strDuplex+","+result+"\n").getBytes() );
                o.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        collector.ack(tuple);
		
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector collector) {
		this.collector = collector;
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("call"));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
