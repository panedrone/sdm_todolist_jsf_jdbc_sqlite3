package com.sqldalmaker.todolist.service;

import com.sqldalmaker.DataStoreManager;
import com.sqldalmaker.todolist.model.dao.GroupsDao;
import com.sqldalmaker.todolist.model.dao.TasksDao;
import com.sqldalmaker.todolist.model.dto.*;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: panedrone Date: 18/04/2012 Time: 22:44
 */
public class TodoListService {

    private DataStoreManager dm;

    public DataStoreManager getDatastoreManager() {
        return dm;
    }

    public void setDatastoreManager(DataStoreManager datastoreManager) {
        this.dm = datastoreManager;
        g_dao = dm.createGroupsDao();
        t_dao = dm.createTasksDao();
    }

    private GroupsDao g_dao;
    private TasksDao t_dao;

    public List<Group> getGroups() throws Exception {
        return g_dao.getGroups();
    }

    public void createGroup(Group gr) throws Exception {
        dm.begin();
        try {
            int res = g_dao.createGroup(gr);
            if (res == 0) {
                throw new Exception("Cannot create Project");
            }
            dm.commit();
        } catch (Exception e) {
            dm.rollback();
            throw new Exception(e);
        }
    }

    public void updateGroup(Group project) throws Exception {
        dm.begin();
        try {
            int res = g_dao.updateGroup(project);
            if (res == 0) {
                throw new Exception("Cannot update Project: " + project.getGId());
            }
            dm.commit();
        } catch (Exception e) {
            dm.rollback();
            throw new Exception(e);
        }
    }

    public void deleteGroup(Group project) throws Exception {
        dm.begin();
        try {
            int res = g_dao.deleteGroup(project.getGId());
            if (res == 0) {
                throw new Exception("Cannot delete Project: " + project.getGId());
            }
            dm.commit();
        } catch (Exception e) {
            dm.rollback();
            throw new Exception(e);
        }
    }

    public List<Task> getGroupTasks(Group group) throws Exception {
        return t_dao.getGroupTasks(group.getGId());
    }

    public void updateTask(Task task) throws Exception {
        dm.begin();
        try {
            int res = t_dao.updateTask(task);
            if (res == 0) {
                throw new Exception("Cannot update task, t_id: " + task.getTId());
            }
            dm.commit();
        } catch (Exception e) {
            dm.rollback();
            throw new Exception(e);
        }
    }

    public void deleteTask(Task task) throws Exception {
        dm.begin();
        try {
            int res = t_dao.deleteTask(task.getTId());
            if (res == 0) {
                throw new Exception("Cannot delete task, t_id: " + task.getTId());
            }
            dm.commit();
        } catch (Exception e) {
            dm.rollback();
            throw new Exception(e);
        }
    }

    public void createTask(Task task) throws Exception {
        dm.begin();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = format.format(new Date());
            task.setTDate(dateString); // today
            int res = t_dao.createTask(task);
            if (res == 0) {
                throw new Exception("Cannot create task");
            }
            dm.commit();
        } catch (Exception e) {
            dm.rollback();
            throw new Exception(e);
        }
    }
}
