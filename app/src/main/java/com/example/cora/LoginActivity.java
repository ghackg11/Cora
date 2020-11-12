package com.example.cora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    EditText emailEditText;
    EditText passwordEditText;
    ImageView logoImage;
    Button goButton;
    TextView text,text2;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_login);

        emailLayout = findViewById(R.id.email_layout);
        emailEditText = emailLayout.getEditText();

        passwordLayout = findViewById(R.id.password_layout);
        passwordEditText = passwordLayout.getEditText();

        goButton = findViewById(R.id.go_button_login);
        logoImage = findViewById(R.id.logo_login_activity);

        text = findViewById(R.id.text);
        text2 = findViewById(R.id.text2);

        mAuth = FirebaseAuth.getInstance();

    }

    public boolean validateEmail(){

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String email = emailEditText.getText().toString().trim();

        if(email.matches(emailPattern) && email.length()>0){
            emailLayout.setError(null);
        }
        else emailLayout.setError("Invalid Email");

            emailEditText.addTextChangedListener(new TextWatcher() {
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
        return emailLayout.getError() == null;

    }


    public boolean validatePassword(){

        final String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        final String password = passwordEditText.getText().toString().trim();

        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(password.matches(regex) && !password.equals("")) passwordLayout.setError(null);
                else passwordLayout.setError("Invalid Password");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(password.matches(regex) && !password.equals("")){
            passwordLayout.setError(null);
            return true;
        }
        else {
            passwordLayout.setError("Invalid Password");
            return false;
        }



    }
    public void login(View view){

        if(validateEmail() && validatePassword()) {
            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Log In successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Log In failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }


    }
    public void goToSignUpPage(View view){


        Pair[] pairs = new Pair[6];

        pairs[0] = new Pair<View,String>(logoImage,"logo_image");
        pairs[1] = new Pair<View,String>(goButton,"go_button_transition");
        pairs[2] = new Pair<View,String>(emailLayout,"email_transition");
        pairs[3] = new Pair<View,String>(passwordLayout,"password_transition");
        pairs[4] = new Pair<View,String>(text,"text_transition");
        pairs[5] = new Pair<View,String>(text2,"text2_transition");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);

        startActivity(new Intent(LoginActivity.this, SignUpActivity.class), options.toBundle());

    }
}