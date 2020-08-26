package com.pwn9.filter.engine;

import com.pwn9.filter.TemplateProvider;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 22/08/2020.
 */
public interface FilterConfig {
    File getTextDir();
    void setTextDir(File textDir);
    File getRulesDir();
    void setRulesDir(File rulesDir);
    TemplateProvider getTemplateProvider();
    void setTemplateProvider(TemplateProvider templateProvider);
    Logger getLogger();
    Level getLogLevel();
    void setLogLevel(Level logLevel);
    File getRuleFile(String path);


}
