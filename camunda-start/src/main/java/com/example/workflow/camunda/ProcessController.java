package com.example.workflow.camunda;


import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/process")
public class ProcessController {

    private final ProcessStarter processStarter;

    public ProcessController(ProcessStarter processStarter) {
        this.processStarter = processStarter;
    }

    @GetMapping(value = "/start/{processDefinitionKey}")
    public String startProcess(@PathVariable("processDefinitionKey") String processDefinitionKey) {
        String processInstanceId = processStarter.startProcess(processDefinitionKey);
        return "Process started with id : " + processInstanceId;
    }

    @PostMapping("/doAction")
    public String approveRequest(@RequestBody Map action) {
        Boolean approved = (Boolean) action.get("approved");
        processStarter.takeAction((String) action.get("processInstanceId"), approved);
        return approved ? "Request approved!" : "Request rejected!";
    }


    @GetMapping("/tasks")
    public List<String> getTasks(@RequestParam String assignee) {
        return processStarter.getInboxByAssignee(assignee);
    }
}
