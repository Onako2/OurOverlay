package rs.onako2.ouroverlay;

import com.cinemamod.mcef.MCEF;
import net.fabricmc.api.ClientModInitializer;

public class OuroverlayClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (!MCEF.isInitialized()) {
            MCEF.initialize();
        }
    }
}
