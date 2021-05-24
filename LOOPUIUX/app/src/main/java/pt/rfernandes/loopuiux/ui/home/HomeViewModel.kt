package pt.rfernandes.loopuiux.ui.home

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import pt.rfernandes.loopuiux.data.DataRepository
import pt.rfernandes.loopuiux.model.TravelEntry

class HomeViewModel(private val repository: DataRepository) : ViewModel() {

    val travelEntries: LiveData<List<TravelEntry>> = repository.getTravelEntries.asLiveData()

    fun insertEntry(travelEntry: TravelEntry) = viewModelScope.launch {
        repository.insert(travelEntry)
    }

    fun deleteEntry(travelEntry: TravelEntry) = viewModelScope.launch {
        repository.delete(travelEntry)
    }

}

class HomeViewModelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}