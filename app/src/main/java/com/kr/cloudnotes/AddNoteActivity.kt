package com.kr.cloudnotes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kr.cloudnotes.models.Note

class AddNoteActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val title = findViewById<EditText>(R.id.etTitle)
        val content = findViewById<EditText>(R.id.etContent)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val note = Note(title.text.toString(), content.text.toString())
            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            firestore.collection("users").document(uid).collection("notes")
                .add(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }
}
