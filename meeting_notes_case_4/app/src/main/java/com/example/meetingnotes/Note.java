package com.example.meetingnotes;

/** Модель одной деловой заметки, общая для SQLite и файлового хранилища. */
public class Note {
    public static final String STORAGE_SQLITE = "SQLITE";
    public static final String STORAGE_FILE = "FILE";

    private String id;
    private String title;
    private String content;
    private long eventAt;
    private String storage;
    private long createdAt;
    private long updatedAt;

    public Note(String id, String title, String content, long eventAt,
                String storage, long createdAt, long updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.eventAt = eventAt;
        this.storage = storage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public long getEventAt() { return eventAt; }
    public String getStorage() { return storage; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
}
