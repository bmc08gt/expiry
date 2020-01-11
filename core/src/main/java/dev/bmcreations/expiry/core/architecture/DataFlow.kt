package dev.bmcreations.expiry.core.architecture

import android.content.Context
import androidx.annotation.StringRes

open class Request
open class Result


data class Error(
    val exception: Throwable? = null,
    val titleString: String? = null,
    val messageString: String? = null,
    @StringRes val titleResId: Int? = null,
    @StringRes val messageResId: Int? = null
) {
    fun hasErrors(): Boolean {
        return !(exception == null &&
                titleString == null &&
                messageString == null &&
                titleResId == null &&
                messageResId == null)
    }

    fun message(context: Context): String {
        return when {
            messageString != null -> messageString
            messageResId != null -> context.getString(messageResId)
            exception != null -> exception.localizedMessage
            else -> "unknown error"
        }
    }
}

data class Loading(
    val loading: Boolean = false,
    val message: String = "Loading..."
) {
    fun isLoading() = loading
}


data class Success(
    val messageString: String? = null,
    @StringRes val messageResId: Int? = null
) {
    fun message(context: Context): String {
        return when {
            messageString != null -> messageString
            messageResId != null -> context.getString(messageResId)
            else -> "Success"
        }
    }
}
