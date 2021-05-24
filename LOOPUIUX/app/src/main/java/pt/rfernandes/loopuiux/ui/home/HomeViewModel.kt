package pt.rfernandes.loopuiux.ui.home

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import pt.rfernandes.loopuiux.data.DataRepository
import pt.rfernandes.loopuiux.model.TravelEntry

class HomeViewModel(private val repository: DataRepository) : ViewModel() {

    private val content =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam convallis ex vitae sagittis viverra. Morbi convallis, mi sed placerat auctor, quam risus faucibus ligula, at maximus ligula massa nec lectus. Pellentesque a dapibus risus. Sed tristique malesuada maximus. Duis id nisi vitae neque porttitor porttitor pulvinar et lectus. Praesent id augue sit amet quam semper ullamcorper. Etiam dignissim tortor erat, ac interdum tellus lacinia at. Pellentesque laoreet egestas nisi. In euismod ligula eu risus commodo tincidunt. Mauris facilisis ex ut magna finibus, id tempus magna congue.\n" +
                "\n" +
                "Morbi ut tincidunt quam, vitae sollicitudin nibh. Quisque cursus iaculis nunc non mattis. Pellentesque elementum fermentum efficitur. Etiam aliquam ante ac felis feugiat, ac sodales risus semper. Pellentesque laoreet nisl non quam pretium convallis. Vivamus id quam massa. Maecenas turpis mi, maximus nec tempor ut, egestas eu ipsum. Aenean vehicula diam nisi, eget consequat diam ultricies at.\n" +
                "\n"

    private val title = "Lorem ipsum dolor sit amet, consectetur adipiscing elit"

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