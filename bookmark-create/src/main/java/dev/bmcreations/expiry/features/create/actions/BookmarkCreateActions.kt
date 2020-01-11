package dev.bmcreations.expiry.features.create.actions

import android.net.Uri
import androidx.annotation.RestrictTo
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import dev.bmcreations.expiry.features.create.R
import dev.bmcreations.expiry.models.Group

@RestrictTo(RestrictTo.Scope.LIBRARY)
object BookmarkCreateActions {
    fun createGroup(controller: NavController) = controller.navigate(R.id.create_a_group)
    fun removeGroup(controller: NavController, group: Group) =
        controller.navigate(R.id.remove_a_group, bundleOf("group" to group))
}
