package pt.rfernandes.loopuiux.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.rfernandes.loopuiux.model.PostContent

class HomeViewModel : ViewModel() {

    private val content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam convallis ex vitae sagittis viverra. Morbi convallis, mi sed placerat auctor, quam risus faucibus ligula, at maximus ligula massa nec lectus. Pellentesque a dapibus risus. Sed tristique malesuada maximus. Duis id nisi vitae neque porttitor porttitor pulvinar et lectus. Praesent id augue sit amet quam semper ullamcorper. Etiam dignissim tortor erat, ac interdum tellus lacinia at. Pellentesque laoreet egestas nisi. In euismod ligula eu risus commodo tincidunt. Mauris facilisis ex ut magna finibus, id tempus magna congue.\n" +
            "\n" +
            "Morbi ut tincidunt quam, vitae sollicitudin nibh. Quisque cursus iaculis nunc non mattis. Pellentesque elementum fermentum efficitur. Etiam aliquam ante ac felis feugiat, ac sodales risus semper. Pellentesque laoreet nisl non quam pretium convallis. Vivamus id quam massa. Maecenas turpis mi, maximus nec tempor ut, egestas eu ipsum. Aenean vehicula diam nisi, eget consequat diam ultricies at.\n" +
            "\n"

    private val title = "Lorem ipsum dolor sit amet, consectetur adipiscing elit"

    private fun getImage() : String {
        return "https://picsum.photos/id/${(1..200).random()}/500/100"
    }

    private val _postsList = MutableLiveData<ArrayList<PostContent>>().apply {
        val postList = ArrayList<PostContent>()

        for (i in 1 until 10) {
            postList.add(PostContent(getImage(), title, content))
        }

        value = postList
    }

    val postsList: LiveData<ArrayList<PostContent>> = _postsList
}