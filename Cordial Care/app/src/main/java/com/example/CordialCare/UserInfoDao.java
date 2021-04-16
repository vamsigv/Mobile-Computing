package com.example.CordialCare;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Query;

@Dao
public interface UserInfoDao {
    @Query("SELECT COUNT(*) FROM UserDetails UD")
    public int count();

    @Query("SELECT UD.* FROM UserDetails UD where UD.timestamp=(SELECT MAX(UD1.timestamp) FROM UserDetails UD1)")
    public UserDetails getLatestData();

    @Insert
    public long insert(UserDetails userDetails);

    @Update
    public int update(UserDetails userDetails);
}
