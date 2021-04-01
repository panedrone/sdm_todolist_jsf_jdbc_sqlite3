package com.sqldalmaker.todolist.model.dto;

/*
    Group is a generated DTO class. Don't modify it manually.
    http://sqldalmaker.sourceforge.net

 */
public class Group  {

    private Integer gId;  // q(g_id)
    private String gName;  // q(g_name)
    private Integer tasksCount;  // q(tasks_count)

    public Integer getGId() {
        return this.gId;
    }

    public void setGId(Integer gId) {
        this.gId = gId;
    }

    public String getGName() {
        return this.gName;
    }

    public void setGName(String gName) {
        this.gName = gName;
    }

    public Integer getTasksCount() {
        return this.tasksCount;
    }

    public void setTasksCount(Integer tasksCount) {
        this.tasksCount = tasksCount;
    }
}