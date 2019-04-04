package ru.softmg.workers.repository;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.softmg.workers.model.User;

@Data
@State(name = "WorkersUser", storages = { @Storage("workersUser.xml") })
public class CurrentUserComponent implements PersistentStateComponent<CurrentUserComponent.State> {
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

}
