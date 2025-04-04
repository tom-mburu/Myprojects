package com.example.mytracker;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link signup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class signup extends Fragment {
private TextInputLayout usernamecontainer;
private TextInputEditText username;
private TextInputLayout emailContainer;
private TextInputEditText email;
private TextInputLayout passwordContainer;
private TextInputEditText password;
private TextInputLayout confirmPasswordContainer;
private TextInputEditText confirmPassword;
private TextView login;
private AppCompatButton signUp;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public signup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment signup.
     */
    // TODO: Rename and change types and number of parameters
    public static signup newInstance(String param1, String param2) {
        signup fragment = new signup();
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
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernamecontainer=view.findViewById(R.id.usernameContainer);
        username=view.findViewById(R.id.username);
        emailContainer=view.findViewById(R.id.emailcontainer);
        email=view.findViewById(R.id.email);
        passwordContainer=view.findViewById(R.id.passwordContainer);
        password=view.findViewById(R.id.password);
        confirmPasswordContainer=view.findViewById(R.id.confirmpasswordContainer);
        confirmPassword=view.findViewById(R.id.confirmPassword);
        signUp=view.findViewById(R.id.signUp);
        login=view.findViewById(R.id.login);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //register the user
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Username="";
                String Password="";
                String Email="";
                String ConfirmPassword="";
                if(TextUtils.isEmpty(username.getText().toString().trim())){
                    usernamecontainer.setError("please enter username");
                    usernamecontainer.requestFocus();
                    return;
                }else{
                    Username=username.getText().toString().trim();
                }
                if(TextUtils.isEmpty(email.getText().toString().trim())){
                    emailContainer.setError("please enter Email");
                    emailContainer.requestFocus();
                    return;
                }else{
                    if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()){
                        Email=email.getText().toString().trim();
                    }else{
                        emailContainer.setError("Invalid Email address");
                        emailContainer.requestFocus();
                        return;
                    }
                }
                if(TextUtils.isEmpty(password.getText().toString().trim())){
                    passwordContainer.setError("please enter password");
                    passwordContainer.requestFocus();
                    return;
                }else{
                    Password=password.getText().toString().trim();
                }
                if(TextUtils.isEmpty(confirmPassword.getText().toString().trim())){
                    confirmPasswordContainer.setError("please re enter password");
                    confirmPasswordContainer.requestFocus();
                    return;
                }
                if(Password.contains(ConfirmPassword)&&!Username.isEmpty()&&!Email.isEmpty()){
                    //register from here
                    String finalUsername = Username;
                    ProgressDialog accountCreatedialog=new ProgressDialog(getActivity());
                    accountCreatedialog.setTitle("create account");
                    accountCreatedialog.setMessage("creating account for you ...please wait");
                    accountCreatedialog.show();
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(
                                    Email,Password
                            ).addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            accountCreatedialog.hide();
                                          if(task.isSuccessful()){
                                             //set username
                                             FirebaseAuth.getInstance()
                                                     .getCurrentUser().updateProfile(
                                                             new UserProfileChangeRequest.Builder()
                                                                     .setDisplayName(finalUsername)
                                                                     .build());
                                              Toast.makeText(getActivity(),"Account created successfully"
                                              ,Toast.LENGTH_LONG).show();
                                              getActivity().getSupportFragmentManager()
                                                      .beginTransaction().replace(R.id.fragmentContainer,
                                                              signin.newInstance("xyz","xyz"))
                                                      .commit();

                                          }else {
                                              Toast.makeText(getActivity(),"failed creating account ",Toast.LENGTH_LONG).show();
                                          }
                                        }
                                    }
                            );

                }else{
                    Snackbar.make(getView()," password are not matching ",Snackbar.LENGTH_INDEFINITE).setAction("Oky", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }

        };


        });
        login.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         getActivity().getSupportFragmentManager()
                                                 .beginTransaction()
                                                 .replace(R.id.loginregisterContainer,signin.newInstance("xyz","xyz"))
                                                 .commit();
                                     }
                                 }
        );

}
}