package com.pwn9.filter.engine.api;

import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 22/08/2020.
 */
public interface Console extends MessageAuthor {

    void sendBroadcast(final List<String> preparedMessages);

    void sendBroadcast(final String message);

    void executeCommand(final String command);
}
