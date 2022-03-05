package com.example.notesapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
//      This is the place we take care of getting value of Note obj into Views of NoteViewHolder
        Note currentNote = notes.get(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));
    }

    @Override
    public int getItemCount() {
//      here we return how many items we want to return in our recyclerView
//      And we want to display as many items as there are currently notes in our Arraylist
        return notes.size();
    }

    /**
     * Purpose of this method:
     * In MainActivity.java we OBSERVED the LiveData and in the onChanged Callback
     * we got passed a "list of notes"
     * And we need to get a way to get these Notes in our RecyclerView
     * And this is what this method is for!!
     * @param notes
     */
    public void setNotes(List<Note> notes) {
        this.notes = notes;
//       Now we need to tell the adapter to Redraw the Layout:
        // This below method MUST NOT be used, ANdroid Studio Notifies the same
        notifyDataSetChanged(); // WE will change this method later, coz this is not the best solution
    }

    /**
     * This is to return a note at a given position
     * @param position - position of note
     * @return
     */
    public Note getNoteAt(int position) {
        return notes.get(position);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle, textViewPriority, textViewDescription;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_title);
            textViewPriority = itemView.findViewById(R.id.textview_priority );
            textViewDescription = itemView.findViewById(R.id.textview_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positionOfNoteClicked = getAdapterPosition();
                    if (listener != null && positionOfNoteClicked != RecyclerView.NO_POSITION) {
                        listener.onItemClick(notes.get(positionOfNoteClicked));
                    }
                }
            });
        }
    }

    /** SEE THIS VIDEO AGAIN FOR UNDERSTANDING AND NOTE PROPERLY -- VIDEO 9
     * We create interface for Handling clicks when using RecyclerView
     *
     * Now anything that implements this interface has to implement the onItemClick method.
     * This is just like :
     * view.setOnItemClickListener(new OnItemClickListener(
     * onItemClick(...) {
     *
     * });
     */
    public interface OnItemClickListener {
        void onItemClick(Note note); //We can also pass something else like position, but here we are interested more in Note
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
