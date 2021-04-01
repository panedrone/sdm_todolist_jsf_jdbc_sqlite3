package com.sqldalmaker.todolist.model.dto;

/*
    Task is a generated DTO class. Don't modify it manually.
    http://sqldalmaker.sourceforge.net

 */
public class Task  {

    private Integer tId;  // t(t_id)
    private Integer gId;  // t(g_id)
    private Integer tPriority;  // t(t_priority)
    private String tDate;  // t(t_date)
    private String tSubject;  // t(t_subject)
    private String tComments;  // t(t_comments)

    public Integer getTId() {
        return this.tId;
    }

    public void setTId(Integer tId) {
        this.tId = tId;
    }

    public Integer getGId() {
        return this.gId;
    }

    public void setGId(Integer gId) {
        this.gId = gId;
    }

    public Integer getTPriority() {
        return this.tPriority;
    }

    public void setTPriority(Integer tPriority) {
        this.tPriority = tPriority;
    }

    public String getTDate() {
        return this.tDate;
    }

    public void setTDate(String tDate) {
        this.tDate = tDate;
    }

    public String getTSubject() {
        return this.tSubject;
    }

    public void setTSubject(String tSubject) {
        this.tSubject = tSubject;
    }

    public String getTComments() {
        return this.tComments;
    }

    public void setTComments(String tComments) {
        this.tComments = tComments;
    }
}