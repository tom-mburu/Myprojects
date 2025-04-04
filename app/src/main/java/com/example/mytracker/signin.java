package com.example.mytracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link signin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class signin extends Fragment {
    private TextView forgotPassword;
    private TextInputLayout loginEmailContainer;
    private TextInputEditText loginEmail;
    private TextInputLayout loginPasswordContainer;
    private TextInputEditText loginPassword;
    private AppCompatButton loginBtn;
    private TextView signUp;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public signin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment signin.
     */
    // TODO: Rename and change types and number of parameters
    public static signin newInstance(String param1, String param2) {
        signin fragment = new signin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginEmailContainer=view.findViewById(R.id.loginEmailContainer);
        loginEmail=view.findViewById(R.id.loginEmail);
        loginPassword=view.findViewById(R.id.loginPassword);
        loginBtn=view.findViewById(R.id.loginBtn);
        signUp=view.findViewById(R.id.signUp);
        forgotPassword=view.findViewById(R.id.forgotpassword);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //forgot password implementations

                FirebaseAuth auth= FirebaseAuth.getInstance();
                FirebaseUser user=auth.getCurrentUser();
                EditText resetEmail=new EditText(getActivity());
                resetEmail.setPadding(10,0,0,15);

                final String[] email = {null};
                AlertDialog.Builder getResetEmailDialog=new AlertDialog.Builder(getActivity())
                        .setView(resetEmail)
                                .setCancelable(true)
                                        .setTitle("Enter email to receive password reset email " )
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                     email[0] =resetEmail.getText().toString();
                                                        if(TextUtils.isEmpty(email[0])){
                                                            Toast.makeText(getActivity(),"Email address needed to receive reset password email",Toast.LENGTH_LONG).show();
                                                        }else{
                                                            ProgressDialog resetPasswordProgress=new ProgressDialog(getActivity());
                                                            resetPasswordProgress.setTitle("sending reset email");
                                                            resetPasswordProgress.setMessage("sending reset password email....please wait");
                                                            resetPasswordProgress.show();
                                                            auth.sendPasswordResetEmail(email[0])
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                resetPasswordProgress.hide();
                                                                                Toast.makeText(getActivity(),"Password reset email sent to provided email...please check ",Toast.LENGTH_LONG).show();
                                                                            } else{
                                                                                Toast.makeText(getActivity(),"failed reseting password..try again later",Toast.LENGTH_LONG).show();
                                                                                resetPasswordProgress.hide();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog  alert=getResetEmailDialog.create();
                alert.show();

            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email="";
                String password="";
                if(TextUtils.isEmpty(loginEmail.getText().toString().trim())){
                    loginEmailContainer.setError("please enter email ");
                    loginEmailContainer.requestFocus();
                    return;
                }else{
                    if(Patterns.EMAIL_ADDRESS.matcher(loginEmail.getText().toString().trim()).matches()){
                       email=loginEmail.getText().toString().trim();
                    }else{
                        loginEmailContainer.setError("please enter valid email");
                        loginEmailContainer.requestFocus();
                        return;
                    }
                }
                if(loginPassword.getText().toString().trim().isEmpty()){
                    loginPasswordContainer.setError("please enter password");
                    loginPasswordContainer.requestFocus();
                    return;
                }else{
                    password=loginPassword.getText().toString().trim();
                }
                if(!email.isEmpty()&&!password.isEmpty()){
                    ProgressDialog loginDialog=new ProgressDialog(getActivity());
                    loginDialog.setMessage("login you to your account ... please wait");
                    loginDialog.setTitle("login to account");
                    loginDialog.show();
                    //login the user
                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    loginDialog.hide();
                                    if(task.isSuccessful()){
                                   getActivity().startActivity(new Intent(getActivity(),MainActivity.class));
                                    }else{
                                        Toast.makeText(getActivity(),"failed to login",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.loginregisterContainer,signup.newInstance("xyz","xyz"))
                        .commit();
            }
        });
    }
}