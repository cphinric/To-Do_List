package edu.uark.ahnelson.roomwordsample.Model

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Word::class), version = 3, exportSchema = false)
abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("Database","Here1")

            INSTANCE?.let { database ->
                scope.launch {
                    val wordDao = database.wordDao()

                    // Delete all content here.
                    wordDao.deleteAll()

                    // Add sample words.
                    var word = Word(null,"Spaghetti",1, "Go to the store and buy spaghetti for mom", "2023-10-20", "14:07")
                    wordDao.insert(word)
                    word = Word(null,"Chicken",2,"Make sure to check on the chickens and see if they laid any eggs", "2023-10-17", "8:00")
                    wordDao.insert(word)

                    // My Entries
                    word = Word(null,"Homework 2",3,"Mobile Programming homework 2 (To-Do list)", "2023-10-13", "23:59")
                    wordDao.insert(word)

                    word = Word(null, "Haircut", 4, "Haircut at shag salon with Lisa", "2023-18-23", "14:00")
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): WordRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database"
                )
                    .addMigrations(migration1to2, migration2to3) // Add the migration here
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
        private val migration1to2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Recreate the table with the correct column order
                database.execSQL("CREATE TABLE word_table_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, word TEXT NOT NULL, quantity INTEGER NOT NULL, description TEXT)")
                // Copy the data from the old table to the new table
                database.execSQL("INSERT INTO word_table_new (id, word, quantity, description) SELECT id, word, quantity, description FROM word_table")
                // Remove the old table
                database.execSQL("DROP TABLE word_table")
                // Change the table name to the correct name
                database.execSQL("ALTER TABLE word_table_new RENAME TO word_table")
            }
        }
        private val migration2to3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the updated schema
                database.execSQL("CREATE TABLE word_table_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, word TEXT NOT NULL, quantity INTEGER NOT NULL, description TEXT, date TEXT NOT NULL, time TEXT NOT NULL)")

                // Copy the data from the old table to the new table
                database.execSQL("INSERT INTO word_table_new (id, word, quantity, description, date, time) SELECT id, word, quantity, description, '', '' FROM word_table")

                // Remove the old table
                database.execSQL("DROP TABLE word_table")

                // Change the table name to the correct name
                database.execSQL("ALTER TABLE word_table_new RENAME TO word_table")
            }
        }
    }
}

