package com.example.autoslidesshowapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import java.util.*
import android.os.Handler
import android.widget.Button

class MainActivity : AppCompatActivity(),View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100
    var flag = 0

    var cursor: Cursor? = null

    var mTimer: Timer? = null

    // タイマー用の時間のための変数
    var mTimerSec = 0.0
    var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                // 許可されている
                var resolver = contentResolver
                cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                    null, // 項目(null = 全項目)
                    null, // フィルタ条件(null = フィルタなし)
                    null, // フィルタ用パラメータ
                    null // ソート (null ソートなし)
                )
                start.setOnClickListener(this)
                //stop.setOnClickListener(this)
                next.setOnClickListener(this)
                buck.setOnClickListener(this)

                getContentsInfo()

            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {

            var resolver = contentResolver
            cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
            )

            start.setOnClickListener(this)
            //stop.setOnClickListener(this)
            next.setOnClickListener(this)
            buck.setOnClickListener(this)

            getContentsInfo()
        }
    }

    override fun onClick(v: View?) {

        if (v!!.id == R.id.start) { //再生ボタンON
            if (mTimer == null) {
                next.setEnabled(false)
                buck.setEnabled(false)
                //stop.visibility = View.VISIBLE
                //start.visibility = View.INVISIBLE
                start.text = "stop"

                Log.d("messeage", "再生ボタンON")
                // タイマーの作成
                mTimer = Timer()
                // タイマーの始動
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        flag = 3
                        mTimerSec += 0.1
                        mHandler.post {
                            Log.d("messeage", "2秒たちました")
                            getContentsInfo()
                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定

            }else{
                next.setEnabled(true)
                buck.setEnabled(true)
                //stop.visibility = View.VISIBLE
                //start.visibility = View.INVISIBLE
                start.text = "start"

                Log.d("messeage", "停止ボタンON")
                mTimer!!.cancel()
                mTimer = null
                mTimerSec = 0.0
            }

        /*}else if(v!!.id == R.id.stop) { //停止ボタンON
            stop.visibility = View.INVISIBLE
            start.visibility = View.VISIBLE
            if (mTimer != null) {
                Log.d("messeage", "停止ボタンON")
                mTimer!!.cancel()
                mTimer = null
                mTimerSec = 0.0

                next.setEnabled(true)
                buck.setEnabled(true)
            }*/
        }else if(v!!.id == R.id.next) { //進むボタン
            //停止している時だけ選択可能
            flag = 3
            getContentsInfo()

        }else if(v!!.id == R.id.buck) { //戻るボタン
            //停止している時だけ選択可能
            flag = 4
            getContentsInfo()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    var resolver = contentResolver
                    cursor = resolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                        null, // 項目(null = 全項目)
                        null, // フィルタ条件(null = フィルタなし)
                        null, // フィルタ用パラメータ
                        null // ソート (null ソートなし)
                    )

                    start.setOnClickListener(this)
                    //stop.setOnClickListener(this)
                    next.setOnClickListener(this)
                    buck.setOnClickListener(this)

                    getContentsInfo()
                }
        }
    }

    public fun getContentsInfo() {

        if (flag == 0) {
            if (cursor!!.moveToFirst()) {

                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }
        } else if (flag == 3) {
            Log.d("messeage", "next")
            if (cursor!!.moveToNext()) {
                Log.d("messeage", "true")
            } else {
                Log.d("messeage", "false")
                cursor!!.moveToFirst()
            }

            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)

        } else if (flag == 4) {
            Log.d("messeage", "buck")

            if (cursor!!.moveToPrevious()) {
                Log.d("messeage", "true")
            } else {
                Log.d("messeage", "false")
                cursor!!.moveToLast()
            }

            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        cursor!!.close()
    }

}
