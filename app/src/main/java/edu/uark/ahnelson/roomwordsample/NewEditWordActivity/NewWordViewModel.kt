package edu.uark.ahnelson.roomwordsample.NewEditWordActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import edu.uark.ahnelson.roomwordsample.Model.Word
import edu.uark.ahnelson.roomwordsample.Model.WordRepository
import kotlinx.coroutines.coroutineScope

class NewWordViewModel(private val repository: WordRepository, private val id:Int) : ViewModel() {

    var curWord: LiveData<Word> = repository.getWord(id).asLiveData()
    private var selectedRecurrence: String = " "

    fun updateId(id:Int){
        curWord = repository.getWord(id).asLiveData()
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    suspend fun insert(word: Word){
        coroutineScope {
            repository.insert(word)
            word.recurrence = selectedRecurrence
        }
    }

    /**
     * Launching a new coroutine to Update the data in a non-blocking way
     */
    suspend fun update(word: Word) {
        coroutineScope {
            repository.update(word)
            word.recurrence = selectedRecurrence
        }
    }
}

class NewWordViewModelFactory(private val repository: WordRepository,private val id:Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewWordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewWordViewModel(repository,id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
