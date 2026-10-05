package com.example.meetingnotes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Главный экран: загрузка, поиск, редактирование и удаление заметок. */
public class MainActivity extends AppCompatActivity implements NoteAdapter.Listener {
    private NoteRepository repository;
    private NoteAdapter adapter;
    private TextInputEditText searchEditText;
    private TextView emptyTextView;
    private final List<Note> allNotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new NoteRepository(this);
        adapter = new NoteAdapter(this);
        searchEditText = findViewById(R.id.searchEditText);
        emptyTextView = findViewById(R.id.emptyTextView);
        RecyclerView recyclerView = findViewById(R.id.notesRecyclerView);
        FloatingActionButton addButton = findViewById(R.id.addNoteButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        addButton.setOnClickListener(view ->
                startActivity(new Intent(this, NoteEditActivity.class)));

        // Фильтрация выполняется сразу после каждого изменения строки поиска.
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        allNotes.clear();
        allNotes.addAll(repository.getAll());
        applyFilter(searchEditText.getText() == null ? "" : searchEditText.getText().toString());
    }

    private void applyFilter(String query) {
        String normalized = query.trim().toLowerCase(new Locale("ru", "RU"));
        List<Note> filtered = new ArrayList<>();
        for (Note note : allNotes) {
            String searchable = (note.getTitle() + " " + note.getContent() + " "
                    + DateFormatter.format(note.getEventAt()) + " " + note.getStorage())
                    .toLowerCase(new Locale("ru", "RU"));
            if (normalized.isEmpty() || searchable.contains(normalized)) {
                filtered.add(note);
            }
        }
        adapter.submitList(filtered);
        emptyTextView.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEdit(Note note) {
        Intent intent = new Intent(this, NoteEditActivity.class);
        intent.putExtra(NoteEditActivity.EXTRA_ID, note.getId());
        intent.putExtra(NoteEditActivity.EXTRA_TITLE, note.getTitle());
        intent.putExtra(NoteEditActivity.EXTRA_CONTENT, note.getContent());
        intent.putExtra(NoteEditActivity.EXTRA_EVENT_AT, note.getEventAt());
        intent.putExtra(NoteEditActivity.EXTRA_STORAGE, note.getStorage());
        intent.putExtra(NoteEditActivity.EXTRA_CREATED_AT, note.getCreatedAt());
        startActivity(intent);
    }

    @Override
    public void onDelete(Note note) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить заметку?")
                .setMessage("Заметка «" + note.getTitle() + "» будет удалена без возможности отмены.")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Удалить", (dialog, which) -> {
                    if (repository.delete(note.getId(), note.getStorage())) {
                        Toast.makeText(this, "Заметка удалена", Toast.LENGTH_SHORT).show();
                        loadNotes();
                    } else {
                        Toast.makeText(this, "Не удалось удалить заметку", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }
}
