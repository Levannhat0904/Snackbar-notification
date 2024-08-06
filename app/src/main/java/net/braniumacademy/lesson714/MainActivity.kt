package net.braniumacademy.lesson714

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var containerLayout: View

    //    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            setupNotificationBuilder()
//        } else {
//            Snackbar.make(
//                containerLayout, R.string.txt_permission_denied,
//                Snackbar.LENGTH_LONG
//            ).setAction("Request") {
//                checkPostNotificationPermission()
//            }.show()
//        }
//    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupNotificationBuilder()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                // Hiển thị thông báo yêu cầu quyền và hướng dẫn người dùng cấp quyền
                Snackbar.make(
                    containerLayout, R.string.txt_permission_denied,
                    Snackbar.LENGTH_LONG
                ).setAction("Request") {
                    checkPostNotificationPermission()
                }.show()
            } else {
                Snackbar.make(
                    containerLayout, R.string.txt_permission_denied,
                    Snackbar.LENGTH_LONG
                ).setAction("Settings") {
                    // Mở màn hình cài đặt của ứng dụng để người dùng cấp quyền thủ công
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }.show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        val btnPost = findViewById<FloatingActionButton>(R.id.fbtn_post_notification)
        containerLayout = findViewById(R.id.container_layout)
        btnPost.setOnClickListener {
            checkPostNotificationPermission()
        }
    }

    private fun checkPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            return
        } else {
            setupNotificationBuilder()
        }
    }

    private fun xcheckPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Quyền đã được cấp, thực hiện hành động
                    setupNotificationBuilder()
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    // Hiển thị lý do vì sao cần quyền
                    Snackbar.make(
                        containerLayout, R.string.txt_permission_required,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("OK") {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }.show()
                }

                else -> {
                    // Quyền bị từ chối và không được hỏi lại
                    Snackbar.make(
                        containerLayout, R.string.txt_permission_denied,
                        Snackbar.LENGTH_LONG
                    ).setAction("Settings") {
                        // Mở màn hình cài đặt của ứng dụng để người dùng cấp quyền thủ công
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:$packageName")
                        )
                        startActivity(intent)
                    }.show()
                }
            }
        } else {
            // Dưới Android 13, không cần xin quyền
            setupNotificationBuilder()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            // đăng ký channel với hệ thống
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupNotificationBuilder() {
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_cat)
        val largeImage = BitmapFactory.decodeResource(resources, R.drawable.cat1)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setLargeIcon(largeIcon)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(largeImage)
                    .bigLargeIcon(null as Bitmap?)
            )
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
        val infoSnackbar =
            Snackbar.make(containerLayout, R.string.text_inform, Snackbar.LENGTH_SHORT)
        infoSnackbar.show()
    }

    companion object {
        const val CHANNEL_ID = "channel_1"
    }
}