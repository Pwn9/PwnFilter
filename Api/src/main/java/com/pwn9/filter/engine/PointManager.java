package com.pwn9.filter.engine;

import com.pwn9.filter.engine.api.Action;
import com.pwn9.filter.engine.api.FilterClient;
import com.pwn9.filter.engine.api.MessageAuthor;

import java.util.List;
import java.util.UUID;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 22/08/2020.
 */
public interface PointManager extends FilterClient {
    void reset();
    int getLeakInterval();
    void setLeakInterval(int leakInterval);
    Double getLeakPoints();
    void setLeakPoints(Double leakPoints);
    Double getPoints(MessageAuthor author);
    void addPoints(UUID id, Double points);
    void addThreshold(String name, Double points, List<Action> ascending, List<Action> descending);
}
