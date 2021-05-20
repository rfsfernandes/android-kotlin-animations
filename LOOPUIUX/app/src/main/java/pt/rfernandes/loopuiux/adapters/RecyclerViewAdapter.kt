package pt.rfernandes.loopuiux.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.model.PostContent

/**
 *   Class RecyclerViewAdapter created at 5/19/21 23:03 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
class RecyclerViewAdapter(private val context: Context, private val callback: CollapseCallback) :
    RecyclerView.Adapter<RecyclerViewAdapter.ListViewHolder>() {
    private var postsList: List<PostContent> = ArrayList()
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

        holder.textViewContentTitle.text = model.title

        holder.textViewContent.text = model.content

        Glide.with(context).load(model.image).into(holder.imageViewContent)

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
                if (isOpen && !hasCompleted)
                    holder.textViewContent.maxLines = 2
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

                if (isOpen)
                    holder.textViewContent.maxLines = 2
                else
                    holder.textViewContent.maxLines = 100

                isOpen = !isOpen

                model.isOpen = isOpen

                hasCompleted = true
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }

        })

        holder.rootView.setOnClickListener(clickListener)
        holder.chevronBackground.setOnClickListener(clickListener)

    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    fun collapseMotionLayout(holder: ListViewHolder) {
        holder.textViewContent.maxLines = 2
        holder.motionBase.transitionToStart()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewContent: ImageView = itemView.findViewById(R.id.imageViewContent)
        val textViewContentTitle: TextView = itemView.findViewById(R.id.textViewContentTitle)
        val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        val motionBase: MotionLayout = itemView.findViewById(R.id.motion_base)
        val chevronBackground: ImageButton =
            itemView.findViewById(R.id.imageButtonChevronBackground)
        val rootView: View = itemView

    }

    fun refreshList(newList: ArrayList<PostContent>) {
        postsList = newList
        notifyDataSetChanged()
    }
}
