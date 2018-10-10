package com.example.agape.sdb;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username, email, password;
    Button btn_register_signUp;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Signup");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register_signUp = findViewById(R.id.btn_register_signUp);

        auth = FirebaseAuth.getInstance();

        btn_register_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String written_username = username.getText().toString();
                String written_email = email.getText().toString();
                String written_password = password.getText().toString();

                if (TextUtils.isEmpty(written_username) || TextUtils.isEmpty(written_email) || TextUtils.isEmpty(written_password)) {
                    Toast.makeText(RegisterActivity.this, "Provide the required Information", Toast.LENGTH_LONG).show();
                } else if (written_password.length() < 6 ) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least  6 characters", Toast.LENGTH_LONG).show();
                } else {
                    register(written_username, written_email, written_password);
                }
            }
        });
    }

    private void register(final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashmap = new HashMap<>();
                            hashmap.put("id", userid);
                            hashmap.put("username", username);
                            hashmap.put("imageURL", "default");

                            reference.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "You Can't SignUp with " +
                                    "this email or password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
