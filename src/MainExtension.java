import com.basesfs.BaseExtension;
import com.basesfs.datacontroller.DataController;
import com.forward.config.DynamicConfigServer;
import module.UserHandler;

public class MainExtension extends BaseExtension {
    @Override
    public void init() {
        initDB();
        super.init();
        initConfig();
    }

    private void initDB() {
        //Ket noi den database
        try {
            DataController.getController();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initConfig() {
        DynamicConfigServer.instance();
    }

    @Override
    public void initModule() {
        //dang ky lang nghe nghe cac su kien client gui len
        new UserHandler(this);
    }
}
