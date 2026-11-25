package rb.com.mbanking.presentation.kaspresso.screens

import android.view.View
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.text.KTextView
import org.hamcrest.Matcher
import rb.com.mbanking.R
import rb.com.mbanking.presentation.kaspresso.base.BaseScreen
import rb.com.mbanking.presentation.more_in_tab_bar.MoreInTabBarFragment

object MoreScreen : BaseScreen<MoreScreen>() {

    override val layout = R.menu.more_in_tab_bar_menu
    override val fragment = MoreInTabBarFragment::class

    val logout = KTextView { withId(R.id.action_exit) }
    val settings = KTextView {
        withId(R.id.tvSubtitle)
        withText(R.string.settings)
    }
    val moreScreenRV = KRecyclerView({ withId(R.id.item_list) }) {
        itemType(::Item)
    }

    class Item(parent: Matcher<View>) : KRecyclerItem<Item>(parent) {

        val avatar = KTextView(parent) { withId(R.id.avatar) }
        val title = KTextView(parent) { withId(R.id.tvTitle) }
        val subTitle = KTextView(parent) { withId(R.id.tvSubtitle) }
    }
}
