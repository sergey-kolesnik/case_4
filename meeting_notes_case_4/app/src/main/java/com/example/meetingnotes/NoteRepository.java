package com.example.meetingnotes;

import android.content.Context;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Единая точка доступа к двум способам хранения заметок. */
public class NoteRepository {
    private final DatabaseHelper databaseHelper;
    private final FileStorageHelper fileStorageHelper;

    public NoteRepository(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
        fileStorageHelper = new FileStorageHelper(context.getApplicationContext());
    }

    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();
        notes.addAll(databaseHelper.getAll());
        notes.addAll(fileStorageHelper.getAll());
        notes.sort(Comparator.comparingLong(Note::getUpdatedAt).reversed());
        return notes;
    }

    public boolean save(Note note) {
        return Note.STORAGE_FILE.equals(note.getStorage())
                ? fileStorageHelper.save(note)
                : databaseHelper.save(note);
    }

    public boolean delete(String id, String storage) {
        return Note.STORAGE_FILE.equals(storage)
                ? fileStorageHelper.delete(id)
                : databaseHelper.delete(id);
    }
}
