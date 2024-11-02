package xyz.imide.chathistoryplus.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.imide.chathistoryplus.config.ChatHistoryPlusConfig;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @ModifyExpressionValue(
            method = {"addMessageToDisplayQueue", "addMessageToQueue", "addRecentChat"},
            at = @At(value = "CONSTANT", args = "intValue=100")
    )
    public int changeChatHistorySize(int value) {
        if (ChatHistoryPlusConfig.INSTANCE != null && ChatHistoryPlusConfig.INSTANCE.enabled) {
            return ChatHistoryPlusConfig.INSTANCE.chatHistorySize;
        }
        return 100;
    }
}
