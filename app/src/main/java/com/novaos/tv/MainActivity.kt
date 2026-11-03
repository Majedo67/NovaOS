
package com.novaos.tv

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*
import android.net.Uri
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    private val bg = Color.rgb(7, 12, 25)
    private val panel = Color.rgb(23, 34, 54)
    private val blue = Color.rgb(65, 170, 255)
    private val white = Color.WHITE
    private val grey = Color.rgb(180, 190, 210)

    private lateinit var clock: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        showHome()
    }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 5, 0, 5)
        }
    }

    private fun background(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = 22f
        }
    }

    private fun makeCard(
        title: String,
        subtitle: String = "",
        action: () -> Unit
    ): LinearLayout {

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(20, 12, 20, 12)
            background = background(panel)
            isFocusable = true
            isClickable = true

            addView(text(title, 18f, white, true))

            if (subtitle.isNotEmpty()) {
                addView(text(subtitle, 12f, grey))
            }

            setOnFocusChangeListener { view, focused ->
                view.animate()
                    .scaleX(if (focused) 1.07f else 1f)
                    .scaleY(if (focused) 1.07f else 1f)
                    .setDuration(160)
                    .start()

                view.background = background(
                    if (focused) Color.rgb(35, 75, 120)
                    else panel
                )
            }

            setOnClickListener { action() }
        }
    }

    private fun getApps(): List<ResolveInfo> {
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

    private fun launchApp(app: ResolveInfo) {
        val intent = packageManager.getLaunchIntentForPackage(
            app.activityInfo.packageName
        )

        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "App cannot be opened",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showHome() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(38, 20, 38, 20)
            setBackgroundColor(bg)
        }

        // TOP BAR

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        top.addView(text("NOVA", 30f, blue, true))

        val spacer = View(this)
        top.addView(
            spacer,
            LinearLayout.LayoutParams(0, 1, 1f)
        )

        clock = text("", 16f, grey)

        top.addView(clock)
        root.addView(top)

        updateClock()

        // NAVIGATION

        val navigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val navItems = listOf("Home", "Apps", "Games", "Settings")

        navItems.forEach { item ->

            val button = TextView(this).apply {
                text = item
                textSize = 16f
                setTextColor(white)
                setPadding(17, 14, 17, 14)
                isFocusable = true
                isClickable = true

                setOnClickListener {
                    when (item) {
                        "Home" -> showHome()
                        "Apps" -> showApps(false)
                        "Games" -> showApps(true)
                        "Settings" -> showSettings()
                    }
                }
            }

            navigation.addView(button)
        }

        root.addView(navigation)

        // MAIN CONTENT

        val scroll = ScrollView(this).apply {
            isVerticalScrollBarEnabled = false
            isSmoothScrollingEnabled = true
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // HERO BANNER

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(35, 20, 35, 20)

            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.rgb(25, 100, 190),
                    Color.rgb(18, 42, 90),
                    Color.rgb(9, 16, 35)
                )
            ).apply {
                cornerRadius = 28f
            }
        }

        hero.addView(text("WELCOME TO", 15f, blue, true))

        hero.addView(
            text("NOVA TV", 40f, white, true)
        )

        hero.addView(
            text(
                "Your entertainment. Your world.",
                17f,
                grey
            )
        )

        val explore = makeCard("Explore your apps") {
            showApps(false)
        }

        hero.addView(
            explore,
            LinearLayout.LayoutParams(230, 75)
        )

        content.addView(
            hero,
            LinearLayout.LayoutParams(-1, 230).apply {
                bottomMargin = 25
            }
        )

        // APP ROW

        addAppSection(content, "Your Apps", getApps())

        // GAMES ROW

        val games = getApps().filter {
            val name = it.loadLabel(packageManager).toString()
            name.contains("game", true)
        }

        addAppSection(content, "Games", games)

        content.addView(
            text(
                "NOVA TV  •  VERSION 3.0",
                12f,
                grey
            ).apply {
                setPadding(0, 30, 0, 15)
            }
        )

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        setContentView(root)
    }

    private fun updateClock() {
        if (::clock.isInitialized) {
            clock.text = SimpleDateFormat(
                "EEE, MMM d  •  HH:mm",
                Locale.getDefault()
            ).format(Date())
        }
    }

    private fun addAppSection(
        parent: LinearLayout,
        title: String,
        apps: List<ResolveInfo>
    ) {

        parent.addView(
            text(title, 25f, white, true).apply {
                setPadding(0, 12, 0, 12)
            }
        )

        val horizontal = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            isSmoothScrollingEnabled = true
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        apps.forEach { app ->

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                background = background(panel)
                setPadding(10, 10, 10, 10)
                isFocusable = true
                isClickable = true

                setOnFocusChangeListener { view, focused ->

                    view.animate()
                        .scaleX(if (focused) 1.10f else 1f)
                        .scaleY(if (focused) 1.10f else 1f)
                        .setDuration(170)
                        .start()

                    view.background = background(
                        if (focused)
                            Color.rgb(35, 75, 120)
                        else panel
                    )
                }

                setOnClickListener {
                    launchApp(app)
                }
            }

            val icon = ImageView(this).apply {
                setImageDrawable(app.loadIcon(packageManager))
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            card.addView(
                icon,
                LinearLayout.LayoutParams(76, 76)
            )

            card.addView(
                text(
                    app.loadLabel(packageManager).toString(),
                    13f,
                    white,
                    true
                ).apply {
                    gravity = Gravity.CENTER
                    maxLines = 2
                }
            )

            row.addView(
                card,
                LinearLayout.LayoutParams(145, 140).apply {
                    setMargins(7, 8, 12, 12)
                }
            )
        }

        if (apps.isEmpty()) {
            row.addView(
                text("No apps found", 15f, grey)
            )
        }

        horizontal.addView(row)

        parent.addView(
            horizontal,
            LinearLayout.LayoutParams(-1, 165)
        )
    }

    // APPS AND GAMES PAGE

    private fun showApps(gamesOnly: Boolean) {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35, 25, 35, 25)
            setBackgroundColor(bg)
        }

        root.addView(
            text(
                if (gamesOnly) "Games" else "All Apps",
                32f,
                white,
                true
            )
        )

        val back = makeCard("← Back to NovaOS") {
            showHome()
        }

        root.addView(
            back,
            LinearLayout.LayoutParams(240, 75)
        )

        val scroll = ScrollView(this)

        val list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val apps = getApps().filter { app ->

            if (!gamesOnly) {
                true
            } else {
                app.loadLabel(packageManager)
                    .toString()
                    .contains("game", true)
            }
        }

        apps.forEach { app ->

            val card = makeCard(
                app.loadLabel(packageManager).toString()
            ) {
                launchApp(app)
            }

            list.addView(
                card,
                LinearLayout.LayoutParams(-1, 75).apply {
                    bottomMargin = 9
                }
            )
        }

        if (apps.isEmpty()) {
            list.addView(
                text(
                    "No matching games found.",
                    16f,
                    grey
                )
            )
        }

        scroll.addView(list)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        setContentView(root)
        back.requestFocus()
    }

    // SETTINGS

    private fun showSettings() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35, 25, 35, 25)
            setBackgroundColor(bg)
        }

        root.addView(
            text("Settings", 34f, white, true)
        )

        val scroll = ScrollView(this)

        val list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        list.addView(
            makeCard("← Back to NovaOS") {
                showHome()
            }
        )

        list.addView(
            makeCard(
                "Appearance",
                "NovaOS dark theme"
            ) {
                Toast.makeText(
                    this,
                    "Dark theme is enabled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        list.addView(
            makeCard(
                "Refresh Apps",
                "Reload installed TV apps"
            ) {
                showHome()
            }
        )

        list.addView(
            makeCard(
                "Google TV",
                "Open your original TV home screen"
            ) {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                }
                startActivity(intent)
            }
        )

        list.addView(
            makeCard(
                "About NovaOS",
                "Version 3.0 • Android TV Edition"
            ) {
                AlertDialog.Builder(this)
                    .setTitle("NovaOS")
                    .setMessage(
                        "NovaOS 3.0\n\n" +
                        "Android TV launcher prototype.\n" +
                        "Created for Haier Google TV."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
        )

        list.addView(
            makeCard(
                "Android Settings",
                "Open device settings"
            ) {
                startActivity(
                    Intent(Settings.ACTION_SETTINGS)
                )
            }
        )

        scroll.addView(list)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        setContentView(root)
    }
}
