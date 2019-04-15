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

class MainActivity : AppCompatActivity(),View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100
    var flag = 0
    var onoff = 0


    var cursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var resolver = contentResolver
        var cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }

        start.setOnClickListener(this)
        stop.setOnClickListener(this)
        next.setOnClickListener(this)
        buck.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        if (v!!.id == R.id.start) {
            start.setOnClickListener {
                if (onoff == 0) {
                    onoff = 1
                }
            }
        }else if(v!!.id == R.id.stop) {
            stop.setOnClickListener {
                if (onoff == 1) {
                    onoff = 0
                }
            }
        }else if(v!!.id == R.id.next) {
            //停止している時だけ選択可能
            if (onoff == 0) {
                next.setOnClickListener {
                    flag = 3
                    getContentsInfo()
                }
            }
        }else if(v!!.id == R.id.buck){
            //停止している時だけ選択可能
            if (onoff == 0) {
                buck.setOnClickListener {
                    flag = 4
                    getContentsInfo()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    public fun getContentsInfo() {
        var resolver = contentResolver

        if (flag == 0) {
            if (cursor.moveToFirst()) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }
        } else if (flag == 3) {
            Log.d("messeage", "next")
            if (cursor.moveToNext()) {
                Log.d("messeage", "true")
                cursor.moveToNext()
            } else {
                Log.d("messeage", "false")
                cursor.moveToFirst()
            }
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)

        } else if (flag == 4) {
            Log.d("messeage", "buck")
            if (cursor.moveToPrevious()) {
                Log.d("messeage", "true")
                cursor.moveToPrevious()
            } else {
                Log.d("messeage", "false")
                cursor.moveToLast()
            }
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }

        cursor.close()
    }

}
