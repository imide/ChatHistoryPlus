package xyz.imide.chathistoryplus.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import lombok.Getter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ChatHistoryPlusConfig {
    public static final ConfigClassHandler<ChatHistoryPlusConfig> CONFIG = ConfigClassHandler.createBuilder(ChatHistoryPlusConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("chathistoryplus.json"))
                    .build())
            .build();

    @Getter
    public static ChatHistoryPlusConfig INSTANCE;

    @SerialEntry
    public boolean enabled = true;

    @SerialEntry
    public int chatHistorySize = 100;

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, (defaults, config, builder) -> builder
                .title(Component.translatable("chathistoryplus.config.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("chathistoryplus.config.category.general"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("chathistoryplus.config.enabled"))
                                .description(OptionDescription.of(Component.translatable("chathistoryplus.config.enabled.description")))
                                .binding(defaults.enabled, () -> config.enabled, newVal -> config.enabled = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("chathistoryplus.config.chatHistorySize"))
                                .description(OptionDescription.of(Component.translatable("chathistoryplus.config.chatHistorySize.description")))
                                .binding(defaults.chatHistorySize, () -> config.chatHistorySize, newVal -> config.chatHistorySize = newVal)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                                        .min(10)
                                        .max(100000)
                                )
                                .build())
                        .build())).generateScreen(parent);
    }
}
