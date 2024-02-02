package com.cyh128.wenku8reader.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cyh128.wenku8reader.R
import com.google.android.material.appbar.MaterialToolbar

class TagSelectActivity : AppCompatActivity() {
    private var tag: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_select)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_act_tag_select)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        chipClickListener()
    }

    private fun chipClickListener() {
        findViewById<View>(R.id.chip_tag_xy).setOnClickListener { v: View? ->
            tag = "校园"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_qc).setOnClickListener { v: View? ->
            tag = "青春"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_la).setOnClickListener { v: View? ->
            tag = "恋爱"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_zy).setOnClickListener { v: View? ->
            tag = "治愈"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_qx).setOnClickListener { v: View? ->
            tag = "群像"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_jj).setOnClickListener { v: View? ->
            tag = "竞技"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_yy).setOnClickListener { v: View? ->
            tag = "音乐"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_ms).setOnClickListener { v: View? ->
            tag = "美食"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_lx).setOnClickListener { v: View? ->
            tag = "旅行"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_hlx).setOnClickListener { v: View? ->
            tag = "欢乐向"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_jy).setOnClickListener { v: View? ->
            tag = "经营"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_zc).setOnClickListener { v: View? ->
            tag = "职场"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_dz).setOnClickListener { v: View? ->
            tag = "斗智"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_nd).setOnClickListener { v: View? ->
            tag = "脑洞"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_zwh).setOnClickListener { v: View? ->
            tag = "宅文化"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_cy).setOnClickListener { v: View? ->
            tag = "穿越"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_qh).setOnClickListener { v: View? ->
            tag = "奇幻"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_mf).setOnClickListener { v: View? ->
            tag = "魔法"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_yn).setOnClickListener { v: View? ->
            tag = "异能"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_zd).setOnClickListener { v: View? ->
            tag = "战斗"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_kh).setOnClickListener { v: View? ->
            tag = "科幻"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_jz).setOnClickListener { v: View? ->
            tag = "机战"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_zz).setOnClickListener { v: View? ->
            tag = "战争"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_mx).setOnClickListener { v: View? ->
            tag = "冒险"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_lat).setOnClickListener { v: View? ->
            tag = "龙傲天"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_xy2).setOnClickListener { v: View? ->
            tag = "悬疑"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_fz).setOnClickListener { v: View? ->
            tag = "犯罪"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_fc).setOnClickListener { v: View? ->
            tag = "复仇"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_ha).setOnClickListener { v: View? ->
            tag = "黑暗"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_lq).setOnClickListener { v: View? ->
            tag = "猎奇"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_js).setOnClickListener { v: View? ->
            tag = "惊悚"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_jd).setOnClickListener { v: View? ->
            tag = "间谍"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_mr).setOnClickListener { v: View? ->
            tag = "末日"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_yx).setOnClickListener { v: View? ->
            tag = "游戏"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_dts).setOnClickListener { v: View? ->
            tag = "大逃杀"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_qmzm).setOnClickListener { v: View? ->
            tag = "青梅竹马"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_mm).setOnClickListener { v: View? ->
            tag = "妹妹"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_ne).setOnClickListener { v: View? ->
            tag = "女儿"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_jk).setOnClickListener { v: View? ->
            tag = "JK"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_jc).setOnClickListener { v: View? ->
            tag = "JC"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_dxj).setOnClickListener { v: View? ->
            tag = "大小姐"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_xz).setOnClickListener { v: View? ->
            tag = "性转"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_wn).setOnClickListener { v: View? ->
            tag = "伪娘"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_rw).setOnClickListener { v: View? ->
            tag = "人外"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_hg).setOnClickListener { v: View? ->
            tag = "后宫"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_bh).setOnClickListener { v: View? ->
            tag = "百合"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_dm).setOnClickListener { v: View? ->
            tag = "耽美"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_ntr).setOnClickListener { v: View? ->
            tag = "NTR"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
        findViewById<View>(R.id.chip_tag_nxsj).setOnClickListener { v: View? ->
            tag = "女性视角"
            val intent: Intent = Intent(this, TagSearchActivity::class.java)
            intent.putExtra("tag", tag)
            startActivity(intent)
        }
    }
}
