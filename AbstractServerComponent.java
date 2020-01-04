/**
 * 
 */
package cmet.ac.sockets.servers;

/**
 * @author sm22898
 *
 */
public abstract class AbstractServerComponent {

	public abstract void handleMessagesFromClient(String msg, ClientManager clientmgr);
	public abstract void sendMessageToClient(String msgc, ClientManager clientmgr);
	public abstract void sendNameToServer(String pName, ClientManager clientmgr);
	public abstract void sendAnswerToServer(String pAnswer, ClientManager clientmgr);
	public abstract void sendScoreToServer(String pScore, ClientManager clientmgr);

}
