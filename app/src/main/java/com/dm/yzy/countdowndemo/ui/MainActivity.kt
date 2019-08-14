package com.dm.yzy.countdowndemo.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.dm.yzy.countdowndemo.R
import com.dm.yzy.countdowndemo.R.layout.activity_main
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        initData()
        initListener()
        loadData()
    }

    private fun initData() {
        //开始倒计时
        cdCircleView.startCountDown()
    }

    private fun initListener() {
        gvHomeAdv.setOnClickListener { clickAdv() }
        ivHomeAdv.setOnClickListener { clickAdv() }
        cdCircleView.setOnClickListener { clickCountDownView() }
        cdCircleView.setAddCountDownListener { countDownEnd() }
        btnChangeMode.setOnClickListener { changeMode() }
    }

    private fun loadData() {
        /**
         * 这里是获取本地的图片进行加载的逻辑，支持GIF以及BITMAP
         */

//        //获取到本地存储的图片，展示
//        val path = Preferences.get(this).getString(SP_KEY_ADV_URL, null)
//        mWebUrl = Preferences.get(this).getString(SP_KEY_WEB_URL, null)
//        if (StringUtils.checkString(path)) {
//            var file = FileUtils.getFilePath(this, Md5Util.getValUTF8(path))
//            if (file.exists()) {
//
//                if (path.contains("gif") || path.contains("GIF")) {
//                    val inputStream = FileInputStream(file)
//                    gvHomeAdv.setMovieResource(inputStream)
//                    ivHomeAdv.visibility = View.GONE
//                } else {
//                    val bm = BitmapFactory.decodeFile(file.absolutePath)
//                    ivHomeAdv.setImageBitmap(bm)
//                    ivHomeAdv.visibility = View.VISIBLE
//                    gvHomeAdv.visibility = View.GONE
//
//                }
//            }
//        }

        gvHomeAdv.setMovieResource(R.drawable.icn_gif_test)
        ivHomeAdv.visibility = View.GONE

    }

    private fun clickCountDownView() {
        Toast.makeText(this, "点击倒计时", Toast.LENGTH_SHORT).show()
    }

    private fun countDownEnd() {
        Toast.makeText(this, "倒计时结束", Toast.LENGTH_SHORT).show()
    }

    private fun clickAdv() {
        Toast.makeText(this, "点击了广告页", Toast.LENGTH_SHORT).show()
    }

    private fun changeMode() {
        if (ivHomeAdv.visibility == View.GONE) {
            gvHomeAdv.visibility = View.GONE
            ivHomeAdv.visibility = View.VISIBLE
            ivHomeAdv.setImageResource(R.drawable.icn_image_test)
        } else {
            gvHomeAdv.visibility = View.VISIBLE
            ivHomeAdv.visibility = View.GONE
            ivHomeAdv.setImageResource(R.drawable.icn_image_test)
        }
    }

}
