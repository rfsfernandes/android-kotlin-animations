package pt.rfernandes.loopuiux.ui.home.new_post


import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.rfernandes.loopuiux.MainActivity
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.databinding.NewPostMotionLayoutBinding
import pt.rfernandes.loopuiux.ui.utils.MultiListenerMotionLayout

var animationPlaybackSpeed: Double = 0.8

/**
 *   Class AddEntryMotionLayout created at 5/20/21 18:57 for the project LOOP UI&UX
 *   By: rodrigofernandes
 *
 *   A MotionLayout version of [FiltersLayout]
 *   All Transitions and ConstraintSets are defined in R.xml.scene_filter
 *
 *   Code in this class contains mostly only choreographing the transitions.
 */
class AddEntryMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MultiListenerMotionLayout(context, attrs, defStyleAttr) {

    private var _binding: NewPostMotionLayoutBinding? = null
    private val binding get() = _binding!!

    var buttonIsOpened = false

//    private val tabsRecyclerView: NoScrollRecyclerView by bindView(R.id.tabs_recycler_view)
//    private val viewPager: ViewPager2 by bindView(R.id.view_pager)
//    private val closeIcon: ImageView by bindView(R.id.close_icon)
//    private val filterIcon: ImageView by bindView(R.id.filter_icon)
//    private val bottomBarCardView: CardView by bindView(R.id.bottom_bar_card_view)

    /** Store all the transition durations to be able to update them dynamically later */
    private val durationsMap: Map<MotionScene.Transition, Int> =
        definedTransitions.associateWith { it.duration }

    init {

//        tabsHandler.init()
        inflate(context, R.layout.new_post_motion_layout, this)
        _binding =
            NewPostMotionLayoutBinding.bind(rootView)
        updateDurations()
//        enableClicks()
    }

    fun openSheet(): Unit = performAnimation {
        setTransition(R.id.set1_base, R.id.set2_path)

        // 1) set1_base -> set2_path
        // (Start scale down animation simultaneously)
        transitionToState(R.id.set2_path)
        startScaleDownAnimator(true)
        awaitTransitionComplete(R.id.set2_path)

        // 2) set2_path -> set3_reveal
        transitionToState(R.id.set3_reveal)
        awaitTransitionComplete(R.id.set3_reveal)

        // 3) set3_reveal -> set4_settle
        transitionToState(R.id.set4_settle)
        awaitTransitionComplete(R.id.set4_settle)
        buttonIsOpened = true
    }

    fun closeSheet(): Unit = performAnimation {
        // 1) set4_settle -> set3_reveal
        transitionToStart()
        awaitTransitionComplete(R.id.set3_reveal)

        // 2) set3_reveal -> set2_path
        setTransition(R.id.set2_path, R.id.set3_reveal)
        progress = 1f
        transitionToStart()
        awaitTransitionComplete(R.id.set2_path)

        // 3) set2_path -> set1_base
        // (Start scale 'up' animator simultaneously)
        setTransition(R.id.set1_base, R.id.set2_path)
        progress = 1f
        transitionToStart()
        startScaleDownAnimator(false)
        awaitTransitionComplete(R.id.set1_base)
        buttonIsOpened = false
        // Remove adapters after closing filter sheet
//        tabsHandler.setAdapters(false)
    }

    private fun onFilterApplied(): Unit = performAnimation {
//        if (!tabsHandler.hasActiveFilters) return@performAnimation

        // 1) set4_settle -> set5_filterCollapse
//        transitionToState(R.id.set5_filterCollapse)
//        awaitTransitionComplete(R.id.set5_filterCollapse)
//
//        // 2) set5_filterCollapse -> set6_filterLoading
//        // (Filter adapter items simultaneously)
////        (context as MainActivity).isAdapterFiltered = true
//        awaitTransitionComplete(R.id.set6_filterLoading)
//
//        // 3) set6_filterLoading -> set7_filterBase
//        // (Start scale 'up' animation simultaneously)
//        startScaleDownAnimator(false)
//        awaitTransitionComplete(R.id.set7_filterBase)

        // Remove adapters
//        tabsHandler.setAdapters(false)
    }

    private fun unFilterAdapterItems(): Unit = performAnimation {

        // 1) set7_filterBase -> set8_unfilterInset
        // (Start scale down animation simultaneously)
        transitionToState(R.id.set8_unfilterInset)
        startScaleDownAnimator(true)
        awaitTransitionComplete(R.id.set8_unfilterInset)

        // 2) set8_unfilterInset -> set9_unfilterLoading
        // (Un-filter adapter items simultaneously)
//        (context as MainActivity).isAdapterFiltered = false
        awaitTransitionComplete(R.id.set9_unfilterLoading)

        // 3) set9_unfilterLoading -> set10_unfilterOutset
        // (Start scale 'up' animation simultaneously)
        startScaleDownAnimator(false)
        awaitTransitionComplete(R.id.set10_unfilterOutset)
    }

//    private fun enableClicks() = when (currentState) {
//        R.id.set1_base, R.id.set10_unfilterOutset -> {
//            binding.closeIcon.setOnClickListener(null)
//        }
//        R.id.set4_settle -> {
//            binding.closeIcon.setOnClickListener { closeSheet() }
//        }
//        R.id.set7_filterBase -> {
//            binding.closeIcon.setOnClickListener { unFilterAdapterItems() }
//        }
//        else -> throw IllegalStateException("Can be called only for the permitted 3 currentStates")
//    }

//    private fun disableClicks() {
//        binding.closeIcon.setOnClickListener(null)
//    }

    private inline fun performAnimation(crossinline block: suspend () -> Unit) {
        (context as MainActivity).lifecycleScope.launch {
//            disableClicks()
            block()
//            enableClicks()
        }
    }

    fun updateDurations() = definedTransitions.forEach {
        it.duration = (durationsMap[it]!! / animationPlaybackSpeed).toInt()
    }

    private fun startScaleDownAnimator(isScaledDown: Boolean): Unit {

    }
//        (context as MainActivity)
//            .getAdapterScaleDownAnimator(isScaledDown)
//            .apply { duration = transitionTimeMs }
//            .start()
}