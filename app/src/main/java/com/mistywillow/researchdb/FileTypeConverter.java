package com.mistywillow.researchdb;

import androidx.room.TypeConverter;

public class FileTypeConverter {

    @TypeConverter
    public byte[] toFileBytes(String value){
            return value.getBytes();
    }
    @TypeConverter
    public String toFileString(byte[] bytes){
        return new String(bytes);
    }

}
