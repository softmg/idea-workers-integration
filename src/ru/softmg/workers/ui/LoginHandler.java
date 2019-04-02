package ru.softmg.workers.ui;

import ru.softmg.workers.model.User;

@FunctionalInterface
public interface LoginHandler {
    void loggedInHandler(User user);
}
