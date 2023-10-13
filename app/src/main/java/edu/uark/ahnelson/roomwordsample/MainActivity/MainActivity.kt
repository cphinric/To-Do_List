package edu.uark.ahnelson.roomwordsample.MainActivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.uark.ahnelson.roomwordsample.Model.Word
import edu.uark.ahnelson.roomwordsample.NewEditWordActivity.EXTRA_ID
import edu.uark.ahnelson.roomwordsample.NewEditWordActivity.NewWordActivity
import edu.uark.ahnelson.roomwordsample.R
import edu.uark.ahnelson.roomwordsample.WordsApplication
import edu.uark.ahnelson.roomwordsample.MyReceiver
import edu.uark.ahnelson.roomwordsample.NotificationUtil

class MainActivity : AppCompatActivity() {

    val adapter = WordListAdapter(
        wordClicked = { word ->
            // Handle click event here
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            intent.putExtra(EXTRA_ID, word.id) // Pass any relevant data to the new activity
            startNewWordActivity.launch(intent)
        },
        wordDeleted = { word ->
            // Handle task deletion here
            wordListViewModel.deleteWord(word)
        }
    )


    private var notificationPermissionGranted = false
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                NotificationUtil().createNotificationChannel(this)
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }
    //This is our viewModel instance for the MainActivity class
    private val wordListViewModel: WordListViewModel by viewModels {
        WordListViewModelFactory((application as WordsApplication).repository)
    }
    //This is our ActivityResultContracts value that defines
    //the behavior of our application when the activity has finished.
    private val startNewWordActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode== Activity.RESULT_OK){
            //Note that all we are doing is logging that we completed
            //This means that the other activity is handling updates to the data
            Log.d("MainActivity","Completed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionGranted = true
            NotificationUtil().createNotificationChannel(this)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter  // Set the adapter property here

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        wordListViewModel.allWords.observe( this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let {
                adapter.submitList(it)
                if(it.isNotEmpty()) {
                    it[0].id?.let { it1 ->
                        NotificationUtil().createClickableNotification(
                            this,
                            it[0].word,
                            it[0].quantity.toString(),
                            Intent(this@MainActivity, NewWordActivity::class.java),
                            it1
                        )
                    }
                }

            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            startNewWordActivity.launch(intent)
        }
    }
}
