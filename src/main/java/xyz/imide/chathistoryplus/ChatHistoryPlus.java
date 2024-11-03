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
package xyz.imide.chathistoryplus;

import xyz.imide.chathistoryplus.config.ChatHistoryPlusConfig;

//? if fabric
import net.fabricmc.api.ModInitializer;
//? if neoforge {
/*
import net.neoforged.api.distmarker.Dist;
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
