package com.example.meetingnotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/** Адаптер связывает список моделей Note с карточками RecyclerView. */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    public interface Listener {
        void onEdit(Note note);
        void onDelete(Note note);
    }

    private final List<Note> notes = new ArrayList<>();
    private final Listener listener;

    public NoteAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Note> newNotes) {
        notes.clear();
        notes.addAll(newNotes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.date.setText(DateFormatter.format(note.getEventAt()));
        holder.storage.setText(Note.STORAGE_FILE.equals(note.getStorage()) ? "JSON" : "SQLite");
        holder.editButton.setOnClickListener(view -> listener.onEdit(note));
        holder.deleteButton.setOnClickListener(view -> listener.onDelete(note));
        holder.itemView.setOnClickListener(view -> listener.onEdit(note));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView content;
        final TextView date;
        final TextView storage;
        final MaterialButton editButton;
        final MaterialButton deleteButton;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.noteTitleTextView);
            content = itemView.findViewById(R.id.noteContentTextView);
            date = itemView.findViewById(R.id.noteDateTextView);
            storage = itemView.findViewById(R.id.noteStorageTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
