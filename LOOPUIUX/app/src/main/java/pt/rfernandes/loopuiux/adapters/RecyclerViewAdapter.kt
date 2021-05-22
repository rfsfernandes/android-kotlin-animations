package pt.rfernandes.loopuiux.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.drawee.view.SimpleDraweeView
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.model.EntryContent

/**
 *   Class RecyclerViewAdapter created at 5/19/21 23:03 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
class RecyclerViewAdapter(private val context: Context, private val callback: CollapseCallback) :
    RecyclerView.Adapter<RecyclerViewAdapter.ListViewHolder>() {
    private var postsList: List<EntryContent> = ArrayList()
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
        } else if(!model.alreadySeen) {
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

        var isOpen = model.isOpen
        var hasCompleted = true

        val clickListener: View.OnClickListener = View.OnClickListener {

            if (hasCompleted) {
                hasCompleted = false
                if (!isOpen) {
                    callback.clicked(position)
                    holder.motionBase.transitionToEnd()
                } else {
                    holder.motionBase.transitionToStart()
                }
            }

        }

        holder.motionBase.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

                isOpen = !isOpen

                if(!isOpen) {
                    holder.textViewContent.maxLines = Integer.MAX_VALUE;
                }

                model.isOpen = isOpen

                hasCompleted = true
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }

        })

        holder.rootView.setOnClickListener(clickListener)
        holder.chevronBackground.setOnClickListener(clickListener)

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
        val chevronBackground: ImageButton =
            itemView.findViewById(R.id.imageButtonChevronBackground)
        val rootView: View = itemView

        fun clearAnimation() {
            rootView.clearAnimation()
        }
    }

    fun refreshList(newList: ArrayList<EntryContent>) {
        postsList = newList
        notifyDataSetChanged()
    }
}
