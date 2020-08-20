/*
 *  PwnFilter - Chat and user-input filter with the power of Regex
 *  Copyright (C) 2016 Pwn9.com / Sage905 <sage905@takeflight.ca>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.pwn9.filter.engine;

import com.pwn9.filter.bukkit.TemplateProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Object to hold the configuration of the PwnFilter Engine
 * <p>
 * Created by Sage905 on 15-09-10.
 */

public class FilterConfig {

    private final Logger logger;
    private volatile File textDir;
    private volatile File rulesDir;
    private Level logLevel;
    private TemplateProvider templateProvider;

    FilterConfig(Logger logger) {
        this.logger = logger;
    }

    /* Getters and Setters */

    public File getTextDir() {
        return textDir;
    }

    public void setTextDir(File textDir) {
        this.textDir = textDir;
    }

    public File getRulesDir() {
        return rulesDir;
    }

    public void setRulesDir(File rulesDir) {
        this.rulesDir = rulesDir;
    }

    public TemplateProvider getTemplateProvider() {
        return templateProvider;
    }

    public void setTemplateProvider(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    public Logger getLogger() {
        return logger;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public File getRuleFile(String path) {
        // TODO: This can most certainly be cleaned up.
        File ruleFile;
        if (path.startsWith("/")) {
            ruleFile = new File(path);
        } else {
            ruleFile = new File(getRulesDir(), path);
        }
        if (ruleFile.exists()) {
            return ruleFile;
        } else {
            try {
                if (copyTemplate(ruleFile)) {
                    return ruleFile;
                }
            } catch (IOException | SecurityException ex) {
                return null; // Failed to create file or copy template.
            }
            return null;
        }
    }

    private boolean copyTemplate(File destFile) throws IOException, SecurityException {

        if (destFile.exists() || templateProvider == null) {
            return false;
        }

        String configName = destFile.getName();

        InputStream templateFile;

        templateFile = templateProvider.getResource(configName);

        if (templateFile == null) {
            // Create an empty file.
            return destFile.mkdirs() && destFile.createNewFile();
        }
        if (destFile.createNewFile()) {
            BufferedInputStream fin = new BufferedInputStream(templateFile);
            FileOutputStream fout = new FileOutputStream(destFile);
            byte[] data = new byte[1024];
            int c;
            while ((c = fin.read(data, 0, 1024)) != -1)
                fout.write(data, 0, c);
            fin.close();
            fout.close();
            logger.info("Created file from template: " + configName);
            return true;
        } else {
            logger.warning("Failed to create file from template: " + configName);
            return false;
        }
    }

}
