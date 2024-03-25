package com.example.dbdemo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dbdemo.db.BookInfoExt.COLUMN_AUTHOR
import com.example.dbdemo.db.BookInfoExt.COLUMN_ID
import com.example.dbdemo.db.BookInfoExt.COLUMN_ISBN
import com.example.dbdemo.db.BookInfoExt.COLUMN_PUBLISH_YEAR
import com.example.dbdemo.db.BookInfoExt.COLUMN_TITLE
import com.example.dbdemo.db.BookInfoExt.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class BookInfo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    var id: Int? = null,
    @ColumnInfo(name = COLUMN_TITLE, typeAffinity = ColumnInfo.TEXT)
    var title: String? = null,
    @ColumnInfo(name = COLUMN_AUTHOR, typeAffinity = ColumnInfo.TEXT)
    var author: String? = null,
    @ColumnInfo(name = COLUMN_PUBLISH_YEAR, typeAffinity = ColumnInfo.INTEGER)
    var publishYear: Int? = null,
    @ColumnInfo(name = COLUMN_ISBN, typeAffinity = ColumnInfo.TEXT)
    var isbn: String? = null
)