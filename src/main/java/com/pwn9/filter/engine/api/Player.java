package com.pwn9.filter.engine.api;

/**
 * A player is a in game person that can be considered to be really present - ie not on console.
 * @author Narimm
 * on 19/08/2020.
 */
public interface Player extends MessageAuthor, CommandSender{

    /**
     * A place is a string that is considered a player general location, it might be a server or a world
     * @return String
     */
    String getPlace();
}
