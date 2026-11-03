
package com.novaos.tv

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*
import android.content.pm.ResolveInfo

class MainActivity : Activity() {

    private val bg = Color.rgb(8, 12, 24)
    private val white = Color.WHITE
    private val muted = Color.rgb(175, 185, 205)
    private val blue = Color.rgb(55, 160, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(45, 22, 45, 15)
            setBackgroundColor(bg)
        }

        // TOP NAVIGATION
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val logo = text("NOVA", 30f, blue, true)
        nav.addView(logo)

        val spacer = View(this)
        nav.addView(spacer, LinearLayout.LayoutParams(0, 1, 1f))

        listOf("Home", "Apps", "Games", "Settings").forEach { item ->
            val button = TextView(this).apply {
                text = item
                textSize = 17f
                setTextColor(white)
                gravity = Gravity.CENTER
                setPadding(18, 12, 18, 12)
                isFocusable = true
                isClickable = true
                setOnClickListener {
                    when (item) {
                        "Apps" -> scrollToApps()
                        "Games" -> scrollToGames()
                        "Settings" -> openSettings()
                    }
                }
            }
            nav.addView(button)
        }

        root.addView(nav)

        // MAIN SCROLL
        val mainScroll = ScrollView(this).apply {
            isFillViewport = true
            isVerticalScrollBarEnabled = false
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 20, 0, 30)
        }

        // CINEMATIC HERO
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(35, 25, 35, 25)
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.rgb(20, 75, 130),
                    Color.rgb(12, 27, 60),
                    Color.rgb(8, 12, 24)
                )
            ).apply {
                cornerRadius = 28f
            }
        }

        hero.addView(text("NOVA ORIGINAL", 14f, blue, true))
        hero.addView(text("Entertainment,\nreimagined.", 38f, white, true))
        hero.addView(text(
            "Everything you love. All in one place.",
            17f, muted, false
        ))

        val explore = makeButton("EXPLORE YOUR APPS")
        explore.setOnClickListener { scrollToApps() }

        hero.addView(explore)

        content.addView(hero, LinearLayout.LayoutParams(
            -1, 230
        ).apply {
            bottomMargin = 28
        })

        // APPS
        val apps = getTVApps()

        addSection(content, "Your Apps", apps)

        val games = apps.filter {
            val name = it.loadLabel(packageManager).toString()
            name.contains("game", true)
        }

        addSection(content, "Games", games)

        // FOOTER
        content.addView(text(
            "NOVA TV  •  YOUR ENTERTAINMENT WORLD",
            13f, muted, false
        ).apply {
            setPadding(0, 35, 0, 10)
        })

        mainScroll.addView(content)
        root.addView(mainScroll)

        setContentView(root)
    }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 5, 0, 5)
        }
    }

    private fun makeButton(label: String): Button {
        return Button(this).apply {
            text = label
            textSize = 14f
            isFocusable = true
        }
    }

    private fun getTVApps(): List<ResolveInfo> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }

        return packageManager.queryIntentActivities(intent, 0)
            .filter {
                it.activityInfo.packageName != packageName
            }
            .distinctBy {
                it.activityInfo.packageName
            }
            .sortedBy {
                it.loadLabel(packageManager).toString()
            }
    }

    private fun addSection(
        parent: LinearLayout,
        title: String,
        apps: List<ResolveInfo>
    ) {
        parent.addView(text(title, 25f, white, true).apply {
            setPadding(0, 12, 0, 12)
        })

        val row = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            isSmoothScrollingEnabled = true
        }

        val cards = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        apps.forEach { app ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(12, 12, 12, 12)
                isFocusable = true
                isClickable = true

                background = rounded(Color.rgb(24, 34, 55))

                setOnFocusChangeListener { view, focused ->
                    view.animate()
                        .scaleX(if (focused) 1.10f else 1f)
                        .scaleY(if (focused) 1.10f else 1f)
                        .setDuration(170)
                        .start()

                    view.background = rounded(
                        if (focused) Color.rgb(35, 75, 115)
                        else Color.rgb(24, 34, 55)
                    )
                }

                setOnClickListener {
                    val launch = packageManager
                        .getLaunchIntentForPackage(
                            app.activityInfo.packageName
                        )
                    launch?.let { startActivity(it) }
                }
            }

            val icon = ImageView(this).apply {
                setImageDrawable(app.loadIcon(packageManager))
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            card.addView(icon, LinearLayout.LayoutParams(78, 78))

            card.addView(text(
                app.loadLabel(packageManager).toString(),
                14f, white, true
            ).apply {
                gravity = Gravity.CENTER
                maxLines = 2
            })

            cards.addView(card, LinearLayout.LayoutParams(
                145, 140
            ).apply {
                setMargins(7, 7, 15, 14)
            })
        }

        if (apps.isEmpty()) {
            cards.addView(text(
                "No apps found in this section",
                15f, muted, false
            ))
        }

        row.addView(cards)

        parent.addView(row, LinearLayout.LayoutParams(
            -1, 165
        ).apply {
            bottomMargin = 15
        })
    }

    private fun rounded(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = 20f
        }
    }

    private fun scrollToApps() {
        Toast.makeText(this, "Your apps are below", Toast.LENGTH_SHORT).show()
    }

    private fun scrollToGames() {
        Toast.makeText(this, "Your games are below", Toast.LENGTH_SHORT).show()
    }

    private fun openSettings() {
        startActivity(Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        ))
    }
}
