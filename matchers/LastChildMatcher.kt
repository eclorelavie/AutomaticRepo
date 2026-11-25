package rb.com.mbanking.presentation.kaspresso.matchers

import android.view.View
import android.view.ViewGroup
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

/**
 * Поиск последнего view у родителя
 */
class LastChildMatcher : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description) {
        description.appendText("last position of parent")
    }

    override fun matchesSafely(view: View): Boolean {
        if (view.parent !is ViewGroup) return false
        val parent: ViewGroup = view.parent as ViewGroup
        return (parent.getChildAt(parent.childCount - 1) == view)
    }
}
