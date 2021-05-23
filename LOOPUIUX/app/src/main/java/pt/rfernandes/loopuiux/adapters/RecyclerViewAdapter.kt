package pt.rfernandes.loopuiux.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.model.EntryContent


/**
 *   Class RecyclerViewAdapter created at 5/19/21 23:03 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
class RecyclerViewAdapter(
    private val context: Context,
    private val callback: RecyclerViewCallback,
    private val recyclerView: RecyclerView
) :
    RecyclerView.Adapter<RecyclerViewAdapter.ListViewHolder>() {
    private var postsList: ArrayList<EntryContent> = ArrayList()
    private var mPosition: Int = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder = ListViewHolder(
        LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
    )

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        mPosition = position

        val model = postsList[position]

        if (!model.hasBeenLoaded) {
            holder.rootView.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.item_animation_left_right
                )
            )
            model.hasBeenLoaded = true
        } else if (!model.alreadySeen) {
            holder.rootView.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.item_animation_fall_down
                )
            )
        }
        model.alreadySeen = true
        holder.textViewContentTitle.text = model.title

        holder.textViewContent.text = model.content

        holder.imageViewContent.setImageURI(model.image)

        var isDetailsOpen = model.isOpen
        var isDeleteOpen = false
        var hasCompleted = true
        var isToOpenDetails = false
        var isToOpenDelete = false
        val clickListenerOpen: View.OnClickListener = View.OnClickListener { v ->
            if (!isDeleteOpen) {
                holder.motionBase.setTransition(R.id.start_to_end_list)
                isToOpenDetails = true
                isToOpenDelete = false
                if (hasCompleted) {
                    hasCompleted = false
                    if (!isDetailsOpen) {
                        callback.clickedItem(position)
                        holder.motionBase.transitionToEnd()
                    } else {
                        holder.motionBase.transitionToStart()
                    }
                }
            }
            v.clearFocus()
        }
        var isToDelete = false;

        holder.cardViewImage.setOnClickListener { v ->
            if (!isDetailsOpen) {
                holder.motionBase.setTransition(R.id.start_to_delete_list)
                isToOpenDetails = false
                isToOpenDelete = true
                if (hasCompleted) {
                    hasCompleted = false
                    if (!isDeleteOpen)
                        holder.motionBase.transitionToEnd()
                    else
                        holder.motionBase.transitionToStart()
                }
            }
            v.clearFocus()
        }

        holder.motionBase.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (isToOpenDetails) {
                    isDetailsOpen = !isDetailsOpen

                    if (!isDetailsOpen) {
                        holder.textViewContent.maxLines = Integer.MAX_VALUE;
                    }

                    model.isOpen = isDetailsOpen
                    isToOpenDetails = false
                }

                if (isToOpenDelete) {
                    isDeleteOpen = !isDeleteOpen
                }

                hasCompleted = true
                if (isToDelete) {
                    handleRemoveItem(holder, position)
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }

        })

        holder.chevronBackground.setOnClickListener(clickListenerOpen)

        val clickListenerDelete: View.OnClickListener = View.OnClickListener {
            isToDelete = true

            holder.motionBase.transitionToStart()
        }

        holder.imageButtonDeleteEntry.setOnClickListener(clickListenerDelete)

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

                postsList.removeAt(position)
                notifyDataSetChanged()
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })

        holder.rootView.startAnimation(animation)
//        postsList.removeAt(position)
//        notifyDataSetChanged()
    }

    override fun onViewDetachedFromWindow(holder: ListViewHolder) {
        holder.clearAnimation()
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    fun collapseMotionLayout(holder: ListViewHolder) {
        holder.textViewContent.maxLines = 2
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

        fun clearAnimation() {
            rootView.clearAnimation()
        }
    }

    fun refreshList(newList: ArrayList<EntryContent>) {
        postsList = newList
        notifyDataSetChanged()
    }
}
