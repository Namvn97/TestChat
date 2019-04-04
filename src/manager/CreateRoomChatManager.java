package manager;

import com.basesfs.utils.extension.ExtensionUtility;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import module.UserHandler;

public class CreateRoomChatManager {
//    private static CreateRoomChatManager outCreateRoomChatManager;
    public User user1;
    public User user2;
    public long idUser1;
    public long idUser2;
    public Room room;
    public String nameRoom;
    public Boolean isOldRoom;
    public UserHandler userHandler;

    public CreateRoomChatManager(User user1, User user2, long idUser1, long idUser2, UserHandler userHandler) {
        this.user1 = user1;
        this.user2 = user2;
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.userHandler = userHandler;
    }

    public void checkRoom() {
        this.room = userHandler.getParentExtension().getParentZone().getRoomManager().getRoomByName(this.nameRoom);
        if (this.room == null) {
            isOldRoom = false;
            createRoomChatPrivate();
        } else {
            isOldRoom = true;
            addUsertoPrivateChat(this.user1);
        }
    }

    private void createRoomChatPrivate() {
        CreateRoomSettings settings = new CreateRoomSettings();
        settings.setName(this.nameRoom);
        settings.setMaxUsers(2);
        settings.setDynamic(true);
        settings.setGame(false);
        settings.setAutoRemoveMode(SFSRoomRemoveMode.WHEN_EMPTY);
        try {
            ExtensionUtility.getInstance().createRoom(userHandler.getParentExtension().getParentZone()
                    , settings, this.user1, true, null);
            addUsertoPrivateChat(this.user2);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }
    }

    private void addUsertoPrivateChat(User u) {
        Room roomPrivate = userHandler.getParentExtension().getParentZone().getRoomByName(this.nameRoom);
        try {
            ExtensionUtility.getInstance().getApi().joinRoom(u, roomPrivate, null, false, null);
        } catch (SFSJoinRoomException e) {
            e.printStackTrace();
        }
    }

    public void setNameRoom() {
        String id1 = String.valueOf(idUser1).substring(3);
        String id2 = String.valueOf(idUser2).substring(3);
        String room = "R";
        if (idUser1 < idUser2) {
            this.nameRoom = castString(room, id1, id2);
        } else {
            this.nameRoom = castString(room, id2, id1);
        }
        checkRoom();
    }

    private String castString(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }

}
