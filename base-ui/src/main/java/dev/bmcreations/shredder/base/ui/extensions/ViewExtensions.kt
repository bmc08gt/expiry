package dev.bmcreations.shredder.base.ui.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.DisplayCutout
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.bmcreations.shredder.base.R

/** Combination of all flags required to put activity into immersive mode */
const val FLAGS_FULLSCREEN =
    View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

/** Milliseconds used for UI animations */
const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L

/** Pad this view with the insets provided by the device cutout (i.e. notch) */
@RequiresApi(Build.VERSION_CODES.P)
fun View.padWithDisplayCutout() {

    /** Helper method that applies padding from cutout's safe insets */
    fun doPadding(cutout: DisplayCutout) = setPadding(
        cutout.safeInsetLeft,
        cutout.safeInsetTop,
        cutout.safeInsetRight,
        cutout.safeInsetBottom
    )

    // Apply padding using the display cutout designated "safe area"
    rootWindowInsets?.displayCutout?.let { doPadding(it) }

    // Set a listener for window insets since view.rootWindowInsets may not be ready yet
    setOnApplyWindowInsetsListener { _, insets ->
        insets.displayCutout?.let { doPadding(it) }
        insets
    }
}

/** Same as [AlertDialog.show] but setting immersive mode in the dialog's window */
fun AlertDialog.showImmersive() {
    // Set the dialog to not focusable
    window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    )

    // Make sure that the dialog's window is in full screen
    window?.decorView?.systemUiVisibility =
        FLAGS_FULLSCREEN

    // Show the dialog while still in immersive mode
    show()

    // Set the dialog to focusable again
    window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
}

private fun resourceHasPackage(id: Int): Boolean {
    return id.ushr(24) != 0
}

val View.identifier: String
    get() {
        val r = this.resources
        r?.let {
            if (id > 0 && resourceHasPackage(
                    id
                )
            ) {
                try {
                    val pkgname: String = when (id and -0x1000000) {
                        0x7f000000 -> "app"
                        0x01000000 -> "android"
                        else -> r.getResourcePackageName(id)
                    }
                    val typename = r.getResourceTypeName(id)
                    val entryname = r.getResourceEntryName(id)
                    return "$pkgname:$typename/$entryname"
                } catch (e: Resources.NotFoundException) {
                }
            }
        }
        return "unknown"
    }

fun Activity.closeIme() {
    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also { imm ->
        val view = this.currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

}

fun Fragment.closeIme() {
    val activity = ((this as? Fragment)?.activity)
    activity?.closeIme()
}

fun Activity.openIme() {
    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also { imm ->
        val view = this.currentFocus ?: View(this)
        imm.showSoftInput(view, 0)
    }
}

fun Fragment.openIme() {
    val activity = ((this as? Fragment)?.activity)
    activity?.openIme()
}

val Activity.rootView: View
    get() {
        return findViewById<View>(android.R.id.content)
    }

fun View.tintBackground(colorResId: Int) {
    val wrapDrawable = DrawableCompat.wrap(background).mutate()
    DrawableCompat.setTint(wrapDrawable, colorResId)
    background = wrapDrawable
}

fun AppCompatImageView.tint(colorResId: Int) {
    this.setColorFilter(colorResId)
}

fun AppCompatImageView.removeTint() {
    this.clearColorFilter()
}

fun FloatingActionButton.animateVisible(delay: Long) {
    apply {
        isInvisible = true
        scaleX = 0f
        scaleY = 0f
        doOnPreDraw {
            postDelayed({ show() }, delay)
        }
    }
}

fun FloatingActionButton.animateGone(delay: Long) {
    apply {
        isInvisible = true
        scaleX = 0f
        scaleY = 0f
        doOnPreDraw {
            postDelayed({
                hide()
            }, delay)
        }
    }
}

fun View.animateVisible(duration: Long) {
    animate()
        .alpha(1f)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isVisible = true
            }
        })
}

fun View.animateGone(duration: Long, delay: Long = 0, doOnDone: (() -> Unit)? = null) {
    animate()
        .alpha(0f)
        .setStartDelay(delay)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isVisible = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                doOnDone?.invoke()
            }
        })
}

fun Button.shake(vibrate: Boolean = false) =
    animate()
        .xBy(-15f)
        .withStartAction {
            if (vibrate) {
                context.getSystemService(Vibrator::class.java)?.vibrateCompat(500)
            }
        }
        .withEndAction { animate().xBy(15f) }
        .start()


fun FloatingActionButton.shake(vibrate: Boolean = false) =
    animate()
        .xBy(-15f)
        .withStartAction {
            if (vibrate) {
                context.getSystemService(Vibrator::class.java)?.vibrateCompat(500)
            }
        }
        .withEndAction { animate().xBy(15f) }
        .start()


fun Vibrator.vibrateCompat(millis: Long = 1L, amplitude: Int = 25) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrate(VibrationEffect.createOneShot(millis, amplitude))
    } else {
        vibrate(millis)
    }
}
