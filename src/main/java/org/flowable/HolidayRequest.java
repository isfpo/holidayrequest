package org.flowable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;

public class HolidayRequest {

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(HolidayRequest.class);

    public static void main(String[] args) {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
                .setJdbcUsername("sa").setJdbcPassword("")
                .setJdbcDriver("org.h2.Driver").setDatabaseSchemaUpdate(
                        AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = cfg.buildProcessEngine();
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("holiday-request.bpmn20.xml").deploy();
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery().deploymentId(deployment.getId())
                .singleResult();
        logger.info(
                "Found process definition : " + processDefinition.getName());

        String employee = "moi";

        Integer nrOfHolidays = 3;

        String description = "turlututu";

        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employee", employee);
        variables.put("nrOfHolidays", nrOfHolidays);
        variables.put("description", description);
        runtimeService.startProcessInstanceByKey("holidayRequest", variables);

        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().list();
        System.out.println("You have " + tasks.size() + " tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            logger.info(i + 1 + ") " + tasks.get(i).getName());
        }

        int taskIndex = 1;
        Task task = tasks.get(taskIndex - 1);
        Map<String, Object> processVariables = taskService
                .getVariables(task.getId());
        System.out.println(processVariables.get("employee") + " wants "
                + processVariables.get("nrOfHolidays")
                + " of holidays. Do you approve this?");

        boolean approved = true;
        variables = new HashMap<String, Object>();
        variables.put("approved", approved);
        taskService.complete(task.getId(), variables);

    }

}
