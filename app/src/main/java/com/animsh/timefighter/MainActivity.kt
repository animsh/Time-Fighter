package com.animsh.timefighter

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var tapMeButton: MaterialButton
    private lateinit var gameScoreLabel: TextView
    private lateinit var gameTimerLabel: TextView

    private var score = 0
    private var gameStarted = false

    private lateinit var countDownTimer: CountDownTimer
    private val initialCountDown: Long = 10000
    private val countDownInterval: Long = 1000
    private var timeLeftCountDown: Long = 60000

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIMER_KEY = "TIMER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: $score")

        tapMeButton = findViewById(R.id.tapMeButton)
        gameScoreLabel = findViewById(R.id.gameScoreLabel)
        gameTimerLabel = findViewById(R.id.gameTimerLabel)

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY);
            timeLeftCountDown = savedInstanceState.getLong(TIMER_KEY);
            resumeGame()
        } else {
            resetGame()
        }
        tapMeButton.setOnClickListener {
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            it.startAnimation(bounceAnimation)
            incrementScore()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.actionAbout) {
            showInfo()
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIMER_KEY, timeLeftCountDown)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(
            TAG,
            "onRestoreInstanceState: ${savedInstanceState.get(SCORE_KEY)} ${
                savedInstanceState.get(
                    TIMER_KEY
                )
            }"
        )
    }

    private fun incrementScore() {
        if (!gameStarted) {
            startGame()
        }
        score++
        val scoreLabel = getString(R.string.your_score, score)
        gameScoreLabel.text = scoreLabel

        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        gameScoreLabel.startAnimation(blinkAnimation)
    }

    private fun resumeGame() {
        gameScoreLabel.text = getString(R.string.your_score, score)

        val restoredTime = timeLeftCountDown / 1000
        gameTimerLabel.text = getString(R.string.time_left, restoredTime)

        countDownTimer = object : CountDownTimer(timeLeftCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftCountDown = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                gameTimerLabel.text = getString(R.string.time_left, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }

        }

        countDownTimer.start()
        gameStarted = true
    }

    private fun resetGame() {
        score = 0
        gameScoreLabel.text = getString(R.string.your_score, score)
        val initialTimeLeft = initialCountDown / 1000
        gameTimerLabel.text = getString(R.string.time_left, initialTimeLeft)

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUnfinished: Long) {
                timeLeftCountDown = millisUnfinished
                gameTimerLabel.text = getString(R.string.time_left, millisUnfinished / 1000)
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.gameOverMessage, score), Toast.LENGTH_LONG).show()
        resetGame()
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.aboutTitle, BuildConfig.VERSION_CODE)
        val dialogMessage = getString(R.string.aboutMessage)

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(dialogTitle)
        alertDialog.setMessage(dialogMessage)
        alertDialog.create().show()
    }
}