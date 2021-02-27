package com.example.janken

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.janken.databinding.ActivityMainBinding

// グー、チョキ、パーのボタンがあるメインのアクティビティ（画面）
class MainActivity : AppCompatActivity() {

    // ビューバインディング（ビューの取得）用の設定
    private lateinit var binding: ActivityMainBinding
    // onCreate()メソッド内でバインディングを使用
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1~3はお決まりの処理
        // 1.生成されたバインディングクラス（今回はActivityMainBinding）に含まれるinflateメソッドを呼び出す
        binding = ActivityMainBinding.inflate(layoutInflater)
        // 2.rootプロパティからルートビューへの参照を取得
        // 3.取得したルートビューをsetContentViewに渡す
        setContentView(binding.root)

        // setOnClickListenerメソッドにてビューがクリックされたときに呼ばれるリスナーを登録する
        binding.gu.setOnClickListener { onJankenButtonTapped(it) }
        binding.choki.setOnClickListener { onJankenButtonTapped(it) }
        binding.pa.setOnClickListener { onJankenButtonTapped(it) }

        // じゃんけんの勝負結果情報をアプリ起動時にクリアにする
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        pref.edit {
            clear()
        }
    }

    // グー、チョキ、パーボタンをタップしたときに実行されるメソッド
    // 引数の？はnullの可能性があるため
    fun onJankenButtonTapped(view: View?) {
        // 新しい画面を開くためにインテントを使う
        // インテントでは呼び出し元のインスタンス（this）、呼び出したいアクティビティ（画面）のクラスを指定する
        val intent = Intent(this, ResultActivity::class.java)
        // putExtraメソッドで送りたいデータをインテントに設定する
        intent.putExtra("MY_HAND", view?.id)
        // startActivityメソッドでアクティビティを起動する
        startActivity(intent)
    }
}