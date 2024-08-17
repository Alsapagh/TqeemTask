package com.example.workflow.component;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Created by Ayman Alsapagh
 * Date :   8/17/2024
 */
@Component
public class ApprovalService implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {

        // Retrieve the approval status from process variables
        Boolean approved = (Boolean) delegateExecution.getVariable("approved");
        // Perform actions based on approval status
        if (approved != null) {
            if (approved) {
                // Handle the case where the request is approved
                addApprovalRecord(delegateExecution.getProcessInstance());
                System.out.println("Request has been approved.");
            } else {
                // Handle the case where the request is rejected
                System.out.println("Request has been rejected.");
            }
        } else {
            // Handle the case where approval is not yet decided
            addRejectRecord(delegateExecution.getProcessInstance());

            System.out.println("Approval status is not yet decided.");
        }

//         You can set additional process variables or perform actions as needed
        logAndMove(delegateExecution);
    }

    private void addRejectRecord(DelegateExecution processInstance) {
    }

    private void addApprovalRecord(DelegateExecution processInstance) {
        //do some log into db and take any actions
        //
    }

    void logAndMove(DelegateExecution delegateExecution) {
        delegateExecution.setVariable("extraVariable", new Object());
        delegateExecution.setVariable("approved", null);//to stop at next step

    }
}
