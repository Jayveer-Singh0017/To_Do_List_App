package com.singh.todolist;

public class Task {
    private int id;
    private String task;
    private int status;
    private String createdAt;

    public Task(int id, String task, int status, String createdAt) {
        this.id = id;
        this.task = task;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getTask() { return task; }
    public int getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
} 