package com.example.meetingnotes;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Хранит каждую заметку отдельным JSON-файлом во внутреннем каталоге приложения. */
public class FileStorageHelper {
    private final File directory;

    public FileStorageHelper(Context context) {
        directory = new File(context.getFilesDir(), "meeting_notes");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public boolean save(Note note) {
        File target = fileFor(note.getId());
        File temporary = new File(directory, note.getId() + ".tmp");
        try (FileWriter writer = new FileWriter(temporary, false)) {
            writer.write(toJson(note).toString(2));
        } catch (IOException | JSONException error) {
            return false;
        }

        // Сначала создается временный файл, затем он атомарно заменяет основной.
        if (target.exists() && !target.delete()) {
            temporary.delete();
            return false;
        }
        return temporary.renameTo(target);
    }

    public List<Note> getAll() {
        List<Note> result = new ArrayList<>();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return result;
        }
        for (File file : files) {
            try {
                result.add(fromJson(new JSONObject(readText(file))));
            } catch (IOException | JSONException ignored) {
                // Поврежденный файл пропускается, чтобы список остальных заметок продолжал работать.
            }
        }
        return result;
    }

    public boolean delete(String id) {
        File target = fileFor(id);
        return !target.exists() || target.delete();
    }

    private File fileFor(String id) {
        // UUID содержит только безопасные символы, но дополнительная очистка защищает имя файла.
        String safeId = id.replaceAll("[^a-zA-Z0-9_-]", "_");
        return new File(directory, safeId + ".json");
    }

    private JSONObject toJson(Note note) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", note.getId());
        json.put("title", note.getTitle());
        json.put("content", note.getContent());
        json.put("eventAt", note.getEventAt());
        json.put("storage", Note.STORAGE_FILE);
        json.put("createdAt", note.getCreatedAt());
        json.put("updatedAt", note.getUpdatedAt());
        return json;
    }

    private Note fromJson(JSONObject json) throws JSONException {
        return new Note(
                json.getString("id"),
                json.getString("title"),
                json.optString("content", ""),
                json.getLong("eventAt"),
                Note.STORAGE_FILE,
                json.getLong("createdAt"),
                json.getLong("updatedAt")
        );
    }

    private String readText(File file) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }
}
