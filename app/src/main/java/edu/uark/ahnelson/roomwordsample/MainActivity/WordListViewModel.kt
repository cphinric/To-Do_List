package edu.uark.ahnelson.roomwordsample.MainActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import edu.uark.ahnelson.roomwordsample.Model.Word
import edu.uark.ahnelson.roomwordsample.Model.WordRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class WordListViewModel(private val repository: WordRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<Word>> = repository.allWords.asLiveData()

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.delete(word)
        }
    }
}

class WordListViewModelFactory(private val repository: WordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
