package dev.microcontrollers.examplemod.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ExampleConfig {
    public static final ConfigClassHandler<ExampleConfig> CONFIG = ConfigClassHandler.createBuilder(ExampleConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("examplemod.json"))
                    .build())
            .build();

    @SerialEntry public boolean example = false;

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Component.translatable("examplemod.examplemod"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("examplemod.examplemod"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("examplemod.example"))
                                .description(OptionDescription.of(Component.translatable("examplemod.example.description")))
                                .binding(defaults.example, () -> config.example, newVal -> config.example = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .build())
        )).generateScreen(parent);
    }
}
