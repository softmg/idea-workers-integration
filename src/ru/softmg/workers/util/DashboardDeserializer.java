package ru.softmg.workers.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.softmg.workers.model.Dashboard;
import ru.softmg.workers.model.DashboardGroup;
import ru.softmg.workers.model.Task;
import ru.softmg.workers.model.Worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardDeserializer extends StdDeserializer<Dashboard> {
    public DashboardDeserializer() {
        super(Dashboard.class);
    }

    @Override
    public Dashboard deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Dashboard dashboard = new Dashboard();
        HashMap<String, DashboardGroup> groupMap = new HashMap<>();

        node.elements().forEachRemaining(jsonNode -> {
            jsonNode.fieldNames().forEachRemaining(id -> {
                JsonNode group = jsonNode.get(id);
                JsonNode worker = group.get("worker");
                JsonNode tasks = group.get("tasks");

                DashboardGroup dashboardGroup = new DashboardGroup();
                dashboardGroup.setWorker(new Worker(worker.get("id").asText(), worker.get("name").asText()));
                List<Task> taskList = new ArrayList<>();

                tasks.elements().forEachRemaining(taskElement -> {
                    Task t = new Task(
                            Integer.parseInt(taskElement.get("id").asText()),
                            taskElement.get("name").asText()
                    );
                    taskList.add(t);
                });
                dashboardGroup.setTasks(taskList);
                groupMap.put(id, dashboardGroup);
            });
        });

        dashboard.setTasks(groupMap);
        return dashboard;
    }
}
