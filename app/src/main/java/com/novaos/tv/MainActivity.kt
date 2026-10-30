
package com.novaos.tv

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private val backgroundColor = Color.rgb(5, 12, 30)
    private val cyanColor = Color.rgb(0, 210, 255)
    private val cardColor = Color.rgb(15, 55, 100)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        setContentView(createHome())
    }

    private fun createHome(): View {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 30, 40, 30)
            setBackgroundColor(backgroundColor)
            gravity = Gravity.TOP
        }

        val title = TextView(this).apply {
            text = "NOVAOS"
            textSize = 38f
            setTextColor(cyanColor)
            isFocusable = true
        }

        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "YOUR GAMING UNIVERSE"
            textSize = 14f
            setTextColor(Color.WHITE)
        }

        root.addView(subtitle)

        val menu = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 30, 0, 20)
        }

        val menuItems = listOf(
            "Games",
            "Apps",
            "Profiles",
            "Settings"
        )

        menuItems.forEach { item ->

            val button = Button(this).apply {
                text = item
                isFocusable = true

                setOnClickListener {
                    subtitle.text = "NOVAOS  /  $item"
                }
            }

            menu.addView(button)
        }

        root.addView(menu)

        val heading = TextView(this).apply {
            text = "FEATURED"
            textSize = 22f
            setTextColor(Color.WHITE)
            setPadding(0, 25, 0, 15)
        }

        root.addView(heading)

        val games = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val gameItems = listOf(
            "GAME LIBRARY",
            "CONTINUE PLAYING",
            "DISCOVER"
        )

        gameItems.forEach { item ->

            val card = TextView(this).apply {
                text = item
                textSize = 18f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                setBackgroundColor(cardColor)
                setPadding(25, 35, 25, 35)
                isFocusable = true

                setOnClickListener {
                    heading.text = item
                }
            }

            val params = LinearLayout.LayoutParams(
                230,
                130
            )

            params.setMargins(0, 0, 20, 0)

            games.addView(card, params)
        }

        root.addView(games)

        return root
    }
}
