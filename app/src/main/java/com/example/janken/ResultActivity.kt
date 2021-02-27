package com.example.janken

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.janken.databinding.ActivityResultBinding

// じゃんけんの結果を表示するアクティビティ(画面)
class ResultActivity : AppCompatActivity() {

    //
    val gu = 0
    val choki = 1
    val pa = 2

    // ビューバインディングの設定
    private lateinit var binding: ActivityResultBinding
    // 以下のお約束のコードでは、onCreate()メソッドが呼び出され、アクティビティが開始されたら（ビュー・画面が呼び出されたら）、
    // activity_OOOO（例：main）.xmlというレイアウトファイルを読み込んで画面に何を表示するか決める処理を行っている
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // inflateメソッドを呼び出す（inflateとは、指定したxmlのviewリソースを使用するしくみのこと）
        // xmlからviewを直接生成するのではなく、layoutInflaterを使用してインスタンス化する
        binding = ActivityResultBinding.inflate(layoutInflater)
        // ルートビューへの参照及び取得したルートビューをsetContentViewへ渡す
        // setContentViewは、アクティビティ上にビュー（画面）を表示するメソッド
        setContentView(binding.root)
        // インテントの取得
        val id = intent.getIntExtra("MY_HAND", 0)

        // 前画面でタップされたボタンに合わせた画像を表示する
        // 定数宣言
        val myHand: Int
        // when式による条件分岐
        myHand = when(id) {
            R.id.gu -> {
                // setImageResourceメソッドで画像リソースを指定
                binding.myHandImage.setImageResource(R.drawable.gu)
                gu
            }
            R.id.choki -> {
                binding.myHandImage.setImageResource(R.drawable.choki)
                choki
            }
            R.id.pa -> {
                binding.myHandImage.setImageResource(R.drawable.pa)
                pa
            }
            else -> gu
        }

        // コンピュータの手を決める
        // コンピューターの手をランダムにする
        // 定数宣言
        // java.lang.Mathのramdom()メソッドを使用する
        val comHand = getHand()
        when(comHand) {
            gu -> binding.comHandImage.setImageResource(R.drawable.com_gu)
            choki -> binding.comHandImage.setImageResource(R.drawable.com_choki)
            pa -> binding.comHandImage.setImageResource(R.drawable.com_pa)
        }

        // じゃんけんの勝敗を判定する
        // 定数宣言
        val gameResult = (comHand -myHand + 3) % 3
        when(gameResult) {
            0 -> binding.resultLabel.setText(R.string.result_draw)  // 引き分け
            1 -> binding.resultLabel.setText(R.string.result_win) // 勝った場合
            2 -> binding.resultLabel.setText(R.string.result_lose) // 負けた場合
        }
        // setOnClickListenerはクリックを検知して反応するインターフェース
        // finishメソッドは、現在のactivityの終了
        binding.backButton.setOnClickListener { finish() }

        // じゃんけんの結果を保存する
        saveData(myHand, comHand, gameResult)
    }

    // じゃんけんの勝敗結果データを保存して、アプリケーションで利用する
    // 共有プリファレンスを利用する
    private fun saveData(myHand: Int, comHand: Int, gameResult: Int) {
        // 定数宣言
        // getDefaultSharedPreferencesメソッドは、デフォルトの共有プリファレンスの取得
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        // 勝負した回数
        // getIntメソッドは、共有プリファレンスの設定項目をInt型で取得する
        val gameCount = pref.getInt("GAME_COUNT", 0)
        // 連勝した回数
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0)
        // コンピュータの前回の手
        val lastComHand = pref.getInt("LAST_COM_HAND", 0)
        //  前回の勝敗
        val lastGameResult = pref.getInt("GAME_RESULT", -1)

        val edtWinningStreakCount: Int =
            when {
                lastGameResult == 2 && gameResult == 2 ->
                    winningStreakCount + 1
                else ->
                    0
            }

        // Android KTXによる拡張で省略（コメントアウト）& editor.putInt(・・・)の変更
        // val editor = pref.edit()
        // putIntメソッドは、共有プリファレンスの設定項目をInt型で設定する
        pref.edit {
            putInt("GAME_COUNT", gameCount + 1) // 勝負した回数
            putInt("WINNING_STREAK_COUNT", edtWinningStreakCount) // 連勝した回数
            putInt("LAST_MY_HAND", myHand) // プレイヤーの前回の手
            putInt("LAST_COM_HAND", lastComHand) // コンピュータの前回の手
            putInt("BEFORE_LAST_COM_HAND", comHand) // コンピュータの前々回の手
            putInt("GAME_RESULT", gameResult) // 前回の勝敗
            // .apply() // 変更の保存

        }
    }

    private fun getHand(): Int {
        var hand = (Math.random() * 3).toInt()
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0)
        val lastMyHand = pref.getInt("LAST_MY_HAND", 0)
        val lastComHand = pref.getInt("LAST_COM_HAND", 0)
        val beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND", 0)
        val gameResult = pref.getInt("GAME_RESULT", -1)

        if (gameCount == 1) {
            if (gameResult == 2) {
                // 前回の勝負が１回目で、コンピュータが勝った場合、
                // コンピュータは次に出す手を変える
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            } else if (gameResult == 1) {
                // 前回の勝負が1回目で、コンピュータが負けた場合、
                // 相手の出したてに勝つ手を出す
                hand = (lastMyHand - 1 + 3) % 3
            }
        } else if (winningStreakCount > 0) {
            if (beforeLastComHand == lastComHand) {
                // 同じ手で連勝した場合は手を変える
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            }
        }
        return hand
    }
}