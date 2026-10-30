
package com.novaos.tv

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(5, 12, 30)
        window.navigationBarColor = Color.rgb(5, 12, 30)
        setContentView(createHome())
    }

    private fun createHome(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 30, 40, 30)
            setBackgroundColor(Color.rgb(5, 12, 30))
        }

        val title = TextView(this).apply {
            text = "NOVAOS"
            textSize = 38f
            setTextColor(Color.rgb(0, 210, 255))
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

        listOf("Games", "Apps", "Profiles", "Settings").forEach {
            val button = Button(this).apply {
                text = it
                isFocusable = true
                setOnClickListener {
                    Toast.makeText(
                        this@MainActivity,
                        "$it selected",
                        Toast.LENGTH_SHORT
                    ).show()
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

        listOf("GAME LIBRARY", "CONTINUE PLAYING", "DISCOVER").forEach {
            val card = TextView(this).apply {
                text = it
                textSize = 18f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.rgb(15, 55, 100))
                setPadding(25, 35, 25, 35)
                isFocusable = true
                setOnClickListener {
                    Toast.makeText(
                        this@MainActivity,
                        it,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            val params = LinearLayout.LayoutParams(
                230, 130
            )
            params.setMargins(0, 0, 20, 0)
            games.addView(card, params)
        }

        root.addView(games)
        return root
    }
}
