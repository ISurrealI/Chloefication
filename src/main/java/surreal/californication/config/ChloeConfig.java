package surreal.californication.config;

import net.minecraftforge.common.config.Config;
import surreal.californication.ChloeValues;

@Config(modid = ChloeValues.MODID)
public class ChloeConfig {
    public static BasicOptions BASIC = new BasicOptions();

    public static class BasicOptions {
        @Config.Comment("Enables debugh mode")
        public boolean debughMode = false;
    }
}
