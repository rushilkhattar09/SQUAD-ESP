package com.squad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squad.questions.Constants;
import com.squad.questions.RandomQuestions;
import com.squad.structure.Question;
import com.squad.structure.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String taskDocID;
    private Task newTask;
    private ImageView primaryQuestion;
    private ImageView option1, option2,option3;
    private Button nextBtn;

    FirebaseFirestore rootDB = FirebaseFirestore.getInstance();
    CollectionReference players = rootDB.collection("Players");
    CollectionReference tasks = rootDB.collection("Tasks");

    private ArrayList<Question> questions = RandomQuestions.getPrimaryQuestion();
    List<String> index;
    private int[] answers = new int[] {-1, -1, -1, -1, -1};
    private int questionNo = 0;

    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIntentData();
        init();
        getTask();

    }

    private void getIntentData() {
        taskDocID = getIntent().getStringExtra(Constants.DOC_ID);
    }

    private void init() {
        primaryQuestion = findViewById(R.id.primary_question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setVisibility(View.GONE);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answers[questionNo] != -1) {
                    questionNo++;
                    if (questionNo < index.size())
                        setData();
                    else generate();

                } else {
                    Toast.makeText(MainActivity.this, "click on answer !", Toast.LENGTH_LONG).show();
                }
            }
        });

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers[questionNo] = 0;
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers[questionNo] = 1;
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers[questionNo] = 2;
            }
        });




        docRef = tasks.document(taskDocID);
    }

    private void getTask() {
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            newTask = new Task(
                                    (boolean) documentSnapshot.getData().get(Constants.COMPLETED),
                                    (List<Integer>) documentSnapshot.getData().get(Constants.QUESTIONS),
                                    (String) documentSnapshot.getData().get(Constants.PLAYER_1),
                                    (String) documentSnapshot.getData().get(Constants.PLAYER_2),
                                    documentSnapshot.getId(),
                                    (String) documentSnapshot.getData().get(Constants.RESPONSE_PLAYER1),
                                    (String) documentSnapshot.getData().get(Constants.RESPONSE_PLAYER1)
                            );

                            parsing();
                            nextBtn.setVisibility(View.VISIBLE);
                            setData();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void logMessage(String message) {
        Log.i("Detail", message);
    }

    private void parsing() {
        String questionIndexes = newTask.getQuestions().toString().replace(" ", "");
        String newquestionIndexes = questionIndexes.substring(1, questionIndexes.length()-1);
        String[] elements = newquestionIndexes.split(",");
        index = Arrays.asList(elements);
    }

    private void setData() {
        Question question = questions.get(Integer.parseInt(index.get(questionNo)));

        Glide.with(this)
                .load(question.getQuestion())
                .into(primaryQuestion);

        Glide.with(this)
                .load(question.getOptions().getOption1())
                .into(option1);
        Glide.with(this)
                .load(question.getOptions().getOption2())
                .into(option2);
        Glide.with(this)
                .load(question.getOptions().getOption3())
                .into(option3);



    }

    private void generate() {

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {
                    Map<String, Object> data = new HashMap<>();
                    String myResponseStr = "";

                    newTask = new Task(
                            (boolean) documentSnapshot.getData().get(Constants.COMPLETED),
                            (List<Integer>) documentSnapshot.getData().get(Constants.QUESTIONS),
                            (String) documentSnapshot.getData().get(Constants.PLAYER_1),
                            (String) documentSnapshot.getData().get(Constants.PLAYER_2),
                            documentSnapshot.getId(),
                            (String) documentSnapshot.getData().get(Constants.RESPONSE_PLAYER1),
                            (String) documentSnapshot.getData().get(Constants.RESPONSE_PLAYER2)
                    );
                    for (int i : answers) myResponseStr += i;
                    if (newTask.getResponsePlayer1().isEmpty()) {
                        data.put(Constants.RESPONSE_PLAYER1, myResponseStr);
                    } else {
                        data.put(Constants.RESPONSE_PLAYER2, myResponseStr);
                        data.put(Constants.COMPLETED, true);
                    }
                    docRef.update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }

            }
        });

    }

}
