package com.example.fakenewsdetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {
    private Button logIn;
    private TextView signIn;
    private FirebaseAuth auth;
    private EditText email,password;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        init();

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    pd=new ProgressDialog(LogIn.this);
                    pd.setMessage("Please wait..");
                    pd.setTitle("Log in");

                    auth=FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LogIn.this,DashBoard.class));
                                pd.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LogIn.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this,SignIn.class));
            }
        });

    }

    public void init(){
        logIn=findViewById(R.id.log_in);
        signIn=findViewById(R.id.sign_in);
        email=findViewById(R.id.user_name);
        password=findViewById(R.id.password);
    }
    public boolean validate(){
        if(email.getText().toString().equals("")||password.getText().toString().equals("")){
            Toast.makeText(this, "Email or password cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
}