package com.example.cora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    TextInputLayout userNameLayout;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    TextInputLayout conformPasswordLayout;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userNameLayout = findViewById(R.id.username_layout_signup);
        emailLayout = findViewById(R.id.email_layout_signup);
        passwordLayout = findViewById(R.id.password_layout_signup);
        conformPasswordLayout = findViewById(R.id.confirm_password_layout_signup);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    private boolean validateUsername(String username){

        userNameLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String username  = charSequence.toString();
                if(username.equals("")) userNameLayout.setError("Field can not be empty");
                else userNameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(username.equals("")) {
            userNameLayout.setError("Field can not be empty");
            return false;
        }
        else{
            userNameLayout.setError(null);
            return true;
        }

    }

    private boolean validateEmail(String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        emailLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String email = charSequence.toString();

                if(email.matches(emailPattern) && email.length()>0){
                    emailLayout.setError(null);
                }
                else emailLayout.setError("Invalid Email");

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //no body required
            }
        });
        if(email.matches(emailPattern) && email.length()>0){
            emailLayout.setError(null);
            return true;
        }
        else emailLayout.setError("Invalid Email");
        return false;
    }

    private boolean validatePassword(String password,String confirmPassowrd){

        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";



        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if(password.matches(regex) && !password.equals(confirmPassowrd)) {
            conformPasswordLayout.setError("Passwords don't match");
            return false;
        }
        else {
            conformPasswordLayout.setError(null);
            return true;
        }

    }

    public void register(View view){

        String username = userNameLayout.getEditText().getText().toString().trim();
        String email = emailLayout.getEditText().getText().toString().trim();
        String password = passwordLayout.getEditText().getText().toString().trim();
        String confirmPassword = conformPasswordLayout.getEditText().getText().toString().trim();

        if(validateUsername(username) && validateEmail(email) && validatePassword(password,confirmPassword)){

            //register here

             User user = new User(username,email, password, false);

            DatabaseReference myRef = database.getReference("users");

            myRef.child(user.username).setValue(user);



            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "User registration Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                        finish();
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, "User registration failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }





    }

    public void goToLoginPage(View view){

        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));


    }

}