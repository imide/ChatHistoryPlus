/*
 * This file is a part of ChatHistoryPlus, licensed under the GNU Affero General Public License version 3.
 *
 * Copyright (C) 2024 imide
 *
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  You can contact me at imide (at) imide (dot) xyz.
 */
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
