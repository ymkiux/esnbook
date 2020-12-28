package wooyun.esnb.room

import android.arch.persistence.room.Room
import android.content.Context

class NoteController(private val context: Context) {
    private var dataDao: NoteDao? = null
    private lateinit var database: NoteDatabase

    fun init(): NoteController {
        database = Room.databaseBuilder(context, NoteDatabase::class.java, "Note.db")
                .allowMainThreadQueries()
                .build()
        dataDao = database.dao
        return this
    }

    fun getAll(): MutableList<Note>? {
        return dataDao?.all
    }

    fun delete(id: Note) {
        dataDao?.delete(id)
        database.close()
    }

    fun add(note: Note) {
        dataDao?.insert(note)
        database.close()
    }

    fun update(notes: Note, name: String, context: String) {
        val note = notes
        note.title = name
        note.context = context
        dataDao?.update(note)
        database.close()
    }
}