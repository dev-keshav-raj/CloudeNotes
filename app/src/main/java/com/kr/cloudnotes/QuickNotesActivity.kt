package com.kr.cloudnotes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class QuickNotesActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var edtQuickTitle: EditText
    private lateinit var edtQuickContent: EditText
    private lateinit var btnSaveQuick: Button
    private lateinit var txtQuickList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_notes)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("quick_notes")

        // Initialize views
        edtQuickTitle = findViewById(R.id.edtQuickTitle)
        edtQuickContent = findViewById(R.id.edtQuickContent)
        btnSaveQuick = findViewById(R.id.btnSaveQuick)
        txtQuickList = findViewById(R.id.txtQuickList)

        // Save button click
        btnSaveQuick.setOnClickListener {
            saveQuickNote()
        }

        // Fetch and display live notes
        fetchQuickNotes()
    }

    private fun saveQuickNote() {
        val title = edtQuickTitle.text.toString().trim()
        val content = edtQuickContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please enter both title and content", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val noteId = database.push().key ?: return

        val quickNote = mapOf(
            "title" to title,
            "content" to content,
            "user" to user.uid
        )

        database.child(noteId).setValue(quickNote)
            .addOnSuccessListener {
                Toast.makeText(this, "Quick note saved!", Toast.LENGTH_SHORT).show()
                edtQuickTitle.text.clear()
                edtQuickContent.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchQuickNotes() {
        txtQuickList.text = "Loading notes..."

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val builder = StringBuilder()
                for (noteSnapshot in snapshot.children) {
                    val title = noteSnapshot.child("title").getValue(String::class.java)
                    val content = noteSnapshot.child("content").getValue(String::class.java)
                    val user = noteSnapshot.child("user").getValue(String::class.java)
                    builder.append("📌 $title\n$content\n👤 by: $user\n\n")
                }
                txtQuickList.text = builder.toString().ifEmpty { "No quick notes yet." }
            }

            override fun onCancelled(error: DatabaseError) {
                txtQuickList.text = "Error loading notes."
            }
        })
    }
}
