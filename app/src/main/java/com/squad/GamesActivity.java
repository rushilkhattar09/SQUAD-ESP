package com.squad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squad.CustomAdapter.CustomAdapter;

import com.squad.questions.Constants;
import com.squad.questions.RandomQuestions;
import com.squad.structure.Player;
import com.squad.structure.Question;
import com.squad.structure.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GamesActivity extends AppCompatActivity  {

    private RecyclerView recyclerView;
    private Button b1;
    private TextView scoreTextView;

    private CustomAdapter customAdapter;

    private Player myPlayer;
    private String UserID;
    private String DocID;

    FirebaseFirestore rootDB = FirebaseFirestore.getInstance();
    CollectionReference players = rootDB.collection("Players");
    CollectionReference tasks = rootDB.collection("Tasks");

    private ArrayList<Task> mTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        getIntentData();
        recyclerView = findViewById(R.id.tasksRecyclerView);
        b1 = findViewById(R.id.pair);
        scoreTextView = findViewById(R.id.score);
        customAdapter = new CustomAdapter(mTasks, UserID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customAdapter);

        getPlayer();
        setListeners();

    }

    private void getIntentData() {
        UserID = getIntent().getStringExtra("userID");
        DocID = getIntent().getStringExtra("docID");

        logMessage(DocID);

    }

    private void getPlayer() {
        players.whereEqualTo(Constants.USER_ID, UserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                        myPlayer = new Player((String) snapshot.get(Constants.USER_ID)
                                , (long)snapshot.get(Constants.SCORE),
                                (HashMap<String, Player>) snapshot.get(Constants.PAIRED_PLAYERS));

                        setTaskListener();
                        setScoreListener();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void setListeners() {
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairWithRandomPlayer(UserID);
            }
        });
    }

    private void pairWithRandomPlayer(final String UserID) {
        players.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                int myIndex = 0;
                for (int i=0; i<queryDocumentSnapshots.getDocuments().size(); i++) {
                    if (queryDocumentSnapshots.getDocuments().get(i).get(Constants.USER_ID).equals(UserID)) {
                        myIndex = i;
                        break;
                    }
                }
                int randomi = 0;
                Random random = new Random();
                do {
                    randomi = random.nextInt((queryDocumentSnapshots.getDocuments().size()));
                }
                while (randomi == myIndex);

                DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(randomi);

                ArrayList<Question> list = new ArrayList<>();
                list.addAll(RandomQuestions.getPrimaryQuestion());
                List<Integer> indices = new ArrayList<>();
                HashSet<Integer> checkSet = new HashSet<>();

                int i=0;
                while (i<5) {
                    Integer element = random.nextInt(list.size());
                    if (!checkSet.contains(element)) {
                        indices.add(element);
                        checkSet.add(element);
                        i++;
                    }
                }

                Task task = new Task(false, indices, UserID,
                        (String) snapshot.getData().get(Constants.USER_ID), "", "", "");

                logMessage(task.toString());

                tasks.add(task)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(GamesActivity.this, "success", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GamesActivity.this, "Network error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

        }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void logMessage(String message) {
        Log.i("Tasks", message);
    }



    private void setTaskListener() {
        tasks.whereEqualTo(Constants.DOC_ID, "")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if(documentChange.getType() == DocumentChange.Type.ADDED)
                            {
                                    if (!((boolean) documentChange.getDocument().getData().get(Constants.COMPLETED)) &&
                                            (documentChange.getDocument().getData().get(Constants.PLAYER_1).equals(UserID) ||
                                                    documentChange.getDocument().getData().get(Constants.PLAYER_2).equals(UserID))) {

                                        mTasks.add(new Task(
                                                (boolean) documentChange.getDocument().getData().get(Constants.COMPLETED),
                                                (List<Integer>) documentChange.getDocument().getData().get(Constants.QUESTIONS),
                                                (String) documentChange.getDocument().getData().get(Constants.PLAYER_1),
                                                (String) documentChange.getDocument().getData().get(Constants.PLAYER_2),
                                                documentChange.getDocument().getId(),
                                                (String) documentChange.getDocument().getData().get(Constants.RESPONSE_PLAYER1),
                                                (String) documentChange.getDocument().getData().get(Constants.RESPONSE_PLAYER2))
                                        );

                                        customAdapter.notifyItemInserted(mTasks.size() - 1);

                                    }
                            } else if(documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                if ((boolean) documentChange.getDocument().getData().get(Constants.COMPLETED) &&
                                        (documentChange.getDocument().getData().get(Constants.PLAYER_1).equals(UserID) ||
                                                documentChange.getDocument().getData().get(Constants.PLAYER_2).equals(UserID))) {

                                    Task modifiedTask = new Task(
                                            (boolean) documentChange.getDocument().getData().get(Constants.COMPLETED),
                                            (List<Integer>) documentChange.getDocument().getData().get(Constants.QUESTIONS),
                                            (String) documentChange.getDocument().getData().get(Constants.PLAYER_1),
                                            (String) documentChange.getDocument().getData().get(Constants.PLAYER_2),
                                            documentChange.getDocument().getId(),
                                            (String) documentChange.getDocument().getData().get(Constants.RESPONSE_PLAYER1),
                                            (String) documentChange.getDocument().getData().get(Constants.RESPONSE_PLAYER2)
                                    );

                                    for (int i = 0; i < mTasks.size(); i++)
                                        if (modifiedTask.getDocID().equals(mTasks.get(i).getDocID())) {
                                            mTasks.remove(i);
                                            customAdapter.notifyItemRemoved(i);
                                            break;
                                        }

                                    if (modifiedTask.getResponsePlayer1().equals(modifiedTask.getResponsePlayer2())) {
                                        players.whereEqualTo(Constants.USER_ID, UserID)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                                                        Map<String, Object> data = new HashMap<>();
                                                        data.put(Constants.SCORE, myPlayer.getScore() + 1);
                                                        players.document(snapshot.getId()).update(data);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                    }

                                }
                            }
                        }

                    }
                });
    }

    private void setScoreListener() {
        players.document(DocID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                scoreTextView.setText("Score is " + documentSnapshot.getData().get(Constants.SCORE));

            }
        });
    }





}
