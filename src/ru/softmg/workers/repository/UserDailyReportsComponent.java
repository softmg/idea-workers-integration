package ru.softmg.workers.repository;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.softmg.workers.model.Report;

import java.util.ArrayList;
import java.util.List;

@Data
@State(name = "UserReports", storages = { @Storage("userReports.xml") })
public class UserDailyReportsComponent implements PersistentStateComponent<UserDailyReportsComponent.State> {
    @Data
    public static class State {
        private List<Report> reports = new ArrayList<>();
    }

    private State state = new State();

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @Nullable
    @Override
    public UserDailyReportsComponent.State getState() {
        return state;
    }
}
