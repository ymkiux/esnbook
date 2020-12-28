package wooyun.esnb.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note... notes);

    @Update
    int update(Note... notes);

    @Delete
    void delete(Note... notes);

    @Query("DELETE FROM Note")
    void delete();


    @Query("SELECT * FROM Note ORDER BY ID DESC")
    List<Note> getAll();


}