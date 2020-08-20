package com.pwn9.filter;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.UUID;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 20/08/2020.
 */
public class MockAudienceProvider implements AudienceProvider {
    private final Audience internal = new Audience() {
        @Override
        public void sendMessage(Component message) {
            System.out.println(message.toString());
        }
    };
    @Override
    public @NotNull Audience all() {
        return internal;
    }

    @Override
    public @NotNull Audience console() {
        return internal;
    }

    @Override
    public @NotNull Audience players() {
        return internal;
    }

    @Override
    public @NotNull Audience player(@NotNull UUID playerId) {
        return internal;
    }

    @Override
    public @NotNull Audience permission(@NotNull String permission) {
        return internal;
    }

    @Override
    public @NotNull Audience world(@NotNull Key world) {
        return internal;
    }

    @Override
    public @NotNull Audience server(@NotNull String serverName) {
        return internal;
    }

    @Override
    public @NotNull ComponentRenderer<Locale> localeRenderer() {
        return (component, context) -> component;
    }

    @Override
    public @NotNull GsonComponentSerializer gsonSerializer() {
        return GsonComponentSerializer.gson();
    }

    @Override
    public void close() {
            //not required
    }
}
