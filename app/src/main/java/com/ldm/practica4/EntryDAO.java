package com.ldm.practica4;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface EntryDAO {
    @Insert
    void insertEntry(Entry entry);

    @Delete
    void deleteEntry(Entry entry);

    @Query("SELECT * FROM entries ORDER BY timestamp DESC")
    List<Entry> getAllEntries();

    @Query("SELECT * FROM entries WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || :searchQuery || '%'")
    List<Entry> searchEntries(String searchQuery);

    @Query("SELECT * FROM entries WHERE isFavorite = 1 ORDER BY timestamp DESC")
    List<Entry> getFavoriteEntries();

    @Query("SELECT * FROM entries WHERE date(entryDate/1000, 'unixepoch') = date(:dateInMillis/1000, 'unixepoch')")
    List<Entry> getEntriesByDate(long dateInMillis);
}