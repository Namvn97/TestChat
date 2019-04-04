package manager;

import com.basesfs.ExtensionBaseClientRequestHandler;
import com.smartfoxserver.v2.entities.User;
import model.ChatTestModel;

import java.util.HashMap;
import java.util.Map;

public class ChatTestManager {
    private static ChatTestManager outChatTestManager;
    public String gid;
    public long id1;
    public long id2;
    public String idUserLeaveRoom;
    public String nameUserLeaveRoom;
    public String nameUserInRoom;
    public ChatTestModel chatTestModel;
    public ExtensionBaseClientRequestHandler handler;
    public static Map<String, ChatTestManager> hsmManager = new HashMap();

    public static ChatTestManager getInstance() {
        if (outChatTestManager == null) {
            outChatTestManager = new ChatTestManager();
        }
        return outChatTestManager;
    }

    public void setChatTestManager(long id1, long id2, ExtensionBaseClientRequestHandler handler) {
        this.id1 = id1;
        this.id2 = id2;
        this.handler = handler;
    }

    public void setgId() {
        String iD1 = String.valueOf(this.id1);
        String iD2 = String.valueOf(this.id2);
        if (id1 < id2) {
            this.gid = castString(iD1, iD2);
        } else if(id1 > id2) {
            this.gid = castString(iD2, iD1);
        }
        this.chatTestModel = ChatTestModel.copyFromDBtoObject(gid);
        hsmManager.put(gid, this);
    }

    public ChatTestManager getChatTestManager(String gId) {
        return hsmManager.get(gId);
    }

    public ChatTestModel getChatTestModelInManager() {
        return this.chatTestModel;
    }

    public User getUserLeaveRoom() {
        if (handler.getParentExtension().getParentZone().getUserByName(this.idUserLeaveRoom).isConnected()) {
            return handler.getParentExtension().getParentZone().getUserByName(this.idUserLeaveRoom);
        }
        return null;
    }

    public void deleleModelChat(){
        chatTestModel.deleteToDB(this.gid);
    }

    private String castString(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }


}
