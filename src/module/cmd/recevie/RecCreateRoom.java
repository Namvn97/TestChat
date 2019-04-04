package module.cmd.recevie;

import com.basesfs.data.BaseCmd;
import com.forward.manager.UserManager;
import com.forward.model.UserModel;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecCreateRoom extends BaseCmd {
    public String nameUser1;
    public String nameUser2;
    public long idUser1;
    public long idUser2;

    public RecCreateRoom(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        int idUserSelect = data.getInt("nameUser2");
        getUser2ForModel(idUserSelect);
    }

    public void getUser1ForModel(String user) {
        UserModel um = UserManager.getInstance().getUserModel(user);
        this.nameUser1 = um.displayName;
        this.idUser1 = um.id;
    }

    public void getUser2ForModel(int user) {
        UserModel um = UserManager.getInstance().getUserModel(user);
        this.nameUser2 = um.displayName;
        this.idUser2 = um.id;
    }
}
