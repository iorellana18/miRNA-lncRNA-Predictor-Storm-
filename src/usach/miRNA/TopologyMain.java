package usach.miRNA;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

import common_features.AccessibilityBolt;
import common_features.EnergyBolt;
import common_features.SeedMatchBolt;
import common_features.resultsBolt;
import inSite_features.EnergyOfAnotherRegionsBolt;
import inSite_features.MatchCounterBolt;

public class TopologyMain {
	private static final String TOPOLOGY_NAME = "miRNA";
	public static void main(String[] args) throws Exception {
		// Create Config instance for cluster configuration
		Config config = new Config();
		config.setDebug(true);

		//
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("RNASpout", new RNASpout(), 1);

		builder.setBolt("SeedMatch", new SeedMatchBolt(), 1).shuffleGrouping("RNASpout");

		builder.setBolt("EnergyBolt", new EnergyBolt(), 1).shuffleGrouping("SeedMatch");

		builder.setBolt("Accessibility", new AccessibilityBolt(), 1).shuffleGrouping("EnergyBolt");
		
		builder.setBolt("EOAR", new EnergyOfAnotherRegionsBolt(),1).shuffleGrouping("Accessibility");
		
		builder.setBolt("Counter",new MatchCounterBolt(),1).shuffleGrouping("EOAR");

		builder.setBolt("Results", new resultsBolt(), 1).shuffleGrouping("Counter");

		if (args != null && args.length > 0) {
			try {
				StormSubmitter.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
			Utils.sleep(100000);
			cluster.shutdown();
		}
	}
}