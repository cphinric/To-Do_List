package edu.uark.ahnelson.roomwordsample.NewEditWordActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import edu.uark.ahnelson.roomwordsample.Model.Word
import edu.uark.ahnelson.roomwordsample.R
import edu.uark.ahnelson.roomwordsample.WordsApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.Calendar

const val EXTRA_ID:String = "edu.uark.ahnelson.NewWordActivity.EXTRA_ID"
class NewWordActivity : AppCompatActivity() {

    private lateinit var editWordView: EditText
    private lateinit var editDescriptionView: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText

    private val calendar: Calendar = Calendar.getInstance()

    private val newWordViewModel: NewWordViewModel by viewModels {
        NewWordViewModelFactory((application as WordsApplication).repository,-1)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)
        editWordView = findViewById(R.id.edit_word2)
        editDescriptionView = findViewById(R.id.description)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)

        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            newWordViewModel.updateId(id)
            // Retrieve the word data including the description from your database or repository
            newWordViewModel.curWord.observe(this) { word ->
                word?.let {
                    editWordView.setText(word.word)
                    editDescriptionView.setText(word.description) // Set the description in the editDescriptionView
                    editTextDate.setText(word.date)
                    editTextTime.setText(word.time)
                }
            }
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            CoroutineScope(SupervisorJob()).launch {
                if (id == -1) {
                    newWordViewModel.insert(
                        Word(null, editWordView.text.toString(), 0, editDescriptionView.text.toString(), editTextDate.text.toString(), editTextTime.text.toString())
                    )
                } else {
                    val updatedWord = newWordViewModel.curWord.value
                    if (updatedWord != null) {
                        updatedWord.word = editWordView.text.toString() // Update the data with the selected text
                        updatedWord.description = editDescriptionView.text.toString() // Update the data with the selected description
                        updatedWord.date = editTextDate.text.toString() // Update the date with the selected date
                        updatedWord.time = editTextTime.text.toString() // Update the data with the selected time
                        newWordViewModel.update(updatedWord)
                    }
                }
            }

            setResult(RESULT_OK)
            finish()
        }

        // Code to show DatePickerDialog
        editTextDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(year, monthOfYear, dayOfMonth)
                    editTextDate.setText(
                        String.format(
                            "%d-%02d-%02d",
                            year,
                            monthOfYear + 1,
                            dayOfMonth
                        )
                    )
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        // Code to show TimePickerDialog
        editTextTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    editTextTime.setText(String.format("%02d:%02d", hourOfDay, minute))
                },
                hour,
                minute,
                true // 24-hour format
            )

            timePickerDialog.show()
        }
    }
}
