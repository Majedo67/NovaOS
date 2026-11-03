
package com.novaos.tv

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*

class MainActivity : Activity() {

    private val cyan = Color.rgb(40, 210, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(5, 12, 27)
        window.navigationBarColor = Color.rgb(5, 12, 27)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(45, 35, 35, 25)
            setBackgroundColor(Color.rgb(5, 12, 27))
        }

        val title = TextView(this).apply {
            text = "NOVA TV"
            textSize = 38f
            setTextColor(cyan)
            typeface = Typeface.DEFAULT_BOLD
        }

        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "YOUR ENTERTAINMENT. YOUR WORLD."
            textSize = 14f
            setTextColor(Color.LTGRAY)
        }

        root.addView(subtitle)

        val heading = TextView(this).apply {
            text = "  YOUR APPS"
            textSize = 23f
            setTextColor(Color.WHITE)
            setPadding(0, 35, 0, 15)
        }

        root.addView(heading)

        val scroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            isSmoothScrollingEnabled = true
        }

        val appRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }

        val activities = packageManager
            .queryIntentActivities(intent, 0)
            .distinctBy { it.activityInfo.packageName }
            .sortedBy {
                it.loadLabel(packageManager).toString()
            }

        activities.forEach { info ->

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(12, 15, 12, 15)
                background = GradientDrawable().apply {
                    setColor(Color.rgb(19, 35, 59))
                    cornerRadius = 22f
                }
                isFocusable = true
                isClickable = true
            }

            val icon = ImageView(this).apply {
                setImageDrawable(info.loadIcon(packageManager))
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            card.addView(icon, LinearLayout.LayoutParams(75, 75))

            val name = TextView(this).apply {
                text = info.loadLabel(packageManager).toString()
                textSize = 15f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                maxLines = 2
                setPadding(0, 12, 0, 0)
            }

            card.addView(name)

            card.setOnFocusChangeListener { view, focused ->
                view.animate()
                    .scaleX(if (focused) 1.09f else 1f)
                    .scaleY(if (focused) 1.09f else 1f)
                    .setDuration(160)
                    .setInterpolator(DecelerateInterpolator())
                    .start()

                view.background = GradientDrawable().apply {
                    setColor(
                        if (focused) Color.rgb(27, 65, 99)
                        else Color.rgb(19, 35, 59)
                    )
                    cornerRadius = 22f
                    if (focused) {
                        setStroke(3, cyan)
                    }
                }
            }

            card.setOnClickListener {
                val launch = packageManager
                    .getLaunchIntentForPackage(
                        info.activityInfo.packageName
                    )
                launch?.let { startActivity(it) }
            }

            val params = LinearLayout.LayoutParams(175, 160)
            params.setMargins(8, 8, 18, 8)

            appRow.addView(card, params)
        }

        scroll.addView(appRow)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200
            )
        )

        val footer = TextView(this).apply {
            text = "NOVA TV  •  ANDROID EDITION"
            textSize = 12f
            setTextColor(Color.GRAY)
            setPadding(0, 30, 0, 0)
        }

        root.addView(footer)

        setContentView(root)

        if (activities.isNotEmpty()) {
            appRow.getChildAt(0).requestFocus()
        }
    }
}
