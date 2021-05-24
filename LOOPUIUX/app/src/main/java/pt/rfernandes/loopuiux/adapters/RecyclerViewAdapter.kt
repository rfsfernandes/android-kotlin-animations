package pt.rfernandes.loopuiux.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.bottomsheet.BottomSheetDialog
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.model.TravelEntry
import pt.rfernandes.loopuiux.ui.utils.CustomOnSwipeTouchListener
import kotlin.math.abs


/**
 *   Class RecyclerViewAdapter created at 5/19/21 23:03 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
class RecyclerViewAdapter(
    private val context: Context,
    private val callback: RecyclerViewCallback
) :
    RecyclerView.Adapter<RecyclerViewAdapter.ListViewHolder>() {
    private var travelEntryList: ArrayList<TravelEntry> = ArrayList()
    private var viewHolderList: ArrayList<ListViewHolder> = ArrayList()
    private var isToDelete = false
    private var forcedClose = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder = ListViewHolder(
        LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
    )

    override fun onBindViewHolder(tempHolder: ListViewHolder, position: Int) {
        var holder = tempHolder
        val model = travelEntryList[position]
        holder.id = model.id
        var foundOne = false

        for (index in viewHolderList.indices) {
            if (holder.id == viewHolderList[index].id) {
                holder = viewHolderList[index]
                foundOne = true
                break
            }
        }

        if (!foundOne) {
            viewHolderList.add(holder)
        }

        if (!model.alreadySeen) {
            holder.rootView.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.item_animation_fall_down
                )
            )
            model.alreadySeen = true
        }

        model.alreadySeen = true
        holder.textViewContentTitle.text = model.title

        holder.textViewContent.text = model.content
        if (model.image.matches("-?\\d+(\\.\\d+)?".toRegex()))
            holder.imageViewContent.setActualImageResource(model.image.toInt())
        else
            holder.imageViewContent.setImageURI(model.image)

        var isDetailsOpen = model.isDetailsOpen
        var isDeleteOpen = model.isDeleteOpen

        if (isDetailsOpen) {
            holder.motionBase.setTransition(R.id.start_to_end_list)
            holder.textViewContent.maxLines = 2
            holder.motionBase.transitionToStart()
        } else if (isDeleteOpen) {
            holder.motionBase.setTransition(R.id.start_to_delete_list)
            holder.motionBase.transitionToStart()
        }

        var hasCompleted = true
        var isToOpenDetails = false
        var isToOpenDelete = false

        val clickListenerOpenDetails: View.OnClickListener = View.OnClickListener { v ->
            if (!isDeleteOpen && hasCompleted) {
                hasCompleted = false
                holder.motionBase.setTransition(R.id.start_to_end_list)
                isToOpenDetails = true
                isToOpenDelete = false
                forcedClose = false
                if (!isDetailsOpen) {
                    model.isDetailsOpen = true
                    holder.motionBase.transitionToEnd()
                } else {
                    model.isDetailsOpen = false
                    holder.motionBase.transitionToStart()
                }

            }
        }

        holder.cardViewImage.setOnClickListener(clickListenerOpenDetails)

        val customTouchListener: CustomOnSwipeTouchListener =
            object : CustomOnSwipeTouchListener(context) {

                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    if (!isDetailsOpen) {
//                        hasCompleted = false
                        holder.motionBase.setTransition(R.id.start_to_delete_list)
                        isToOpenDetails = false
                        isToOpenDelete = true
                        forcedClose = false

                        model.isDeleteOpen = false
                        holder.motionBase.transitionToStart()

                    }
                }

                override fun onSwipeRight() {
                    super.onSwipeRight()
                    if (!isDetailsOpen) {
//                        hasCompleted = false
                        holder.motionBase.setTransition(R.id.start_to_delete_list)
                        isToOpenDetails = false
                        isToOpenDelete = true
                        forcedClose = false

                        model.isDeleteOpen = true
                        holder.motionBase.transitionToEnd()

                    }
                }
            }

        holder.cardViewImage.setOnTouchListener(customTouchListener)
        holder.textViewContentTitle.setOnTouchListener(customTouchListener)
        holder.textViewContent.setOnTouchListener(customTouchListener)
        holder.imageViewContent.setOnTouchListener(customTouchListener)

        holder.motionBase.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                hasCompleted = true
                if (isToOpenDetails) {
                    isDetailsOpen = !isDetailsOpen

                    if (!isDetailsOpen) {
                        holder.textViewContent.maxLines = Integer.MAX_VALUE
                    }

                    model.isDetailsOpen = isDetailsOpen
                    if (!forcedClose)
                        setToFalse(model.id)
                }

                if (isToOpenDelete) {
                    isDeleteOpen = !isDeleteOpen
                    model.isDeleteOpen = isDeleteOpen
                    if (!forcedClose)
                        setToFalse(model.id)
                }

                if (isToDelete) {
                    handleRemoveItem(holder, position)
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }

        })

        holder.chevronBackground.setOnClickListener(clickListenerOpenDetails)

        val clickListenerDelete: View.OnClickListener = View.OnClickListener {
            showDialogDelete(holder)
        }

        holder.imageButtonDeleteEntry.setOnClickListener(clickListenerDelete)

    }

    fun setToFalse(id: Int) {

        var holder: ListViewHolder

        for (tempHolder in viewHolderList) {
            if (tempHolder.id != id) {

                holder = tempHolder
                for (index in travelEntryList.indices) {
                    if (travelEntryList[index].id != id && travelEntryList[index].isDetailsOpen && holder.id == travelEntryList[index].id) {
                        travelEntryList[index].isDetailsOpen = false
                        forcedClose = true
                        collapseMotionLayoutExpandDetails(holder)
                        break
                    } else if (travelEntryList[index].id != id && travelEntryList[index].isDeleteOpen && holder.id == travelEntryList[index].id) {
                        travelEntryList[index].isDeleteOpen = false
                        forcedClose = true
                        collapseMotionLayoutDelete(holder)
                        break
                    }
                }

            }
        }


    }

    fun handleRemoveItem(holder: ListViewHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(
            context,
            R.anim.item_animation_middle_right
        )

        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                isToDelete = false
                callback.deletedItem(travelEntryList[position])
                travelEntryList.removeAt(position)
                notifyDataSetChanged()
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })

        holder.rootView.startAnimation(animation)
    }

    private fun showDialogDelete(holder: ListViewHolder) {
        val bottomSheetDialog = BottomSheetDialog(context)

        bottomSheetDialog.setContentView(R.layout.custom_dialog_layout)

        bottomSheetDialog.findViewById<Button>(R.id.buttonAcceptDialog)?.setOnClickListener {
            bottomSheetDialog.dismiss()
            isToDelete = true

            holder.motionBase.transitionToStart()
        }

        bottomSheetDialog.findViewById<Button>(R.id.buttonDismissDialog)?.setOnClickListener {
            bottomSheetDialog.dismiss()

        }

        bottomSheetDialog.setOnDismissListener {
            isToDelete = false

            holder.motionBase.transitionToStart()
        }

        bottomSheetDialog.show()


    }

    override fun onViewDetachedFromWindow(holder: ListViewHolder) {
        holder.clearAnimation()
    }

    override fun getItemCount(): Int {
        return travelEntryList.size
    }

    override fun getItemId(position: Int): Long {
        return travelEntryList[position].id.toLong()
    }

    private fun collapseMotionLayoutExpandDetails(holder: ListViewHolder) {
        holder.textViewContent.maxLines = 2
        holder.motionBase.transitionToStart()
    }

    private fun collapseMotionLayoutDelete(holder: ListViewHolder) {
        holder.motionBase.transitionToStart()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewContent: SimpleDraweeView = itemView.findViewById(R.id.imageViewContent)
        val textViewContentTitle: TextView = itemView.findViewById(R.id.textViewContentTitle)
        val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        val motionBase: MotionLayout = itemView.findViewById(R.id.motion_base)
        val cardViewImage: CardView = itemView.findViewById(R.id.imageView2)
        val chevronBackground: ImageButton =
            itemView.findViewById(R.id.imageButtonChevronBackground)
        val rootView: View = itemView

        val imageButtonDeleteEntry: ImageButton = itemView.findViewById(R.id.imageButtonDeleteEntry)

        var id: Int = 0

        fun clearAnimation() {
            rootView.clearAnimation()
        }
    }

    fun refreshList(newList: ArrayList<TravelEntry>) {
        travelEntryList = newList
        notifyDataSetChanged()
    }
}