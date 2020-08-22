package com.pwn9.filter.engine;

import com.pwn9.filter.engine.api.AuthorService;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.MessageAuthor;
import com.pwn9.filter.engine.api.NotifyTarget;
import com.pwn9.filter.engine.rules.action.ActionFactory;
import com.pwn9.filter.engine.rules.chain.InvalidChainException;
import com.pwn9.filter.engine.rules.chain.RuleChain;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 22/08/2020.
 */
public interface FilterService {

    Logger getLogger();
    void setDebugMode(String s);
    FilterConfig getConfig();
    void registerAuthorService(AuthorService authorService);
    void deregisterAuthorService(AuthorService authorService);
    void clearAuthorServices();
    List<AuthorService> getAuthorServices();
    MessageAuthor getAuthor(UUID uuid);
    RuleChain parseRules(File ruleFile) throws InvalidChainException;
    ActionFactory getActionFactory();
    void clearLogFileHandler();
    void setLogFileHandler(File logFile);

    void notifyTargets(String perm, String message);
    void registerNotifyTarget(NotifyTarget t);
    boolean unregisterClient(FilterClient f);
    void registerClient(FilterClient f);
    void disableClients();
    void enableClients();
    void shutdown();
    Set<FilterClient> getActiveClients();
    PointManager getPointManager();
}
