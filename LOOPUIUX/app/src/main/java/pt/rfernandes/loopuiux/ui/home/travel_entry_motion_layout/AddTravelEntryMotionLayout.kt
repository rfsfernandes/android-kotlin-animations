package pt.rfernandes.loopuiux.ui.home.travel_entry_motion_layout


import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.rfernandes.loopuiux.MainActivity
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.databinding.NewEntryMotionLayoutBinding
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

    private var _binding: NewEntryMotionLayoutBinding? = null
    private val binding get() = _binding!!

    var buttonIsOpened = false

    /** Store all the transition durations to be able to update them dynamically later */
    private val durationsMap: Map<MotionScene.Transition, Int> =
        definedTransitions.associateWith { it.duration }

    init {

        inflate(context, R.layout.new_entry_motion_layout, this)
        _binding =
            NewEntryMotionLayoutBinding.bind(rootView)
        updateDurations()

    }

    fun openSheet(): Unit = performAnimation {
        setTransition(R.id.set1_base, R.id.set2_path)

        // 1) set1_base -> set2_path
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

        setTransition(R.id.set1_base, R.id.set2_path)
        progress = 1f
        transitionToStart()
        startScaleDownAnimator(false)
        awaitTransitionComplete(R.id.set1_base)
        buttonIsOpened = false

    }


    private inline fun performAnimation(crossinline block: suspend () -> Unit) {
        (context as MainActivity).lifecycleScope.launch {
            block()
        }
    }

    fun updateDurations() = definedTransitions.forEach {
        it.duration = (durationsMap[it]!! / animationPlaybackSpeed).toInt()
    }

    private fun startScaleDownAnimator(isScaledDown: Boolean): Unit {

    }

}