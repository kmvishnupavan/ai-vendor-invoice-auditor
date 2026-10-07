package com.example.invoiceaudit.dto;

public class SystemStatusResponse {

    private boolean aiConnected;
    private String aiMode;
    private String aiModel;
    private boolean databaseConnected;
    private String databaseType;

    public SystemStatusResponse() {
    }

    public SystemStatusResponse(boolean aiConnected, String aiMode, String aiModel, boolean databaseConnected, String databaseType) {
        this.aiConnected = aiConnected;
        this.aiMode = aiMode;
        this.aiModel = aiModel;
        this.databaseConnected = databaseConnected;
        this.databaseType = databaseType;
    }

    public boolean isAiConnected() { return aiConnected; }
    public void setAiConnected(boolean aiConnected) { this.aiConnected = aiConnected; }
    public String getAiMode() { return aiMode; }
    public void setAiMode(String aiMode) { this.aiMode = aiMode; }
    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }
    public boolean isDatabaseConnected() { return databaseConnected; }
    public void setDatabaseConnected(boolean databaseConnected) { this.databaseConnected = databaseConnected; }
    public String getDatabaseType() { return databaseType; }
    public void setDatabaseType(String databaseType) { this.databaseType = databaseType; }
}
