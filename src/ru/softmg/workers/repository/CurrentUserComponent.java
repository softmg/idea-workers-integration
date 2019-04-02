package ru.softmg.workers.repository;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.softmg.workers.model.User;

@Data
@State(name = "WorkersUser", storages = { @Storage("workersUser.xml") })
public class CurrentUserComponent implements ApplicationComponent, PersistentStateComponent<CurrentUserComponent.State> {
    private ApplicationContext applicationContext;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class State {
        private User currentUser;
    }

    private State currentState = new State();

    @Override
    public void loadState(@NotNull State state) {
        this.currentState = state;
    }

    @Nullable
    @Override
    public State getState() {
        return currentState;
    }

    @Override
    public void initComponent() {
        // applicationContext = new ClassPathXmlApplicationContext("classpath:spring/application-context.xml");
        // ((ClassPathXmlApplicationContext) applicationContext).refresh();
    }
}
