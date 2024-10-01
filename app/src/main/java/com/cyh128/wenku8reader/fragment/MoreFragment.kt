package com.cyh128.wenku8reader.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.AboutActivity
import com.cyh128.wenku8reader.activity.SettingActivity
import com.cyh128.wenku8reader.activity.UserInfoActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MoreFragment : Fragment() {
    private lateinit var view: View
    private lateinit var setting: CardView
    private lateinit var about: CardView
    private lateinit var userInfo: CardView
    private lateinit var disclaimers: CardView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        view = inflater.inflate(R.layout.fragment_more, container, false)
        setting = view.findViewById(R.id.cardView_frag_myinfo_setting)
        about = view.findViewById(R.id.cardView_frag_myinfo_about)
        userInfo = view.findViewById(R.id.cardView_frag_myinfo_userinfo)
        disclaimers = view.findViewById(R.id.cardView_frag_myinfo_disclaimers)

        userInfo.setOnClickListener { v: View? ->
            val toUserInfo: Intent = Intent(activity, UserInfoActivity::class.java)
            startActivity(toUserInfo)
        }
        setting.setOnClickListener { v: View? ->
            val toSetting: Intent = Intent(activity, SettingActivity::class.java)
            startActivity(toSetting)
        }
        about.setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    activity,
                    AboutActivity::class.java
                )
            )
        }
        disclaimers.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("免责声明")
                .setMessage(
                        "在使用本软件之前，请您仔细阅读以下内容，并确保您充分理解并同意以下条款：\n" +
                        "1、本软件为开源的第三方软件，可以免费下载用于测试相关功能，在测试完毕后应及时卸载本软件。\n" +
                        "2、本软件为第三方开源软件，不与轻小说文库(wenku8.cc)有任何关联。软件内所有内容均来自轻小说文库(wenku8.cc)公开在互联网的资源，仅供用户参考和学习使用，不得用于商业和非法用途。对于使用本软件所造成的任何后果，本软件作者概不负责。\n" +
                        "3、如果本软件存在侵犯您的合法权益的情况，请及时与作者联系，作者将会及时删除有关内容。\n" +
                        "4、本软件不会收集、存储、使用任何用户的个人信息，包括但不限于姓名、地址、电子邮件地址、电话号码等。在使用本软件过程中，不会进行任何形式的个人信息采集。如用户提供任何个人信息，将被视为用户已自愿提供，并且用户将自行承担由此产生的所有法律责任。\n" +
                        "5、本软件使用者应遵守国家相关法律法规和使用规范，不得利用本软件从事任何违法违规行为。如因使用本软件而导致的违法行为，使用者应承担相应的法律责任。\n" +
                        "6、本软件作者保留对免责声明的最终解释权。\n" +
                        "如您不同意本免责声明中的任何内容，请勿使用本软件。使用本软件即代表您已完全理解并同意上述内容。")
                .setPositiveButton("好的") { _, _ -> null }
                .show()
        }
        return view
    }
}
