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

class MoreFragment : Fragment() {
    private lateinit var view: View
    private lateinit var setting: CardView
    private lateinit var about: CardView
    private lateinit var userInfo: CardView
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
        return view
    }
}
