package edu.uark.ahnelson.roomwordsample.NewEditWordActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.viewModels
import edu.uark.ahnelson.roomwordsample.Model.Word
import edu.uark.ahnelson.roomwordsample.R
import edu.uark.ahnelson.roomwordsample.WordsApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import java.util.Calendar

const val EXTRA_ID: String = "edu.uark.ahnelson.NewWordActivity.EXTRA_ID"

class NewWordActivity : AppCompatActivity() {
    private lateinit var editWordView: EditText
    private lateinit var editDescriptionView: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var recurrenceSpinner: Spinner
    private lateinit var selectedRecurrence: String
    private lateinit var checkBoxCompleted: CheckBox

    private val calendar: Calendar = Calendar.getInstance()

    private val newWordViewModel: NewWordViewModel by viewModels {
        NewWordViewModelFactory((application as WordsApplication).repository, -1)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)

        editWordView = findViewById(R.id.edit_word2)
        editDescriptionView = findViewById(R.id.description)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        recurrenceSpinner = findViewById(R.id.recurrenceSpinner)
        checkBoxCompleted = findViewById(R.id.checkBox)

        val recurrenceOpt = arrayOf("Does Not Repeat", "Daily", "Weekly", "Monthly", "Yearly")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, recurrenceOpt)
        recurrenceSpinner.adapter = spinnerAdapter

        // Set up the OnItemSelectedListener for the spinner
        recurrenceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRecurrence = recurrenceOpt[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected (if needed)
            }
        }

        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            newWordViewModel.updateId(id)
            newWordViewModel.curWord.observe(this) { word ->
                word?.let {
                    editWordView.setText(word.word)
                    editDescriptionView.setText(word.description)
                    editTextDate.setText(word.date)
                    editTextTime.setText(word.time)

                    val recurrenceIndex = recurrenceOpt.indexOf(word.recurrence)
                    if (recurrenceIndex != -1) {
                        recurrenceSpinner.setSelection(recurrenceIndex)
                    }
                    checkBoxCompleted.isChecked = word?.done ?: false

                }
            }
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            CoroutineScope(SupervisorJob()).launch {
                if (id == -1) {
                    newWordViewModel.insert(
                        Word(
                            null,
                            editWordView.text.toString(),
                            0,
                            editDescriptionView.text.toString(),
                            editTextDate.text.toString(),
                            editTextTime.text.toString(),
                            selectedRecurrence,
                            checkBoxCompleted.isChecked //set completed status
                        )
                    )
                } else {
                    val updatedWord = newWordViewModel.curWord.value
                    if (updatedWord != null) {
                        updatedWord.word = editWordView.text.toString()
                        updatedWord.description = editDescriptionView.text.toString()
                        updatedWord.date = editTextDate.text.toString()
                        updatedWord.time = editTextTime.text.toString()
                        updatedWord.recurrence = recurrenceSpinner.selectedItem.toString()
                        updatedWord.done = checkBoxCompleted.isChecked // Update the completed status
                        newWordViewModel.update(updatedWord)
                    }
                }
            }

            setResult(RESULT_OK)
            finish()
        }

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
                true
            )

            timePickerDialog.show()
        }
    }
}