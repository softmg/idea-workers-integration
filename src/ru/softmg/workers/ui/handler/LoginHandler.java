package ru.softmg.workers.ui.handler;

import ru.softmg.workers.model.User;

@FunctionalInterface
public interface LoginHandler {
    void loggedInHandler(User user);
}
