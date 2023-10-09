package edu.uark.ahnelson.roomwordsample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import edu.uark.ahnelson.roomwordsample.Model.Word
import edu.uark.ahnelson.roomwordsample.NewEditWordActivity.EXTRA_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(EXTRA_ID,-1)
        Log.d("MyReceiver","Broadcast Received $id")

        val repository = (context.applicationContext as WordsApplication).repository
        CoroutineScope(SupervisorJob()).launch {
            val word: Word = repository.getWordNotLive(id)
            Log.d("MyReceiver", "Word is ${word.word} with quantity ${word.quantity}")
        }

    }
}