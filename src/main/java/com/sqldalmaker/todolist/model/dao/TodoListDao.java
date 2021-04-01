package com.sqldalmaker.todolist.model.dao;

import com.sqldalmaker.DataStore;

import com.sqldalmaker.todolist.model.dto.Group;
import com.sqldalmaker.todolist.model.dto.Task;
import java.util.ArrayList;
import java.util.List;

/*
    TodoListDao is a generated DAO class. Don't modify it manually.
    http://sqldalmaker.sourceforge.net

 */
public class TodoListDao {

    protected final DataStore ds;

    public TodoListDao(DataStore ds) {
        this.ds = ds;
    }

    /*
        (C)RUD: groups
        Generated values are passed to DTO.
        Returns the number of affected rows or -1 on error.
     */
    public int createGroup(Group p) throws Exception {
        String sql = "insert into groups (g_name) values (?)";
        String[] gen_col_nm = new String[]{"g_id"};
        Object[] gen_values = new Object[gen_col_nm.length];
        int res = ds.insert(sql, gen_col_nm, gen_values, p.getGName());
        p.setGId(ds.castGeneratedValue(Integer.class, gen_values[0]));
        return res;
    }

    /*
        CR(U)D: groups
        Returns the number of affected rows or -1 on error.
     */
    public int updateGroup(Group p) throws Exception {
        String sql = "update groups set g_name=? where g_id=?";
        return ds.execDML(sql, p.getGName(), p.getGId());
    }

    /*
        CR(U)D: groups
        Returns the number of affected rows or -1 on error.
     */
    public int updateGroup(String gName, Integer gId) throws Exception {
        String sql = "update groups set g_name=? where g_id=?";
        return ds.execDML(sql, gName, gId);
    }

    /*
        CRU(D): groups
        Returns the number of affected rows or -1 on error.
     */
    public int deleteGroup(Integer gId) throws Exception {
        String sql = "delete from groups where g_id=?";
        return ds.execDML(sql, gId);
    }

    public List<Group> getGroups() throws Exception {
        String sql = "select g.*,"
                + "\n (select count(*) from tasks where g_ID=g.g_ID) as tasks_count"
                + "\n from groups g";
        final List<Group> res = new ArrayList<Group>();
        ds.queryAllRows(sql, new DataStore.RowHandler() {
            @Override
            public void handleRow(DataStore.RowData rd) throws Exception {
                Group obj = new Group();
                obj.setGId(rd.getValue(Integer.class, "g_id"));  // q(g_id) <- q(g_id)
                obj.setGName(rd.getValue(String.class, "g_name"));  // q(g_name) <- q(g_name)
                obj.setTasksCount(rd.getValue(Integer.class, "tasks_count"));  // q(tasks_count) <- q(tasks_count)
                res.add(obj);
            }
        });
        return res;
    }

    /*
        (C)RUD: TASKS
        Generated values are passed to DTO.
        Returns the number of affected rows or -1 on error.
     */
    public int createTask(Task p) throws Exception {
        String sql = "insert into TASKS (g_id, t_priority, t_date, t_subject, t_comments) values (?, ?, ?, ?, ?)";
        String[] gen_col_nm = new String[]{"t_id"};
        Object[] gen_values = new Object[gen_col_nm.length];
        int res = ds.insert(sql, gen_col_nm, gen_values, p.getGId(), p.getTPriority(), p.getTDate(), p.getTSubject(), p.getTComments());
        p.setTId(ds.castGeneratedValue(Integer.class, gen_values[0]));
        return res;
    }

    /*
        CR(U)D: TASKS
        Returns the number of affected rows or -1 on error.
     */
    public int updateTask(Task p) throws Exception {
        String sql = "update TASKS set g_id=?, t_priority=?, t_date=?, t_subject=?, t_comments=? where t_id=?";
        return ds.execDML(sql, p.getGId(), p.getTPriority(), p.getTDate(), p.getTSubject(), p.getTComments(), p.getTId());
    }

    /*
        CR(U)D: TASKS
        Returns the number of affected rows or -1 on error.
     */
    public int updateTask(Integer gId, Integer tPriority, String tDate, String tSubject, String tComments, Integer tId) throws Exception {
        String sql = "update TASKS set g_id=?, t_priority=?, t_date=?, t_subject=?, t_comments=? where t_id=?";
        return ds.execDML(sql, gId, tPriority, tDate, tSubject, tComments, tId);
    }

    /*
        CRU(D): TASKS
        Returns the number of affected rows or -1 on error.
     */
    public int deleteTask(Integer tId) throws Exception {
        String sql = "delete from TASKS where t_id=?";
        return ds.execDML(sql, tId);
    }

    public List<Task> getGroupTasks(int t_id) throws Exception {
        String sql = "select * from TASKS where g_id=?"
                + "\n order by t_date";
        final List<Task> res = new ArrayList<Task>();
        ds.queryAllRows(sql, new DataStore.RowHandler() {
            @Override
            public void handleRow(DataStore.RowData rd) throws Exception {
                Task obj = new Task();
                obj.setTId(rd.getValue(Integer.class, "t_id"));  // t(t_id) <- q(t_id)
                obj.setGId(rd.getValue(Integer.class, "g_id"));  // t(g_id) <- q(g_id)
                obj.setTPriority(rd.getValue(Integer.class, "t_priority"));  // t(t_priority) <- q(t_priority)
                obj.setTDate(rd.getValue(String.class, "t_date"));  // t(t_date) <- q(t_date)
                obj.setTSubject(rd.getValue(String.class, "t_subject"));  // t(t_subject) <- q(t_subject)
                obj.setTComments(rd.getValue(String.class, "t_comments"));  // t(t_comments) <- q(t_comments)
                res.add(obj);
            }
        }, t_id);
        return res;
    }
}