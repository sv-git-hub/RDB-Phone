package com.mistywillow.researchdb.researchdb.entities;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.*;
import com.mistywillow.researchdb.FileTypeConverter;

@Entity(tableName = "File")
public class Files implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "FileID")
    private int fileID;

    @ColumnInfo(name = "FileName")
    private String fileName;

    @TypeConverters(FileTypeConverter.class)
    @ColumnInfo(name = "FileData", typeAffinity = ColumnInfo.BLOB)
    private byte[] fileData;

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    public Files(int fileID, String fileName, byte[] fileData){
        this.fileID = fileID;
        this.fileName = fileName;
        this.fileData = fileData;
    }

    public Files(Parcel in){
        this.fileID = in.readInt();
        this.fileName = in.readString();
        this.fileData = in.createByteArray();
    }

    public static final Creator<Files> CREATOR = new Creator<Files>() {
        @Override
        public Files createFromParcel(Parcel in) {
            return new Files(in);
        }

        @Override
        public Files[] newArray(int size) {
            return new Files[size];
        }
    };

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID){ this.fileID = fileID; }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {return fileData;}
    //public String getFileData() {return fileData;}

    public void setFileData(byte[] fileData) {this.fileData = fileData;}
    //public void setFileData(String fileData) {this.fileData = fileData;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(fileID);
        dest.writeString(fileName);
        // added getBytes(StandardCharsets.UTF_8)
        dest.writeByteArray(fileData);
    }
}
