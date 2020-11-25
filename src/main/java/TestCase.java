import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.integration.hazelcast.leader.LeaderInitiator;
import org.springframework.integration.leader.DefaultCandidate;

import java.io.IOException;

public class TestCase {
    public static void main(String[] args) throws InterruptedException, IOException {
        Config config = new Config();

        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().addMember("localhost").setEnabled(true);

        HazelcastInstance ins1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance ins2 = Hazelcast.newHazelcastInstance(config);

        LeaderInitiator l1 = new LeaderInitiator(ins1, new DefaultCandidate("1", "master"));
        LeaderInitiator l2 = new LeaderInitiator(ins2, new DefaultCandidate("2", "master"));

        l1.start();
        Thread.sleep(500);
        l2.start();
        Thread.sleep(500);

        System.out.println("Node1 yield");
        l1.getContext().yield();
        Thread.sleep(1000);

        System.out.println("Node2 yield");
        l2.getContext().yield();

        System.in.read();

        ins1.shutdown();
        ins2.shutdown();
    }
}
