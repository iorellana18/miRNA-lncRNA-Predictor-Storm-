package usach.miRNA;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

public class TopologyMain {
	private static final String TOPOLOGY_NAME = "miRNA";
	public static void main(String[] args) throws Exception {
		// Create Config instance for cluster configuration
		Config config = new Config();
		config.setDebug(true);
		config.setNumWorkers(1);

		//
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("RNASpout", new RNASpout(), 1);

		builder.setBolt("SeedMatch", new SeedMatchBolt(), 1).shuffleGrouping("RNASpout","streamSpout");

	//	builder.setBolt("EnergyBolt", new EnergyBolt()).shuffleGrouping("SeedMatch");

	//	builder.setBolt("Accessibility", new AccessibilityBolt()).shuffleGrouping("EnergyBolt");

	//	builder.setBolt("Results", new resultsBolt()).fieldsGrouping("Accessibility", new Fields("call"));

		if (args != null && args.length > 0) {
			try {
				StormSubmitter.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
			Utils.sleep(20000);
			cluster.shutdown();
}
	}
}