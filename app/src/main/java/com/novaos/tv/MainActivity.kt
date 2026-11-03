package com.novaos.tv

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    private val bg = Color.rgb(7, 12, 25)
    private val panel = Color.rgb(20, 29, 49)
    private val blue = Color.rgb(35, 130, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(160, 175, 200)

    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var clock: TextView
    private var currentPage = "Home"

    data class TVApp(
        val name: String,
        val packageName: String,
        val icon: android.graphics.drawable.Drawable,
        val intent: Intent
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun rounded(color: Int, radius: Float = 22f): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius
        }
    }

    private fun gradient(): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(15, 75, 155),
                Color.rgb(20, 35, 75),
                Color.rgb(12, 18, 35)
            )
        ).apply { cornerRadius = 30f }
    }

    private fun makeText(
        value: String,
        size: Float,
        color: Int = white,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }
    }

    private fun makeButton(
        label: String,
        action: () -> Unit
    ): TextView {
        return TextView(this).apply {
            text = label
            textSize = 17f
            setTextColor(white)
            gravity = Gravity.CENTER
            setPadding(24, 14, 24, 14)
            background = rounded(panel, 40f)
            isFocusable = true
            isClickable = true

            setOnFocusChangeListener { view, focused ->
                view.animate()
                    .scaleX(if (focused) 1.08f else 1f)
                    .scaleY(if (focused) 1.08f else 1f)
                    .setDuration(140)
                    .start()

                setBackground(
                    rounded(if (focused) blue else panel, 40f)
                )
            }

            setOnClickListener { action() }
        }
    }

    private fun setupPage(title: String) {
        currentPage = title

        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(38, 24, 38, 20)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        top.addView(
            makeText("NOVA", 29f, blue, true),
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        clock = makeText("", 16f, muted)
        top.addView(clock)

        root.addView(top)

        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 18, 0, 18)
        }

        listOf("Home", "Apps", "Games", "Settings").forEach { page ->
            val button = makeButton(
                if (page == currentPage) "●  $page" else page
            ) {
                when (page) {
                    "Home" -> showHome()
                    "Apps" -> showApps()
                    "Games" -> showGames()
                    "Settings" -> showSettings()
                }
            }

            val params = LinearLayout.LayoutParams(-2, -2)
            params.marginEnd = 12
            nav.addView(button, params)
        }

        root.addView(nav)

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            isFocusable = false
        }

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 8, 0, 35)
        }

        scroll.addView(content)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
        updateClock()
    }

    private fun updateClock() {
        if (::clock.isInitialized) {
            clock.text = SimpleDateFormat(
                "EEE, MMM d  •  HH:mm",
                Locale.getDefault()
            ).format(Date())
        }
    }

    private fun addHeading(title: String, subtitle: String? = null) {
        content.addView(
            makeText(title, 25f, white, true).apply {
                setPadding(0, 22, 0, 8)
            }
        )

        if (subtitle != null) {
            content.addView(
                makeText(subtitle, 14f, muted).apply {
                    setPadding(0, 0, 0, 12)
                }
            )
        }
    }

    private fun getApps(): List<TVApp> {
        val result = mutableListOf<TVApp>()

        val leanback = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }

        val normal = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val found = packageManager.queryIntentActivities(leanback, 0)
        val all = if (found.isNotEmpty()) {
            found
        } else {
            packageManager.queryIntentActivities(normal, 0)
        }

        val seen = mutableSetOf<String>()

        for (info in all) {
            try {
                val packageName = info.activityInfo.packageName
                if (packageName == packageNameOfSelf()) continue
                if (!seen.add(packageName)) continue

                val launch = packageManager.getLaunchIntentForPackage(packageName)
                    ?: continue

                result.add(
                    TVApp(
                        info.loadLabel(packageManager).toString(),
                        packageName,
                        info.loadIcon(packageManager),
                        launch
                    )
                )
            } catch (_: Exception) {
            }
        }

        return result.sortedBy { it.name.lowercase(Locale.getDefault()) }
    }

    private fun packageNameOfSelf(): String = packageName

    private fun launchApp(app: TVApp) {
        try {
            startActivity(app.intent)
        } catch (_: Exception) {
            Toast.makeText(this, "App could not be opened", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addAppRow(apps: List<TVApp>) {
        if (apps.isEmpty()) {
            content.addView(
                makeText("No compatible apps found.", 16f, muted)
            )
            return
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        apps.forEach { app ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(12, 16, 12, 16)
                background = rounded(panel)
                isFocusable = true
                isClickable = true

                setOnFocusChangeListener { view, focused ->
                    view.animate()
                        .scaleX(if (focused) 1.09f else 1f)
                        .scaleY(if (focused) 1.09f else 1f)
                        .setDuration(150)
                        .start()

                    background = rounded(
                        if (focused) Color.rgb(35, 75, 130) else panel
                    )
                }

                setOnClickListener { launchApp(app) }
            }

            val icon = ImageView(this).apply {
                setImageDrawable(app.icon)
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            card.addView(icon, LinearLayout.LayoutParams(76, 76))
            card.addView(
                makeText(app.name, 13f, white, true).apply {
                    gravity = Gravity.CENTER
                    maxLines = 2
                    setPadding(0, 10, 0, 0)
                }
            )

            val params = LinearLayout.LayoutParams(140, 145)
            params.marginEnd = 16
            row.addView(card, params)
        }

        content.addView(
            HorizontalScrollView(this).apply {
                isHorizontalScrollBarEnabled = false
                addView(row)
            }
        )
    }

    private fun showHome() {
        setupPage("Home")

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(35, 25, 35, 25)
            background = gradient()
            minimumHeight = 220
        }

        hero.addView(makeText("WELCOME TO", 14f, Color.LTGRAY, true))
        hero.addView(
            makeText("NovaOS", 42f, white, true).apply {
                setPadding(0, 6, 0, 8)
            }
        )
        hero.addView(
            makeText(
                "Your entertainment. Your home.",
                17f,
                Color.rgb(220, 230, 255)
            )
        )

        hero.addView(
            makeButton("Explore your apps  →") {
                showApps()
            }.apply {
                setBackgroundColor(blue)
            },
            LinearLayout.LayoutParams(-2, -2).apply {
                topMargin = 22
            }
        )

        content.addView(hero)

        val apps = getApps()
        addHeading("Continue watching", "Open an app from your TV")
        addAppRow(apps.take(12))

        addHeading("Your games")
        val games = apps.filter {
            it.name.contains("game", true)
        }
        addAppRow(games)

        content.addView(
            makeButton("Open Google TV Home") {
                openGoogleTV()
            }.apply {
                setPadding(20, 20, 20, 20)
            },
            LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = 25
            }
        )
    }

    private fun showApps() {
        setupPage("Apps")
        addHeading("All apps", "Installed apps available to launch")
        addAppRow(getApps())
    }

    private fun showGames() {
        setupPage("Games")
        addHeading("Games", "Your installed games")

        val games = getApps().filter {
            it.name.contains("game", true)
        }

        addAppRow(games)

        if (games.isEmpty()) {
            content.addView(
                makeText(
                    "No games were detected by name. Check All Apps for your games.",
                    16f,
                    muted
                )
            )
        }
    }

    private fun showSettings() {
        setupPage("Settings")
        addHeading("Settings", "Customize your NovaOS experience")

        content.addView(
            makeButton("Refresh installed apps") {
                showSettings()
                Toast.makeText(this, "App list refreshed", Toast.LENGTH_SHORT).show()
            }.apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = 12
            }
        )

        content.addView(
            makeButton("Open Android TV Settings") {
                try {
                    startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                } catch (_: Exception) {
                    Toast.makeText(this, "Settings unavailable", Toast.LENGTH_SHORT).show()
                }
            },
            LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = 12
            }
        )

        content.addView(
            makeButton("Open Google TV Home") {
                openGoogleTV()
            },
            LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = 12
            }
        )

        content.addView(
            makeButton("About NovaOS") {
                AlertDialog.Builder(this)
                    .setTitle("NovaOS")
                    .setMessage(
                        "NovaOS is an Android TV launcher concept.\n\n" +
                        "It runs on top of Android TV and does not replace Google TV or install Linux."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            },
            LinearLayout.LayoutParams(-1, -2)
        )
    }

    private fun openGoogleTV() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(
                this,
                "Android TV Home could not be opened",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onBackPressed() {
        if (currentPage != "Home") {
            showHome()
        } else {
            super.onBackPressed()
        }
    }
}
