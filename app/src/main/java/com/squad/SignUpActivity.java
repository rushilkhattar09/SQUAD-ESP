package com.squad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squad.structure.Player;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    FirebaseFirestore rootDB = FirebaseFirestore.getInstance();
    CollectionReference players = rootDB.collection("Players");


    private EditText userName;
    private Button enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
        setListeners();

    }

    private void init() {
        userName = findViewById(R.id.usernameText);
        enter = findViewById(R.id.signup);
    }

    private void setListeners() {
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userName.getText().toString().isEmpty()) {
                    checkIfUserExists(userName.getText().toString());
                }
            }
        });
    }

    private void checkIfUserExists(final String userId) {
        players.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                

                    boolean flag = false;

                    if (task.getResult() != null) {

                        

                        for (QueryDocumentSnapshot snapshot : task.getResult()) {

                            logMessage(snapshot.getData().get("userId").toString());

                            if (snapshot.getData().get("userId").equals(userId)) {
                                initiateGame(userId, snapshot.getId());
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            logMessage("new user");

                            players.add(new Player(userName.getText().toString(), 0, new HashMap<String, Player>()))
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            initiateGame(userId, documentReference.getId());
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, "error" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                    else {
                        logMessage("new user");

                        players.add(new Player(userName.getText().toString(), 0, new HashMap<String, Player>()))
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        initiateGame(userId, documentReference.getId());
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, "error" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }

                } else {
                    Toast.makeText(SignUpActivity.this, "error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void logMessage(String message) {
        Log.i("Login", message);
    }

    private void initiateGame(String userID, final String docID) {
        
        Intent intent = new Intent(SignUpActivity.this, GamesActivity.class);
        intent.putExtra("userID", userID);
        intent.putExtra("docID", docID);
        startActivity(intent);
        finish();
    }

    

}
