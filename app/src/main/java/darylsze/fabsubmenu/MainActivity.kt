package darylsze.fabsubmenu

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val fabSubMenu = listOf<FabItem>(
            FabItem(title = "位置", iconRes = android.R.drawable.ic_dialog_email, color = R.color.colorPrimary, onClickAction = { view -> Snackbar.make(view, "submenu item $view on click ", Snackbar.LENGTH_LONG).show() }),
            FabItem(title = "時間", iconRes = android.R.drawable.ic_dialog_email, color = R.color.colorPrimary, onClickAction = { view -> Snackbar.make(view, "submenu item $view on click ", Snackbar.LENGTH_LONG).show() }),
            FabItem(title = "地區", iconRes = android.R.drawable.ic_dialog_email, color = R.color.colorAccent, onClickAction = { view -> Snackbar.make(view, "submenu item $view on click ", Snackbar.LENGTH_LONG).show() })
    ).asReversed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fam.addMenuItems(*fabSubMenu.toTypedArray())

        fam.setOnMenuItemClickListener(object : FloatingActionMenu.OnMenuItemClickListener {
            override fun onParentFabClick() {
                Log.i("tag", "on parent fab click")
            }

            override fun onParentFabActive() {
                Log.i("tag", "on parent fab active")
            }

            override fun onParentFabInactive() {
                Log.i("tag", "on parent fab inactive")
            }

            override fun onMenuItemFabClick(fab: FabItem) {
                Log.i("tag", "on menu item fab clicked $fab")
                fam.removeMenuItems(fabSubMenu.first())
            }

        })
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
