package client;

import bootcamp.TokenIssueFlowInitiator;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.utilities.NetworkHostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Connects to a Corda node via RPC and performs RPC operations on the node.
 *
 * The RPC connection is configured using command line arguments.
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse("localhost:10004");
        String username = "user1"; // args[1];
        String password = "test"; // args[2];

        final CordaRPCClient client = new CordaRPCClient(nodeAddress);
        final CordaRPCConnection connection = client.start(username, password);
        final CordaRPCOps proxy = connection.getProxy();

        logger.info(proxy.currentNodeTime().toString());
        Set<Party> parties = proxy.partiesFromName("PartyB", false);
        proxy.startFlowDynamic(TokenIssueFlowInitiator.class, proxy.partiesFromName("PartyB", false).stream().findFirst().get(), 786);

        NodeInfo nodeInfo = proxy.nodeInfo();

        connection.notifyServerAndClose();
    }
}