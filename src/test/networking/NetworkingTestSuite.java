package test.networking;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	BitArrayTest.class,
	ClientServerAiTest.class,
	ClientServerInfoTest.class,
	ClientServerLobbyRoomTest.class,
	ClientServerPlayerTest.class,
	ClientServerRoomTest.class,
	PacketEncodeDecodeTest.class,
//	PacketEncodeDecodeTest2.class,
	PacketHistoryEntryTest.class,
	ServerAITest.class,
	ServerClientInfoTest.class,
	ServerClientTableTest.class
})


public class NetworkingTestSuite {

}
