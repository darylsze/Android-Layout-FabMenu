package darylsze.fabsubmenu

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.fab_submenu_item.view.*
import kotlinx.android.synthetic.main.floating_action_menu.view.*
import java.util.*

/**
 * Created by windsze on 22/2/2018.
 */


/**
 * Default use title:String first, then use titleRes
 */
data class FabItem(
        val id: Int = Random().nextInt(),
        val title: String? = null,
        val titleRes: Int? = null,
        val iconRes: Int,
        val color: Int,
        val onClickAction: (View) -> Unit
) {
    fun getTitleString(context: Context): String {
        title ?: titleRes ?: throw NullPointerException("both title and titleRes are null.")
        return title ?: context.getString(titleRes!!)
    }
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.removeParentView(): View {
    (parent as? ViewGroup)?.removeView(this)
    return this
}

class FloatingActionMenu @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    interface OnMenuItemClickListener {
        fun onParentFabClick()
        fun onParentFabActive()
        fun onParentFabInactive()
        fun onMenuItemFabClick(fab: FabItem)
    }

    private val menuItems = mutableMapOf<FabItem, View>()
    private var listener: OnMenuItemClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.floating_action_menu, this, true)
        fabBg.hide()
        fabMenuParent.setOnClickListener {
            listener?.onParentFabClick()

            if (didFakeViewShow) {
                closeFloatingActionMenu()
                listener?.onParentFabInactive()
            } else {
                openFloatingActionMenu()
                listener?.onParentFabActive()
            }
        }
    }

    fun setOnMenuItemClickListener(listener: OnMenuItemClickListener) {
        this.listener = listener
    }

    fun removeMenuItems(vararg item: FabItem) {
        item.forEach {
            menuItems[it]?.apply {
                (this.parent as ViewGroup).removeView(this)
                menuItems.remove(it)
                Log.i("", "item $item has deleted from menuItems")
            }
        }

        closeFloatingActionMenu()
    }


    fun openFloatingActionMenu() {
        showCircularReveal(fabBg)
        scrollUpFabs(*menuItems.values.toTypedArray())
    }

    fun closeFloatingActionMenu() {
        hideCircularReveal(fabBg)
        scrollDownFabs(*menuItems.values.toTypedArray())
    }

    fun addMenuItems(vararg item: FabItem) {
        item.forEach {
            val v = it.convertToView(fabMenuParent).removeParentView()
            menuItems[it] = v
            rl.addView(v)
        }
    }

    private fun produceFabMenuItemView(): View {
        return LayoutInflater.from(context).inflate(R.layout.fab_submenu_item, rl, false) as RelativeLayout
    }

    private fun FabItem.convertToView(parentBtn: FloatingActionButton): View {
        val context = parentBtn.context
        val v = produceFabMenuItemView()
        v.layoutParams = (v.layoutParams as RelativeLayout.LayoutParams).apply {
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            val right = (fabMenuParent.width - v.famItemBtn.width) / 2
            rightMargin = (fabMenuParent.layoutParams as ViewGroup.MarginLayoutParams).rightMargin + right
            bottomMargin = (fabMenuParent.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
        }

        v.famItemLabel.text = this.getTitleString(context)
        v.famItemBtn.setImageDrawable(ContextCompat.getDrawable(context, iconRes))
        v.famItemBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, color))
        v.setOnClickListener {
            onClickAction.invoke(v)
            listener?.onMenuItemFabClick(this)
        }
        v.hide()

        return v
    }

    fun setFabIcon(@DrawableRes icon: Int) {
        fabMenuParent.setImageResource(icon)
    }

    /**
    SIZE_MINI = 1
    SIZE_NORMAL = 0
    SIZE_AUTO = -1
    AUTO_MINI_LARGEST_SCREEN_WIDTH = 470
    see floatingActionButton for reference
     */
    fun setFabSize(size: Int) {
        fabMenuParent.size = size
    }

    fun setFabColor(@ColorRes color: Int) {
        fabMenuParent.setImageDrawable(ContextCompat.getDrawable(context, color))
        fabMenuParent.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, color))
    }

    var isFabLoading = false

    fun setProgress(progress: Int) {
        // todo show circular progress bar outside button
    }

    fun setLoading(toLoad: Boolean) {
        //todo Not yet implemented
        if (toLoad && !isFabLoading) {
            startFabLoading()
            return
        }

        if (!toLoad && isFabLoading) {
            cancelFabLoading()
            return
        }
    }

    private fun cancelFabLoading() {
        // todo
    }

    private fun startFabLoading() {
        // todo

    }

    private fun scrollDownFabs(vararg famItems: View) {
        famItems.toList().forEachIndexed { index, view ->

            val fromY = (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            val toY = (fabMenuParent.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin + (fabMenuParent.height - view.height)
            val Y_THRESHOLD = 150

            ValueAnimator.ofInt(fromY)
                    .apply {
                        duration = 300
                        start()
                        addUpdateListener {
                            val newY = it.animatedValue as Int
                            view.layoutParams = (view.layoutParams as ViewGroup.MarginLayoutParams).apply {
                                bottomMargin = Math.max(fromY - newY, toY)
                            }
                            if (newY > fromY - Y_THRESHOLD) {
                                view.visibility = View.INVISIBLE
                            }
                        }
                    }

            scaleSmallView(view.famItemBtn)
        }

    }

    private fun scaleSmallView(view: View) {
        ScaleAnimation(1f, 0.5f, 1f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                .apply {
                    duration = 300
                    fillAfter = true
                    view.startAnimation(this)
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                        override fun onAnimationStart(animation: Animation?) {
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            view.visibility = View.INVISIBLE
                        }

                    })
                }
    }

    private fun scrollUpFabs(vararg fabs: View) {
        fabs.toList().forEachIndexed { index, view ->
            view.visibility = View.VISIBLE

            val toY = getViewRealHeight(fabMenuParent) + (index.toFloat() * getViewRealHeight(view)).toInt()

            ValueAnimator.ofInt(toY).apply {
                start()
                addUpdateListener {
                    val newY = it.animatedValue as Int
                    view.layoutParams = (view.layoutParams as ViewGroup.MarginLayoutParams).apply {
                        bottomMargin = newY
                    }
                }
            }

            scaleLargeView(view.famItemBtn)
        }
    }

    private fun scaleLargeView(view: View) {
        ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                .apply {
                    duration = 300
                    fillAfter = true
                    view.startAnimation(this)
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            view.visibility = View.VISIBLE
                        }

                        override fun onAnimationStart(animation: Animation?) {
                        }

                    })
                }
    }

    private fun getViewRealHeight(view: View): Int {
        val bottomMargin = (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
        val topMargin = (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin
        val topPadding = view.paddingTop
        val bottomPadding = view.paddingBottom
        return view.height + (topMargin + bottomMargin) / 2 + (topPadding + bottomPadding) / 2
    }

    private fun showCircularReveal(viewToShow: View) {
        didFakeViewShow = true

        // get the center for the clipping circle
        val cx = (fabMenuParent.left + fabMenuParent.getRight()) / 2
        val cy = (fabMenuParent.getTop() + fabMenuParent.getBottom()) / 2

        // get the final radius for the clipping circle
        val finalRadius = Math.sqrt(Math.pow(viewToShow.width.toDouble(), 2.toDouble()) + Math.pow(viewToShow.height.toDouble(), 2.toDouble()))

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(viewToShow, cx, cy, fabMenuParent.measuredWidth / 2f, finalRadius.toFloat())

        // make the view visible and start the animation
        viewToShow.show()

        anim.start()
    }

    private var didFakeViewShow: Boolean = false

    private fun hideCircularReveal(viewToHide: View) {
        didFakeViewShow = false

        // get the center for the clipping circle
        val cx = (fabMenuParent.getLeft() + fabMenuParent.getRight()) / 2
        val cy = (fabMenuParent.getTop() + fabMenuParent.getBottom()) / 2

        // get the initial radius for the clipping circle
        val initialRadius = Math.hypot(viewToHide.width.toDouble(), viewToHide.height.toDouble())

        // create the animation (the final radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(viewToHide, cx, cy, initialRadius.toFloat(), fabMenuParent.measuredWidth / 2f)

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                viewToHide.setVisibility(View.INVISIBLE)
            }
        })

        // start the animation
        anim.start()

    }

}

