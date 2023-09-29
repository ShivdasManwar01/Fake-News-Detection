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

public class SignIn extends AppCompatActivity {
    private EditText email,password,re_enterPassword;
    private TextView warning;
    private Button signIn;
    private TextView logIn;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate()) {
                    pd = new ProgressDialog(SignIn.this);
                    pd.setMessage("Please wait...");
                    pd.setTitle("Sign in");
                    register(email.getText().toString(), password.getText().toString());
                    clearScreen();
                    pd.dismiss();
                }

            }
        });
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this,LogIn.class));
            }
        });
    }

    public void register(String email,String password){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(SignIn.this,DashBoard.class));
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SignIn.this, "Registration failed. Try with another email.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean validate(){
        String pattern="^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if(email.getText().toString().equals("")||password.getText().toString().equals("")|| re_enterPassword.getText().toString().equals("")){

            warning.setText("Plese fill all the fields");
            warning.setVisibility(View.VISIBLE);
            return false;
        }
        else
            warning.setVisibility(View.GONE);
        if(!email.getText().toString().matches(pattern)){
            Toast.makeText(this, "Incorrect email id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.getText().toString().length()<6){
            Toast.makeText(this, "Password should be at least 6 character long", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.getText().toString().equals(re_enterPassword.getText().toString())){
            warning.setText("Passwords did not match");
           return false;
        }
        else
            warning.setVisibility(View.GONE);
        return true;
    }
    public void clearScreen(){
        email.setText("");
        password.setText("");
        re_enterPassword.setText("");
    }
    public void init(){
        signIn=findViewById(R.id.sign_in);
        logIn=findViewById(R.id.log_in);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        re_enterPassword=findViewById(R.id.re_password);
        warning=findViewById(R.id.warning);
    }
}