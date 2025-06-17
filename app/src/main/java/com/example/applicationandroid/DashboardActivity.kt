package com.example.applicationandroid

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.applicationandroid.R
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DashboardAdapter
    private val itemList = mutableListOf<DashboardItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarDashboard)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { goToLogin() }

        // Welcome text
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val username = intent.getStringExtra("username") ?: "User"
        tvWelcome.text = getString(R.string.welcome_fmt, username)

        // RecyclerView + scroll-to-top
        recyclerView = findViewById(R.id.recyclerViewItems)
        adapter = DashboardAdapter(itemList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<ImageView>(R.id.imgScrollTop).setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        // Fetch data
        fetchDashboardItems(
            keypass = intent.getStringExtra("keypass") ?: "",
            username = username,
            password = intent.getStringExtra("password") ?: ""
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                goToLogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    private fun fetchDashboardItems(
        keypass: String,
        username: String,
        password: String
    ) {
        RetrofitClient.apiService
            .getDashboardItems(keypass.lowercase(), username, password)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful && response.body() != null) {
                        val entities: JsonArray = response.body()!!.getAsJsonArray("entities") ?: JsonArray()
                        itemList.clear()
                        entities.forEach { elem ->
                            val obj = elem.asJsonObject
                            val map = obj.entrySet().associate { (k, v) ->
                                k to if (v.isJsonPrimitive) v.asString else v.toString()
                            }
                            itemList.add(DashboardItem(map))
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@DashboardActivity, "Failed to load", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@DashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}