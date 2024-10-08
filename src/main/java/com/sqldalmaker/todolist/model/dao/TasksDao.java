package com.sqldalmaker.todolist.model.dao;

import com.sqldalmaker.DataStore;

import com.sqldalmaker.todolist.model.dto.Task;
import java.util.ArrayList;
import java.util.List;

// Code generated by a tool. DO NOT EDIT.
// https://sqldalmaker.sourceforge.net/

public class TasksDao {

    protected final DataStore ds;

    public TasksDao(DataStore ds) {
        this.ds = ds;
    }

    /*
        (C)RUD: tasks
        Generated values are passed to DTO.
        Returns the number of affected rows or -1 on error.
     */
    public int createTask(Task p) throws Exception {
        String sql = "insert into tasks (g_id, t_priority, t_date, t_subject, t_comments) values (?, ?, ?, ?, ?)";
        String[] gen_col_nm = new String[]{"t_id"};
        Object[] gen_values = new Object[gen_col_nm.length];
        int res = ds.insert(sql, gen_col_nm, gen_values, p.getGID(), p.getTPriority(), p.getTDate(), p.getTSubject(), p.getTComments());
        p.setTID(ds.castGeneratedValue(Integer.class, gen_values[0]));
        return res;
    }

    /*
        CR(U)D: tasks
        Returns the number of affected rows or -1 on error.
     */
    public int updateTask(Task p) throws Exception {
        String sql = "update tasks set g_id=?, t_priority=?, t_date=?, t_subject=?, t_comments=? where t_id=?";
        return ds.execDML(sql, p.getGID(), p.getTPriority(), p.getTDate(), p.getTSubject(), p.getTComments(), p.getTID());
    }

    /*
        CR(U)D: tasks
        Returns the number of affected rows or -1 on error.
     */
    public int updateTask(Integer gID, Integer tPriority, String tDate, String tSubject, String tComments, Integer tID) throws Exception {
        String sql = "update tasks set g_id=?, t_priority=?, t_date=?, t_subject=?, t_comments=? where t_id=?";
        return ds.execDML(sql, gID, tPriority, tDate, tSubject, tComments, tID);
    }

    /*
        CRU(D): tasks
        Returns the number of affected rows or -1 on error.
     */
    public int deleteTask(Integer tID) throws Exception {
        String sql = "delete from tasks where t_id=?";
        return ds.execDML(sql, tID);
    }

    public List<Task> getGroupTasks(int t_id) throws Exception {
        String sql = "select * from TASKS where g_id=?" +
        "\n order by t_date";
        final List<Task> res = new ArrayList<Task>();
        ds.queryAllRows(sql, new DataStore.RowHandler() {
            @Override
            public void handleRow(DataStore.RowData rd) throws Exception {
                Task obj = new Task();
                obj.setTID(rd.getInteger("t_id"));  // t <- q
                obj.setGID(rd.getInteger("g_id"));  // t <- q
                obj.setTPriority(rd.getInteger("t_priority"));  // t <- q
                obj.setTDate(rd.getString("t_date"));  // t <- q
                obj.setTSubject(rd.getString("t_subject"));  // t <- q
                obj.setTComments(rd.getString("t_comments"));  // t <- q
                res.add(obj);
            }
        }, t_id);
        return res;
    }
}