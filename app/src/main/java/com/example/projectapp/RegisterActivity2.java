package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.projectapp.databinding.ActivityMainBinding;
import com.example.projectapp.databinding.ActivityRegister2Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity2 extends AppCompatActivity {

    private ActivityRegister2Binding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegister2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth= FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });


    }
    private String name ="", email="",password="";
    private void validateData() {

        name = binding.nameEt.getText().toString().trim();
        email= binding.emailIt.getText().toString().trim();
        password = binding.passwordIt.getText().toString().trim();
        String cpassword = binding.confirmpasswordIt.getText().toString().trim();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"Enter your name",Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this,"Invalid email pattern",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Enter password",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cpassword))
        {
            Toast.makeText(this,"confirm password",Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(cpassword))
        {
            Toast.makeText(this,"password mismatch",Toast.LENGTH_SHORT).show();
        }
        else{
            createUserAccount();
        }

    }

    private void createUserAccount() {
        progressDialog.setMessage("Creating Account");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult)
            {
                updateUserInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity2.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateUserInfo()
    {
        progressDialog.setMessage("Saving User Info");
        long timestamp = System.currentTimeMillis();

        String uid = firebaseAuth.getUid();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email",email);
        hashMap.put("name",name);
        hashMap.put("profileImage","");
        hashMap.put("userType","user");
        hashMap.put("timestamp",timestamp);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity2.this,"Account Created",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity2.this,DashboardUserActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity2.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}