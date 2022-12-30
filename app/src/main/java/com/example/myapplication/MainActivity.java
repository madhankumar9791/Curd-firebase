package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextNumber;
    Button buttonAddUser;
    ListView listViewUsers;
    List<User> Users;
    DatabaseReference databaseReference;
 // madhankumar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        initListner();
    }


    private void findViews() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextNumber = (EditText) findViewById(R.id.editTextNumber);
        listViewUsers = (ListView) findViewById(R.id.listViewUsers);
        buttonAddUser = (Button) findViewById(R.id.buttonAddUser);
        Users = new ArrayList<>();
    }

    private void initListner() {
        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addUser();
            }
        });


        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User User = Users.get(i);
                CallUpdateAndDeleteDialog(User.getUserid(), User.getUsername(), User.getUseremail(), User.getUsermobileno());


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User User = postSnapshot.getValue(User.class);
                    Users.add(User);
                }
                UserList UserAdapter = new UserList(MainActivity.this, Users);
                listViewUsers.setAdapter(UserAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void CallUpdateAndDeleteDialog(final String userid, String username, final String email, String monumber) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText updateTextname = (EditText) dialogView.findViewById(R.id.updateTextname);
        final EditText updateTextemail = (EditText) dialogView.findViewById(R.id.updateTextemail);
        final EditText updateTextmobileno = (EditText) dialogView.findViewById(R.id.updateTextmobileno);
        updateTextname.setText(username);
        updateTextemail.setText(email);
        updateTextmobileno.setText(monumber);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateUser);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteUser);
        dialogBuilder.setTitle(username);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = updateTextname.getText().toString().trim();
                String email = updateTextemail.getText().toString().trim();
                String mobilenumber = updateTextmobileno.getText().toString().trim();

                if (!TextUtils.isEmpty(name)) {
                    if (!TextUtils.isEmpty(email)) {
                        if (!TextUtils.isEmpty(mobilenumber)) {
                            updateUser(userid, name, email, mobilenumber);
                            b.dismiss();
                        }
                    }
                }

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(userid);
                b.dismiss();
            }
        });
    }

    private boolean updateUser(String id, String name, String email, String mobilenumber) {
        DatabaseReference UpdateReference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        User User = new User(id, name, email, mobilenumber);
        UpdateReference.setValue(User);
        Toast.makeText(getApplicationContext(), "User Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteUser(String id) {
        DatabaseReference DeleteReference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        DeleteReference.removeValue();
        Toast.makeText(getApplicationContext(), "User Deleted", Toast.LENGTH_LONG).show();
        return true;
    }


    private void addUser() {

        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String mobilenumber = editTextNumber.getText().toString().trim();


        if (!TextUtils.isEmpty(name)) {
            if (!TextUtils.isEmpty(email)) {
                if (!TextUtils.isEmpty(mobilenumber)) {

                    String id = databaseReference.push().getKey();
                    User User = new User(id, name, email, mobilenumber);
                    databaseReference.child(id).setValue(User);

                    editTextName.setText("");
                    editTextNumber.setText("");
                    editTextEmail.setText("");
                    Toast.makeText(this, "User added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Please enter a mobilenumber", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Please enter a Email", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }
}