package com.example.meetingnotes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

/** Экран создания и редактирования заметки. */
public class NoteEditActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "note_id";
    public static final String EXTRA_TITLE = "note_title";
    public static final String EXTRA_CONTENT = "note_content";
    public static final String EXTRA_EVENT_AT = "note_event_at";
    public static final String EXTRA_STORAGE = "note_storage";
    public static final String EXTRA_CREATED_AT = "note_created_at";

    private final Calendar eventCalendar = Calendar.getInstance();
    private TextInputEditText titleEditText;
    private TextInputEditText contentEditText;
    private MaterialButton dateButton;
    private MaterialButton timeButton;
    private RadioButton sqliteRadioButton;
    private RadioButton fileRadioButton;
    private NoteRepository repository;
    private String noteId;
    private String originalStorage;
    private long createdAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        repository = new NoteRepository(this);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        sqliteRadioButton = findViewById(R.id.sqliteRadioButton);
        fileRadioButton = findViewById(R.id.fileRadioButton);
        MaterialButton saveButton = findViewById(R.id.saveButton);
        MaterialToolbar toolbar = findViewById(R.id.editToolbar);

        noteId = getIntent().getStringExtra(EXTRA_ID);
        boolean editing = noteId != null;
        toolbar.setTitle(editing ? R.string.edit_note : R.string.new_note);
        toolbar.setNavigationOnClickListener(view -> finish());

        if (editing) {
            fillExistingNote();
        } else {
            noteId = UUID.randomUUID().toString();
            createdAt = System.currentTimeMillis();
            originalStorage = null;
        }
        updateDateTimeButtons();

        dateButton.setOnClickListener(view -> showDatePicker());
        timeButton.setOnClickListener(view -> showTimePicker());
        saveButton.setOnClickListener(view -> saveNote());
    }

    private void fillExistingNote() {
        titleEditText.setText(getIntent().getStringExtra(EXTRA_TITLE));
        contentEditText.setText(getIntent().getStringExtra(EXTRA_CONTENT));
        eventCalendar.setTimeInMillis(getIntent().getLongExtra(EXTRA_EVENT_AT, System.currentTimeMillis()));
        originalStorage = getIntent().getStringExtra(EXTRA_STORAGE);
        createdAt = getIntent().getLongExtra(EXTRA_CREATED_AT, System.currentTimeMillis());
        if (Note.STORAGE_FILE.equals(originalStorage)) {
            fileRadioButton.setChecked(true);
        } else {
            sqliteRadioButton.setChecked(true);
        }
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            eventCalendar.set(Calendar.YEAR, year);
            eventCalendar.set(Calendar.MONTH, month);
            eventCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDateTimeButtons();
        }, eventCalendar.get(Calendar.YEAR), eventCalendar.get(Calendar.MONTH),
                eventCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(this, (view, hour, minute) -> {
            eventCalendar.set(Calendar.HOUR_OF_DAY, hour);
            eventCalendar.set(Calendar.MINUTE, minute);
            updateDateTimeButtons();
        }, eventCalendar.get(Calendar.HOUR_OF_DAY), eventCalendar.get(Calendar.MINUTE), true).show();
    }

    private void updateDateTimeButtons() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru", "RU"));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("ru", "RU"));
        dateButton.setText(dateFormat.format(eventCalendar.getTime()));
        timeButton.setText(timeFormat.format(eventCalendar.getTime()));
    }

    private void saveNote() {
        String title = textOf(titleEditText).trim();
        String content = textOf(contentEditText).trim();
        if (title.isEmpty()) {
            titleEditText.setError("Введите заголовок");
            titleEditText.requestFocus();
            return;
        }

        String storage = fileRadioButton.isChecked() ? Note.STORAGE_FILE : Note.STORAGE_SQLITE;
        long now = System.currentTimeMillis();
        Note note = new Note(noteId, title, content, eventCalendar.getTimeInMillis(),
                storage, createdAt, now);

        if (repository.save(note)) {
            // При смене способа хранения старая копия удаляется только после успешного сохранения новой.
            if (originalStorage != null && !storage.equals(originalStorage)) {
                repository.delete(noteId, originalStorage);
            }
            Toast.makeText(this, "Заметка сохранена", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Не удалось сохранить заметку", Toast.LENGTH_LONG).show();
        }
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }
}
