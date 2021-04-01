package com.sqldalmaker.todolist.beans;

import com.sqldalmaker.todolist.model.dto.*;
import com.sqldalmaker.todolist.service.TodoListService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: panedrone
 * Date: 18/04/12
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */
public class TodoListBean {

    private TodoListService service;

    public TodoListService getService() {
        return service;
    }

    public void setService(TodoListService service) {
        this.service = service;
    }

    public List<Group> getGroups() throws Exception {
        return service.getGroups();
    }

    private Group newGroup;

    public Group getNewGroup() {
        if (newGroup == null) {
            newGroup = new Group();
        }
        return newGroup;
    }


    public void createNew() throws Exception {
        service.createGroup(newGroup);
    }

    /////////////////////////////

    private Group selectedGroup;

    // http://www.primefaces.org/showcase/ui/datatableRowSelectionByColumn.jsf

    public Group getSelectedGroup() {
        if (selectedGroup == null) {
            selectedGroup = new Group(); //
        }
        return selectedGroup;
    }

    public void setSelectedGroup(Group selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public void updateSelected() throws Exception {
        if (selectedGroup != null && selectedGroup.getGId() != 0) {
            service.updateGroup(selectedGroup);
        }
    }

    public void deleteSelected() throws Exception {
        if (selectedGroup != null && selectedGroup.getGId() != 0) {
            service.deleteGroup(selectedGroup);
            clickedGroup = null;
            selectedGroup = null;
        }
    }

    /////////////////////////////

    private Group clickedGroup;

    public Group getClickedGroup() {
        if (clickedGroup == null) {
            clickedGroup = new Group(); //
        }
        return clickedGroup;
    }

    public void setClickedGroup(Group clickedGroup) {
        this.clickedGroup = clickedGroup;
    }

    /////////////////////////////

    public List<Task> getTasks() throws Exception {
        if (clickedGroup != null) {
            return service.getGroupTasks(clickedGroup);
        }
        return new ArrayList<Task>();
    }

    private Task newTask;

    public Task getNewTask() {
        if (newTask == null) {
            newTask = new Task();
        }
        return newTask;
    }


    public void createTask() throws Exception {
        newTask.setGId(clickedGroup.getGId());
        service.createTask(newTask);
    }

    private Task selectedTask;

    public Task getSelectedTask() {
        if (selectedTask == null) {
            selectedTask = new Task();
        }
        return selectedTask;
    }

    public void setSelectedTask(Task selectedTask) {
        this.selectedTask = selectedTask;
    }

    public void updateSelectedTask() throws Exception {
        if (selectedTask != null && selectedTask.getGId() != 0) {
            service.updateTask(selectedTask);
        }
    }

    public void deleteSelectedTask() throws Exception {
        if (selectedTask != null && selectedTask.getGId() != 0) {
            service.deleteTask(selectedTask);
            selectedTask = null;
        }
    }
}
