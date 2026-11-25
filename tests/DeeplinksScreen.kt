package psb.com.mbanking.presentation.kaspresso.screens.unauthorized


import androidx.appcompat.widget.AppCompatTextView
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.text.KTextView
import psb.com.mbanking.R
import psb.com.mbanking.presentation.deep_link.DeepLinkTestFragment
import psb.com.mbanking.presentation.kaspresso.base.BaseScreen

object DeeplinksScreen : BaseScreen<DeeplinksScreen>() {

    override val layout = R.layout.content_deeplink_test
    override val fragment = DeepLinkTestFragment::class

    val toolbarTitle = KTextView {
        withParent { withId(R.id.toolbar) }
        isInstanceOf(AppCompatTextView::class.java)
        isFirst()
    }

    val etDeeplink = KEditText {
        withId(R.id.etDeeplink)
    }
    val btnEnter = KEditText {
        withId(R.id.btnEnter)
    }
}
