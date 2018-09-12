package de.smartsquare.kickpi

import android.content.Context
import android.content.res.Resources
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.view.IconicsImageView

/**
 * @author Ruben Gees
 */
class LoadingIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : IconicsImageView(context, attrs, defStyleAttr) {

    private companion object {
        private const val ANIMATION_DURATION = 8000L
        private const val ROTATION = 1440f
    }

    init {
        this.icon = IconicsDrawable(context)
            .icon(CommunityMaterial.Icon.cmd_soccer)
            .colorRes(R.color.colorPrimary)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animateIndicator()
    }

    override fun onDetachedFromWindow() {
        ViewCompat.animate(this).cancel()

        super.onDetachedFromWindow()
    }

    private fun animateIndicator() {
        ViewCompat.animate(this)
            .x(Resources.getSystem().displayMetrics.widthPixels.toFloat())
            .rotationBy(ROTATION)
            .setDuration(ANIMATION_DURATION)
            .withEndAction {
                x = (-width).toFloat()

                animateIndicator()
            }
            .start()
    }
}
