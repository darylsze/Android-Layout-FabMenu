package movie6.fabsubmenu

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.view.animation.TranslateAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


data class FabData(
        val title: String,
        val color: Int
)

class MainActivity : AppCompatActivity() {

    private var didFakeViewShow: Boolean = false
    val fakeView by lazy { fakeSubmenuView }

    val fabSubMenu = listOf<FabData, (view: View) -> Unit>(
            FabData(title = "fab2", color = R.color.colorPrimary) to { view -> Snackbar.make(view, "submenu item $view on click ", Snackbar.LENGTH_LONG).show() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            if (didFakeViewShow) {
                hideCircularReveal(fakeView)
                scrollDownFabs(fab2, fab3)
            } else {
                showCircularReveal(fakeView)
                scrollUpFabs(fab2, fab3)
            }

            Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun scrollDownFabs(vararg fabs: FloatingActionButton) {
        fabs.toList().forEachIndexed { index, view ->


            val toY = fab.height + index.toFloat() * fab.height + (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin
            val animate = TranslateAnimation(
                    0f, // fromXDelta
                    0f, // toXDelta
                    -toY, // fromYDelta
                    0f)                // toYDelta

            animate.duration = 300
            animate.fillAfter = true
            view.startAnimation(animate)
            view.hide()
        }

    }

    private fun scrollUpFabs(vararg fabs: FloatingActionButton) {
        fabs.toList().forEachIndexed { index, view ->

            val toY = fab.height + index.toFloat() * fab.height + (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin
            val animate = TranslateAnimation(
                    0f, // fromXDelta
                    0f, // toXDelta
                    0f, // fromYDelta
                    -toY)                // toYDelta
            animate.duration = 300
            animate.fillAfter = true
            view.startAnimation(animate)
            view.show()
        }
    }

    fun showCircularReveal(viewToShow: View) {
        didFakeViewShow = true

        // get the center for the clipping circle
        val cx = (fab.left + fab.getRight()) / 2
        val cy = (fab.getTop() + fab.getBottom()) / 2

        // get the final radius for the clipping circle
//        val finalRadius = Math.max(viewToShow.getWidth(), viewToShow.getHeight()) + 500
        val finalRadius = Math.sqrt(Math.pow(viewToShow.width.toDouble(), 2.toDouble()) + Math.pow(viewToShow.height.toDouble(), 2.toDouble()))

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(viewToShow, cx, cy, fab.measuredWidth / 2f, finalRadius.toFloat())

        // make the view visible and start the animation
        viewToShow.setVisibility(View.VISIBLE)

        anim.start()
    }

    fun hideCircularReveal(viewToHide: View) {
        didFakeViewShow = false

        // get the center for the clipping circle
        val cx = (fab.getLeft() + fab.getRight()) / 2
        val cy = (fab.getTop() + fab.getBottom()) / 2

        // get the initial radius for the clipping circle
        val initialRadius = Math.sqrt(Math.pow(viewToHide.width.toDouble(), 2.toDouble()) + Math.pow(viewToHide.height.toDouble(), 2.toDouble()))

        // create the animation (the final radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(viewToHide, cx, cy, initialRadius.toFloat(), fab.measuredWidth / 2f)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else                 -> super.onOptionsItemSelected(item)
        }
    }
}
