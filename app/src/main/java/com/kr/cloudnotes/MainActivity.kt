package com.kr.cloudnotes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kr.cloudnotes.adapters.NoteAdapter
import com.kr.cloudnotes.models.Note

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private val notesList = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerViewNotes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NoteAdapter(notesList)
        recyclerView.adapter = adapter

        // Toolbar setup
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.title = "CloudNotes"
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_signout -> {
                    auth.signOut()
                    Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                R.id.action_about -> {
                    Toast.makeText(
                        this,
                        "CloudNotes v1.0 — A Firebase-powered Notes App by Keshav Raj",
                        Toast.LENGTH_LONG
                    ).show()
                    true
                }
                else -> false
            }
        }

        // Floating Action Buttons
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddNote)
        val fabQuick = findViewById<FloatingActionButton>(R.id.quickadd)

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        fabQuick.setOnClickListener {
            startActivity(Intent(this, QuickNotesActivity::class.java))
        }

        // Load Notes from Firestore
        loadNotes()
    }

    private fun loadNotes() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).collection("notes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading notes", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    notesList.clear()
                    for (doc in snapshot.documents) {
                        val note = doc.toObject(Note::class.java)
                        if (note != null) notesList.add(note)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}
