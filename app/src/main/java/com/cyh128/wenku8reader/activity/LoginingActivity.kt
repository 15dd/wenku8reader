package com.cyh128.wenku8reader.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.util.GlobalConfig
import com.cyh128.wenku8reader.util.LoginWenku8
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class LoginingActivity : AppCompatActivity() {
    //MainActivity
    private lateinit var username: String
    private lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logining)
        if (initDataBase()) {
            val login = Login()
            login.start()
        }
    }

    private val nameAndPassword: Boolean
        get() {
            try {
                val cursor = GlobalConfig.db.query("user_info", null, null, null, null, null, null)
                if (cursor.moveToNext()) {
                    for (i in 0 until cursor.count) {
                        cursor.move(i)
                        //int id = cursor.getInt(0);
                        username = cursor.getString(1)
                        password = cursor.getString(2)
                    }
                    cursor.close()
                    return true
                }
                return false
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    private inner class Login : Thread() {
        override fun run() {
            try {
                if (!nameAndPassword) { //如果数据库中没有保存用户名和密码时，跳转到用户名和密码输入界面
                    val gotoinput = Intent(this@LoginingActivity, LoginInputActivity::class.java)
                    startActivity(gotoinput)
                    finish()
                    return
                }
                val flag = LoginWenku8.login(username, password)
                if (flag) {
                    val toMainAppUI = Intent(this@LoginingActivity, AppActivity::class.java)
                    startActivity(toMainAppUI)
                    finish()
                } else {
                    val gotoinput = Intent(this@LoginingActivity, LoginInputActivity::class.java)
                    startActivity(gotoinput)
                    finish()
                }
            } catch (e: Exception) { //当登录错误时
                e.printStackTrace()
                runOnUiThread {
                    MaterialAlertDialogBuilder(this@LoginingActivity)
                        .setCancelable(false) //禁止点击其他区域
                        .setTitle("网络错误")
                        .setMessage("可能是以下原因造成的:\n\n1 -> 请检查是否正在连接VPN或代理服务器\n2 -> 未连接上网络\n3 -> 服务器(wenku8.cc)出错，(此网站有时会登不上去)\n\n请稍后再试")
                        .setPositiveButton(
                            "重启软件"
                        ) { dialogInterface: DialogInterface?, i: Int ->
                            val intent: Intent? = packageManager.getLaunchIntentForPackage(
                                packageName
                            )
                            intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        }
                        .setIcon(R.drawable.warning)
                        .show()
                }
            }
        }
    }

    private fun initDataBase(): Boolean { //初始化数据库，读取数据
        GlobalConfig.db = openOrCreateDatabase("info.db", MODE_PRIVATE, null)
        //下面是为了防止没有对应的table时报错，一般出现在删除数据库时
        GlobalConfig.db.execSQL("CREATE TABLE IF NOT EXISTS old_reader_read_history(bookUrl TEXT PRIMARY KEY,indexUrl TEXT UNIQUE NOT NULL,title TEXT NOT NULL,location INT NOT NULL)")
        GlobalConfig.db.execSQL("CREATE TABLE IF NOT EXISTS new_reader_read_history(bookUrl TEXT PRIMARY KEY,indexUrl TEXT UNIQUE NOT NULL,title TEXT NOT NULL,location INT NOT NULL)")
        GlobalConfig.db.execSQL("CREATE TABLE IF NOT EXISTS user_info(_id INTEGER PRIMARY KEY autoincrement,username TEXT,password TEXT)")
        GlobalConfig.db.execSQL("CREATE TABLE IF NOT EXISTS setting(_id INTEGER UNIQUE,checkUpdate BOOLEAN NOT NULL,bookcaseViewType BOOLEAN NOT NULL,readerMode INTEGER NOT NULL)")
        GlobalConfig.db.execSQL("CREATE TABLE IF NOT EXISTS reader(_id INTEGER UNIQUE,newFontSize FLOAT NOT NULL,newLineSpacing FLOAT NOT NULL,oldFontSize FLOAT NOT NULL,oldLineSpacing FLOAT NOT NULL,bottomTextSize FLOAT NOT NULL,isUpToDown BOOLEAN NOT NULL,canSwitchChapterByScroll BOOLEAN NOT NULL,backgroundColorDay TEXT NOT NULL,backgroundColorNight TEXT NOT NULL,textColorDay TEXT NOT NULL,textColorNight TEXT NOT NULL)")
        return try {
            val sql = "select * from setting where _id=1"
            val cursor = GlobalConfig.db.rawQuery(sql, null)
            if (cursor.moveToNext()) {
                for (i in 0 until cursor.count) {
                    cursor.move(i)
                    GlobalConfig.checkUpdate = cursor.getInt(1) == 1
                    GlobalConfig.bookcaseViewType = cursor.getInt(2) == 1
                    GlobalConfig.readerMode = cursor.getInt(3)
                }
                cursor.close()
            } else {
                GlobalConfig.checkUpdate = true
                GlobalConfig.bookcaseViewType = false
                GlobalConfig.readerMode = 1
            }
            val sql2 = "select * from reader where _id=1"
            val cursor2 = GlobalConfig.db.rawQuery(sql2, null)
            if (cursor2.moveToNext()) {
                for (i in 0 until cursor2.count) {
                    cursor2.move(i)
                    GlobalConfig.newReaderFontSize = cursor2.getFloat(1)
                    GlobalConfig.newReaderLineSpacing = cursor2.getFloat(2)
                    GlobalConfig.oldReaderFontSize = cursor2.getFloat(3)
                    GlobalConfig.oldReaderLineSpacing = cursor2.getFloat(4)
                    GlobalConfig.readerBottomTextSize = cursor2.getFloat(5)
                    GlobalConfig.isUpToDown = cursor2.getInt(6) == 1
                    GlobalConfig.canSwitchChapterByScroll = cursor2.getInt(7) == 1
                    GlobalConfig.backgroundColorDay = cursor2.getString(8)
                    GlobalConfig.backgroundColorNight = cursor2.getString(9)
                    GlobalConfig.textColorDay = cursor2.getString(10)
                    GlobalConfig.textColorNight = cursor2.getString(11)
                }
                cursor2.close()
            } else {
                GlobalConfig.newReaderFontSize = 30 + 20f
                GlobalConfig.newReaderLineSpacing = 1.5f
                GlobalConfig.oldReaderFontSize = 16f
                GlobalConfig.oldReaderLineSpacing = 1f
                GlobalConfig.readerBottomTextSize = 50f
                GlobalConfig.isUpToDown = false
                GlobalConfig.canSwitchChapterByScroll = true
                GlobalConfig.backgroundColorDay = "#FFFFFF"
                GlobalConfig.backgroundColorNight = "#000000"
                GlobalConfig.textColorDay = "#000000"
                GlobalConfig.textColorNight = "#FFFFFF"
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                MaterialAlertDialogBuilder(this)
                    .setIcon(R.drawable.database_error)
                    .setTitle("数据库出现问题")
                    .setMessage("数据库读取出现问题，可能是您升级过软件，点击重启软件，软件会自行修复\n注意：该过程会清除设置数据、登录状态、本地阅读记录")
                    .setCancelable(false)
                    .setPositiveButton(
                        "重启软件"
                    ) { dialog, which ->
                        cleanDatabases() //删除数据库
                        val intent = packageManager.getLaunchIntentForPackage(packageName)
                        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    .show()
            }
            false
        }
    }

    private fun cleanDatabases() { //https://www.jianshu.com/p/603329500679
        deleteFilesByDirectory(File("/data/data/$packageName/databases"))
    }

    private fun deleteFilesByDirectory(directory: File) {
        if (directory.exists() && directory.isDirectory) {
            for (item in directory.listFiles()!!) {
                item.delete()
            }
        }
    }
}
