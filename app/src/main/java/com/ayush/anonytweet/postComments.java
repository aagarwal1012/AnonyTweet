package com.ayush.anonytweet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayush.anonytweet.Classes.postCommentClass;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class postComments extends AppCompatActivity {

    private RelativeLayout activity_post_comments;
    //Add Emojicon
    private EmojiconEditText emojiconEditText;
    private ImageView emojiButton;
    private ImageView submitButton;
    private EmojIconActions emojIconActions;
    private String postId;

    private static long longHash(String string) {
        long h = 0;
        int l = string.length();
        char[] chars = string.toCharArray();

        for (int i = 0; i < l; i++) {
            h = 2 * h + chars[i];
        }
        return h;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        activity_post_comments = findViewById(R.id.activity_main);

        Bundle bundle = getIntent().getExtras();
        postId = bundle.getString("TweetId");

        //Add Emoji
        emojiButton = findViewById(R.id.emoji_button);
        submitButton = findViewById(R.id.submit_button);
        emojiconEditText = findViewById(R.id.emojicon_edit_text);
        emojIconActions = new EmojIconActions(getApplicationContext(), activity_post_comments, emojiButton, emojiconEditText);
        emojIconActions.ShowEmojicon();

        submitButton.setOnClickListener(view -> {
            FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).push().setValue(new postCommentClass(emojiconEditText.getText().toString(),
                    FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            emojiconEditText.setText("");
            emojiconEditText.requestFocus();
        });

        //Load content
        displayChatMessage();

    }

    private void displayChatMessage() {
        ListView listOfMessage = findViewById(R.id.list_of_message);
        FirebaseListAdapter<postCommentClass> adapter = new FirebaseListAdapter<postCommentClass>(this, postCommentClass.class, R.layout.list_comment_item, FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)) {
            @Override
            protected void populateView(View v, postCommentClass model, int position) {

                TextView messageText, messageUser, messageTime;
                messageText = (EmojiconTextView) v.findViewById(R.id.message_text);
                messageUser = v.findViewById(R.id.message_user);
                messageTime = v.findViewById(R.id.message_time);

                messageText.setText(model.getCommentText());

                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(model.getUserId())) {
                    messageUser.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                } else {
                    messageUser.setText("Anonymous " + longHash(model.getUserId()));
                }

                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getCommentTime()));

            }
        };
        listOfMessage.setAdapter(adapter);
    }

}
