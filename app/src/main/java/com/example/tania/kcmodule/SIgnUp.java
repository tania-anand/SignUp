package com.example.tania.kcmodule;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class SIgnUp extends AppCompatActivity implements View.OnClickListener
{
    EditText edemail;
    EditText edpassword;
    Button signup;
    TextView text;

    String email="";
    String password="";

    // for sign up activity create shared instance of FirebaseAuth
    private FirebaseAuth mAuth;

    // firebase Database
    private DatabaseReference mUsersDatabase;

    private static final String TAG = "EmailPassword";
    private static final String DATABASENAME="USER";

    boolean registerFlag=true;




    void initViews()
    {
        edemail = (EditText)findViewById(R.id.edittext_email);
        edpassword = (EditText)findViewById(R.id.edittext_password);

        signup = (Button)findViewById(R.id.buttonlogin);
        signup.setOnClickListener(this);

        text=(TextView)findViewById(R.id.textview_signIn);
        text.setOnClickListener(this);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v)
    {
        email = edemail.getText().toString().trim();
        password=edpassword.getText().toString().trim();

        if(validateFields())
        {
            if (v.getId() == R.id.buttonlogin)
            {
                if(registerFlag)

                    register();

                else
                    signIn();

            }

        }

        if (v.getId() == R.id.textview_signIn)
        {
            registerFlag=false;
            signup.setText("Sign In");

        }

    }

    void register()
    {
        initDialog(this);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            FirebaseUser current_user = mAuth.getCurrentUser();

                            String uid = current_user.getUid();

                            if(current_user!=null)
                            {


                                Log.d("error", "createUserWithEmail:success");
                                mUsersDatabase.child(DATABASENAME).child(uid).child("EMAIL").setValue(email);
                                mUsersDatabase.child(DATABASENAME).child(uid).child("PASSWORD").setValue(password);


                            }
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            dismissDialog();


                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                            clearfields();

                            dismissDialog();



                        }

                    }
                });
        // [END create_user_with_email]
    }


    void signIn()
    {
        initDialog(this);

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            dismissDialog();



                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_LONG).show();
                            clearfields();
                            dismissDialog();

                        }

                    }
                });

    }

    boolean validateFields()
    {

        boolean flag = true;


        if(email.equals(""))
        {
            edemail.setError("Enter Email Id");
            flag = false;
        }
        else
        {
            if(!email.contains("@") && !email.contains("."))
            {
                edemail.setError("Enter Valid Email");
                flag = false;
            }
        }


        if(password.equals(""))
        {
            edpassword.setError("Enter Password");
            flag = false;
        }

        return flag;
    }


    void clearfields()
    {
        edemail.setText("");
        edpassword.setText("");
    }


    static Dialog dialog;

    public static Dialog initDialog(Context context)
    {
        dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setCancelable(false);
        dialog.show();
        LottieAnimationView animationView = (LottieAnimationView) dialog.findViewById(R.id.animation_view);
        animationView.setAnimation("glow_loading.json");
        animationView.loop(true);
        animationView.setProgress(0.5f);
        animationView.playAnimation();
        return dialog;
    }

    public static void dismissDialog(){
        dialog.dismiss();
    }

}
