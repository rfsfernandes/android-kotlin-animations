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
 *   Class NewPostMotionLayout created at 5/20/21 18:57 for the project LOOP UI&UX
 *   By: rodrigofernandes
 *
 *   A MotionLayout version of [FiltersLayout]
 *   All Transitions and ConstraintSets are defined in R.xml.scene_filter
 *
 *   Code in this class contains mostly only choreographing the transitions.
 */
class NewPostMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MultiListenerMotionLayout(context, attrs, defStyleAttr) {

    private var _binding: NewPostMotionLayoutBinding? = null
    private val binding get() = _binding!!

//    private val tabsRecyclerView: NoScrollRecyclerView by bindView(R.id.tabs_recycler_view)
//    private val viewPager: ViewPager2 by bindView(R.id.view_pager)
//    private val closeIcon: ImageView by bindView(R.id.close_icon)
//    private val filterIcon: ImageView by bindView(R.id.filter_icon)
//    private val bottomBarCardView: CardView by bindView(R.id.bottom_bar_card_view)

    /** Store all the transition durations to be able to update them dynamically later */
    private val durationsMap: Map<MotionScene.Transition, Int> =
        definedTransitions.associateWith { it.duration }
//    private val tabsHandler: ViewPagerTabsHandler by lazy {
//        ViewPagerTabsHandler(viewPager, tabsRecyclerView, bottomBarCardView)
//    }

    init {

//        tabsHandler.init()
        inflate(context, R.layout.new_post_motion_layout, this)
        _binding =
            NewPostMotionLayoutBinding.bind(rootView)
        updateDurations()
        enableClicks()
    }

    /**
     * Opens the filter sheet. Set adapters before starting.
     *
     * Order of animations:
     * set1_base -> set2_path -> set3_reveal -> set4_settle
     */
    private fun openSheet(): Unit = performAnimation {

        // Set adapters before opening filter sheet
//        tabsHandler.setAdapters(true)

        // Set the start transition. This is necessary because the
        // un-filtering animation ends with set10 and we need to
        // reset it here when opening the sheet the next time
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
    }

    /**
     * Closes the filter sheet. Remove adapters after it's complete, useless to
     * keep them around unless opened again.
     * Instead of creating more transitions, we reverse the transitions by setting
     * the required transition at progress=1f (end state) and using [transitionToStart].
     *
     * Order of animations:
     * set4_settle -> set3_reveal -> set2_path -> set1_base
     */
    private fun closeSheet(): Unit = performAnimation {

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

        // Remove adapters after closing filter sheet
//        tabsHandler.setAdapters(false)
    }

    /**
     * Performs the filter animation. We don't use [transitionToState] or
     * [transitionToStart] here for multiple transitions because we use
     * autoTransition=animateToEnd in the MotionScene which automatically
     * transitions from state to state.
     *
     * Order of animations:
     * set4_settle -> set5_filterCollapse -> set6_filterLoading -> set7_filterBase
     */
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

    /**
     * Removes filters in adapter while animating.
     *
     * Order of animations:
     * set7_filterBase -> set8_unfilterInset -> set9_unfilterLoading -> set10_unfilterOutset
     */
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

    /**
     * Based on the currentState (ConstraintSet), we set the appropriate click listeners.
     * Do not call this method during an animation.
     */
    private fun enableClicks() = when (currentState) {
        R.id.set1_base, R.id.set10_unfilterOutset -> {
            binding.newEntryIcon.setOnClickListener { openSheet() }
            binding.closeIcon.setOnClickListener(null)
        }
        R.id.set4_settle -> {
            binding.newEntryIcon.setOnClickListener { onFilterApplied() }
            binding.closeIcon.setOnClickListener { closeSheet() }
        }
        R.id.set7_filterBase -> {
            binding.closeIcon.setOnClickListener { unFilterAdapterItems() }
            binding.newEntryIcon.setOnClickListener(null)
        }
        else -> throw IllegalStateException("Can be called only for the permitted 3 currentStates")
    }

    /**
     * Called when an animation is started so that double clicking or
     * clicking during animation will not trigger anything
     */
    private fun disableClicks() {
        binding.newEntryIcon.setOnClickListener(null)
        binding.closeIcon.setOnClickListener(null)
    }

    /**
     * Convenience method to launch a coroutine in MainActivity's lifecycleScope
     * (to start animating transitions in MotionLayout) and to handle clicks appropriately.
     *
     * Note: [block] must contain only animation related code. Clicks are
     * disabled at start and enabled at the end.
     *
     * Warning: [awaitTransitionComplete] must be called for the final state at the end of
     * [block], otherwise [enableClicks] will be called at the wrong time for the wrong state.
     */
    private inline fun performAnimation(crossinline block: suspend () -> Unit) {
        (context as MainActivity).lifecycleScope.launch {
            disableClicks()
            block()
            enableClicks()
        }
    }

    /**
     * Update durations of all transitions in the motion scene (Usually happens when
     * [animationPlaybackSpeed] in [MainActivity] is changed using the Nav Drawer).
     */
    fun updateDurations() = definedTransitions.forEach {
        it.duration = (durationsMap[it]!! / animationPlaybackSpeed).toInt()
    }


    /**
     * Convenience method to start ScaleDown animation in [MainListAdapter].
     * The duration of the scale down animation will match that of the current transition.
     */
    private fun startScaleDownAnimator(isScaledDown: Boolean): Unit {

    }
//        (context as MainActivity)
//            .getAdapterScaleDownAnimator(isScaledDown)
//            .apply { duration = transitionTimeMs }
//            .start()
}