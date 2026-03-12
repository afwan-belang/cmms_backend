package com.sekolah.model;

import java.sql.Timestamp;

public class WorkOrder {
    private int id;
    private int assetId;
    private String assetName; // Tambahan untuk tampilan (dari hasil Join)
    private String technicianName; // Tambahan untuk tampilan
    private String issue;
    private String priority; // HIGH, MEDIUM, LOW
    private String status;   // OPEN, IN_PROGRESS, COMPLETED
    private Timestamp createdAt;

    // Constructor lengkap
    public WorkOrder(int id, String assetName, String technicianName, String issue, String priority, String status, Timestamp createdAt) {
        this.id = id;
        this.assetName = assetName;
        this.technicianName = technicianName;
        this.issue = issue;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public String getAssetName() { return assetName; }
    public String getTechnicianName() { return technicianName; }
    public String getIssue() { return issue; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}