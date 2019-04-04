package manager;

import com.basesfs.utils.extension.ExtensionUtility;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import module.UserHandler;

public class LeaveRoomManager {
    public User user1;
    public User user2;
    public Room room;
    public String nameRoom;
    public UserHandler userHandler;

    public LeaveRoomManager(User user1, String nameRoom, UserHandler userHandler) {
        this.user1 = user1;
        this.nameRoom = nameRoom;
        this.userHandler = userHandler;
    }

    public void userLeaveRoom() {
        this.room = userHandler.getParentExtension().getParentZone().getRoomByName(nameRoom);
        ExtensionUtility.getInstance().leaveRoom(this.user1, this.room);
    }

    public User getUser2() {
        this.user2 = this.room.getUserList().get(0);
        return this.user2;
    }
}
