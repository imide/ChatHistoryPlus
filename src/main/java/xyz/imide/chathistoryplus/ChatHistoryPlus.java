package xyz.imide.chathistoryplus;

import xyz.imide.chathistoryplus.config.ChatHistoryPlusConfig;

//? if fabric
import net.fabricmc.api.ModInitializer;
//? if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
*///?}


//? if neoforge
/*@Mod(value = "chathistoryplus", dist = Dist.CLIENT)*/
public class ChatHistoryPlus /*? if fabric {*/ implements ModInitializer /*?}*/ {
    //? if fabric {
    @Override
    public void onInitialize() {
        ChatHistoryPlusConfig.CONFIG.load();
        ChatHistoryPlusConfig.INSTANCE = ChatHistoryPlusConfig.CONFIG.instance();
    }
    //?}

    //? if neoforge {
    /*public ChatHistoryPlus() {
        ChatHistoryPlusConfig.CONFIG.load();
        ChatHistoryPlusConfig.INSTANCE = ChatHistoryPlusConfig.CONFIG.instance();
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> ChatHistoryPlusConfig.configScreen(parent));
    }
    *///?}
}
