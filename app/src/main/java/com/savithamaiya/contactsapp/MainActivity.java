package com.savithamaiya.contacts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.savithamaiya.contacts.model.Contact;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addContactBtn;
    private RecyclerView recyclerView;
    private List<Contact> contacts;
    private ContactsAdapter contactsAdapter;

    private SharedPreferences sharedPreferences;

    private Gson gson = new GsonBuilder().create();
    private Type type = new TypeToken<ArrayList<Contact>>() {}.getType();

    private static final String SHARED_PREF_NAME = "ContactPrefs";
    private static final String SHARED_PREF_KEY = "contacts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addContactBtn = findViewById(R.id.addContactBtn);
        recyclerView = findViewById(R.id.mainRecyclerView);
        contacts = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(this,contacts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactsAdapter);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        String contactList = sharedPreferences.getString(SHARED_PREF_KEY,"");

        if(!contactList.isEmpty()){
            contacts.addAll(gson.fromJson(contactList,type));
        }

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContactInfo();
            }
        });
    }

    private void addContactInfo() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View addContactView = layoutInflater.inflate(R.layout.add_contact,null,false);

        EditText firstNameField = addContactView.findViewById(R.id.firstNameField);
        EditText lastNameField = addContactView.findViewById(R.id.lastNameField);
        EditText contactNumberField = addContactView.findViewById(R.id.numberField);
        EditText emailField = addContactView.findViewById(R.id.emailField);

        AlertDialog.Builder addDialog = new AlertDialog.Builder(this);
        addDialog.setView(addContactView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String firstName = firstNameField.getText().toString();
                        String lastName = lastNameField.getText().toString();
                        String contactNumber = contactNumberField.getText().toString();
                        String email = emailField.getText().toString();
                        contacts.add(new Contact(firstName, lastName, contactNumber, email));
                        Collections.sort(contacts);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("contacts",gson.toJson(contacts));
                        editor.commit();
                        contactsAdapter.notifyDataSetChanged();
                        showToast("Contact Added");
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                showToast("Cancel");
            }
        })
                .create()
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}