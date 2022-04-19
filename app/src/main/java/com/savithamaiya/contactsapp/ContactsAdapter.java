package com.savithamaiya.contactsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.savithamaiya.contactsapp.model.Contact;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private Context context;
    private List<Contact> contacts;

    SharedPreferences sharedPreferences;

    public ContactsAdapter(Context context,List<Contact> contacts){
        this.context = context;
        this.contacts = contacts;
        sharedPreferences = this.context.getSharedPreferences("ContactPrefs",Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.contact_list,parent,false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.getNameTextView().setText(contact.getFirstName()+" "+contact.getLastName());
        holder.getContactNumberTextView().setText(contact.getContactNumber());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView contactNumberTextView;
        private ImageView contactMenuView;

        private Gson gson = new GsonBuilder().create();

        //Setting the list of contacts in the home screen
        public ContactsViewHolder(@NonNull View view) {
            super(view);
            nameTextView = view.findViewById(R.id.contactNameView);
            contactNumberTextView = view.findViewById(R.id.numberView);
            contactMenuView = view.findViewById(R.id.dropMenuIcon);

            contactMenuView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buildPopUp(view);
                }
            });
        }

        //Showing popup to editing and deleting contact info
        private void buildPopUp(View view) {
            Contact contact = contacts.get(getAdapterPosition());
            PopupMenu dropMenu = new PopupMenu(context, view);
            dropMenu.getMenuInflater().inflate(R.menu.contact_menu, dropMenu.getMenu());
            dropMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    return popUpMenuActions(menuItem, contact);
                }
            });
            dropMenu.show();
            try {
                Field popup = PopupMenu.class.getDeclaredField("mPopup");
                popup.setAccessible(true);
                Object menu = popup.get(dropMenu);
                menu.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(menu, true);
            } catch (Exception e) {
            }

        }

        private boolean popUpMenuActions(MenuItem menuItem, Contact contact) {
            switch (menuItem.getItemId()) {
                case R.id.editContact:
                    View editContactView = LayoutInflater.from(context).inflate(R.layout.add_contact, null, false);

                    EditText firstName = editContactView.findViewById(R.id.firstNameField);
                    EditText lastName = editContactView.findViewById(R.id.lastNameField);
                    EditText contactNumber = editContactView.findViewById(R.id.numberField);
                    EditText email = editContactView.findViewById(R.id.emailField);
                    TextView contactFormLabel = editContactView.findViewById(R.id.contactFormLabel);

                    contactFormLabel.setText("Update Contact");
                    firstName.setText(contact.getFirstName());
                    lastName.setText(contact.getLastName());
                    contactNumber.setText(contact.getContactNumber());
                    email.setText(contact.getEmail());

                    AlertDialog.Builder editContactDialog = new AlertDialog.Builder(context);
                    editContactDialog.setView(editContactView);
                    editContactDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            contact.setFirstName(firstName.getText().toString());
                            contact.setLastName(lastName.getText().toString());
                            contact.setContactNumber(contactNumber.getText().toString());
                            contact.setEmail(email.getText().toString());
                            Collections.sort(contacts);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("contacts", gson.toJson(contacts));
                            editor.commit();
                            notifyDataSetChanged();
                            showToast("Updated Contact");
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showToast("Update Cancelled");
                            dialogInterface.dismiss();
                        }
                    }).create().show();
                    showToast("Edit contact");
                    return true;
                case R.id.deleteContact:
                    AlertDialog.Builder deleteContactDialog = new AlertDialog.Builder(context);
                    deleteContactDialog.setTitle("Delete Contact")
                            .setIcon(R.drawable.ic_warning_24)
                            .setMessage("Are you sure to delete this contact ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    contacts.remove(getAdapterPosition());
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("contacts", gson.toJson(contacts));
                                    editor.commit();
                                    notifyDataSetChanged();
                                    showToast("Deleted Contact");
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showToast("Cancelled Deletion");
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                    return true;
                default:
                    return false;
            }
        }

        private void showToast(String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public void setNameTextView(TextView nameTextView) {
            this.nameTextView = nameTextView;
        }

        public TextView getContactNumberTextView() {
            return contactNumberTextView;
        }

        public void setContactNumberTextView(TextView contactNumberTextView) {
            this.contactNumberTextView = contactNumberTextView;
        }
    }
}
