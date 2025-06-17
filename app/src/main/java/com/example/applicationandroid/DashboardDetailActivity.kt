package com.example.applicationandroid

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.applicationandroid.R

class DashboardDetailActivity : AppCompatActivity() {

    private lateinit var scrollDetails: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_detail)

        // Toolbar with back arrow
        val toolbar = findViewById<Toolbar>(R.id.toolbarDetail)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Logout button
        val btnLogout = findViewById<ImageButton>(R.id.btnLogoutDetail)
        btnLogout.setOnClickListener {
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                .also { startActivity(it) }
            finish()
        }

        // Scroll-to-top and dashboard buttons
        scrollDetails = findViewById(R.id.scrollDetails)
        findViewById<ImageView>(R.id.imgDetailLogoBottom).setOnClickListener {
            scrollDetails.post { scrollDetails.fullScroll(ScrollView.FOCUS_UP) }
        }
        findViewById<ImageView>(R.id.imgDashboardLogoBottom).setOnClickListener {
            Intent(this, DashboardActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .also { startActivity(it) }
            finish()
        }

        // Populate details
        val container = findViewById<LinearLayout>(R.id.containerDetails)
        val fields = intent.getSerializableExtra("fields") as? Map<String, String> ?: emptyMap()
        fields.forEach { (key, value) ->
            val label = "${key.replaceFirstChar { it.uppercase() }}: "
            val spannable = SpannableString(label + value).apply {
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    0, label.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            TextView(this).apply {
                text = spannable
                textSize = 16f
                setPadding(0, 8, 0, 8)
            }.also { container.addView(it) }
        }
    }
}