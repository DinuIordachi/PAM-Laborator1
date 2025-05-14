package com.iordachi.laborator1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.iordachi.laborator1.databinding.ActivityMainBinding
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private var hasNotificationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launchNotificationPermissions()

        val searchInput = binding.searchInput
        val btnSearch = binding.btnSearch
        val btnAdd = binding.btnAdd
        val btnClear = binding.btnClear
        val btnNotify = binding.btnNotify
        val recyclerView = binding.recyclerView

        val adapter = TaskAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.tasks.observe(this) {
            adapter.submitList(it)
        }

        btnSearch.setOnClickListener {
            val query = searchInput.text.toString()
            if (query.isNotEmpty()) {
                val url = "https://www.google.com/search?q=${Uri.encode(query)}"
                startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
            }
        }

        btnAdd.setOnClickListener {
            val query = searchInput.text.toString()
            viewModel.addTask(Task(title = query, description = "Descriere pentru $query"))
        }

        btnClear.setOnClickListener {
            viewModel.clearTasks()
        }

        btnNotify.setOnClickListener {
            // Trigger permission launcher
            launchNotificationPermissions()

            Handler(Looper.getMainLooper()).postDelayed({
                NotificationUtil.showNotification(this)
            }, 10000)
        }
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                // If permission not granted and it's Android API >= 33, show rationale materialUI
                if (Build.VERSION.SDK_INT >= 33) {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                        showNotificationPermissionRationale()
                    } else {
                        // if Android API < 33 show classic dialog that redirects to settings
                        showSettingDialog()
                    }
                }
            } else {
                Toast
                    .makeText(this, "notification permission granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = "package:$packageName".toUri()
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                launchNotificationPermissions()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun launchNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Trigger permission request from caller
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)

                return
            }
        } else {
            hasNotificationPermissionGranted = true
        }
    }
}