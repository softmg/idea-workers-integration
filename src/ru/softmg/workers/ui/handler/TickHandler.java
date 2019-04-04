package ru.softmg.workers.ui.handler;

@FunctionalInterface
public interface TickHandler {
    void tick(Integer tick);
}
