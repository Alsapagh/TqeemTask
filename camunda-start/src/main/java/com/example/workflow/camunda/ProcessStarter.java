package com.example.workflow.camunda;

import jakarta.annotation.PostConstruct;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProcessStarter {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;

    /**
     * get tasks for each role
     * @param assignee
     * @return
     */
    public List<String> getInboxByAssignee(String assignee) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).active().list();
        return tasks.stream()
                .map(task -> "Task ID: " + task.getId() + ", Name: " + task.getName())
                .collect(Collectors.toList());
    }

    /**
     * take action and move ticket to next step
     * @param processInstanceId
     * @param approved
     */
    public void takeAction(String processInstanceId, boolean approved) {
        // Check if the process instance exists and is active
        long count = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .active()
                .count();

        if (count == 0) {
            throw new RuntimeException("Process instance with ID " + processInstanceId + " does not exist or has already ended.");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approved);
        runtimeService.setVariables(processInstanceId, variables);

        // Trigger the waiting task or event
        runtimeService.createMessageCorrelation("ActionTaken")
                .processInstanceId(processInstanceId)
                .setVariable("approved", approved)
                .correlate();
    }

    /**
     * start and instance to create a new request
     * @param processDefinitionKey
     * @return
     */
    public String startProcess(String processDefinitionKey) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", true); // Initializing 'approved' as null
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);

        return processInstance.getProcessInstanceId();
    }

    /**
     * this will deploy this diagram
     */
    @PostConstruct
    public void deployProcess() {
        repositoryService.createDeployment()
                .addClasspathResource("stc-emp-requests.bpmn")
                .deploy();
    }

}
