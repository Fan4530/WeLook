package com.laioffer.eventreporter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends Activity {
    private DatabaseReference database;
    private ListView listView;
    private EditText editText;
    private CommentAdapter commentAdapter;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        Intent intent = getIntent();
        final String eventId = intent.getStringExtra("EventID");
        final String commenter = intent.getStringExtra("Commenter");

        listView = (ListView) findViewById(R.id.comment_listview);
        editText = (EditText) findViewById(R.id.comment_edittext);
        button = (Button) findViewById(R.id.comment_submit);

        database = FirebaseDatabase.getInstance().getReference();
        commentAdapter = new CommentAdapter(this);
        getData(eventId, commentAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(eventId, commenter);
                editText.setText("");
                getData(eventId, commentAdapter);

            }
        });


    }


    private void sendComment(final String eventId, final String commenter) {
        String description = editText.getText().toString();
        if (description.equals("")) {
            return;
        }
        Comment comment = new Comment();
        comment.setCommenter(commenter);
        comment.setEventId(eventId);
        comment.setDescription(description);
        comment.setTime(System.currentTimeMillis());
        String key = database.child("comments").push().getKey();

        comment.setCommentId(key);
        database.child("comments").child(key).setValue(comment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "The comment is failed, please check you network status.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "The comment is reported", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void getData(final String eventId, final CommentAdapter commentAdapter) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DataSnapshot commentSnapshot = dataSnapshot.child("comments");
                List<Comment> comments = new ArrayList<Comment>();
                for (DataSnapshot noteDataSnapshot : commentSnapshot.getChildren()) {
                    Comment comment = noteDataSnapshot.getValue(Comment.class);
                    if(comment.getEventId().equals(eventId)) {
                        comments.add(comment);
                    }
                }
                database.getRef().child("events").child(eventId).child("commentNumber").setValue(comments.size());
                commentAdapter.setComments(comments);


                DataSnapshot eventSnapshot = dataSnapshot.child("events");
                for (DataSnapshot noteDataSnapshot : eventSnapshot.getChildren()) {
                    Event event = noteDataSnapshot.getValue(Event.class);
                    if(event.getId().equals(eventId)) {
                        commentAdapter.setEvent(event);
                        break;
                    }
                }

                listView.setAdapter(commentAdapter);
                commentAdapter.notifyDataSetChanged();//对整个listview 进行更新，更新界面
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




                    }
