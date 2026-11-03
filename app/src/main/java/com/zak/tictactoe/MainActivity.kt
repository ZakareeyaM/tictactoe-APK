package com.zak.tictactoe

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.random.Random

class MainActivity : Activity() {

    private val human = "X"
    private val ai = "O"
    private val board = Array(9) { "" }
    private val cells = arrayOfNulls<Button>(9)

    private lateinit var status: TextView
    private lateinit var score: TextView

    private var wins = 0
    private var losses = 0
    private var draws = 0
    private var gameOver = false
    private var aiThinking = false

    private val handler = Handler(Looper.getMainLooper())

    private val lines = arrayOf(
        intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
    )

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#111827"))
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        score = TextView(this).apply {
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        root.addView(score)

        status = TextView(this).apply {
            textSize = 16f
            setTextColor(Color.parseColor("#9CA3AF"))
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(16))
        }
        root.addView(status)

        for (r in 0 until 3) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }
            for (c in 0 until 3) {
                val index = r * 3 + c
                val btn = Button(this).apply {
                    textSize = 36f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.parseColor("#1F2937"))
                    setOnClickListener { onCellClick(index) }
                }
                val params = LinearLayout.LayoutParams(dp(96), dp(96)).apply {
                    setMargins(dp(4), dp(4), dp(4), dp(4))
                }
                row.addView(btn, params)
                cells[index] = btn
            }
            root.addView(row)
        }

        val newGame = Button(this).apply {
            text = "New Game"
            setOnClickListener { resetBoard() }
        }
        val ngParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(20) }
        root.addView(newGame, ngParams)

        setContentView(root)
        updateScore()
        status.text = "Your turn (X)"
    }

    private fun onCellClick(i: Int) {
        if (gameOver || aiThinking || board[i].isNotEmpty()) return
        place(i, human)
        if (finishIfOver()) return
        aiThinking = true
        status.text = "AI is thinking..."
        handler.postDelayed({
            aiMove()
            aiThinking = false
            if (!finishIfOver()) status.text = "Your turn (X)"
        }, 300)
    }

    private fun place(i: Int, symbol: String) {
        board[i] = symbol
        cells[i]?.apply {
            text = symbol
            setTextColor(if (symbol == human) Color.parseColor("#60A5FA") else Color.parseColor("#F472B6"))
        }
    }

    private fun aiMove() {
        val available = board.indices.filter { board[it].isEmpty() }
        if (available.isEmpty()) return
        // 20% of the time play a random move so the AI is beatable
        val move = if (Random.nextFloat() < 0.2f) {
            available.random()
        } else {
            bestMove()
        }
        place(move, ai)
    }

    private fun bestMove(): Int {
        var bestVal = Int.MIN_VALUE
        val best = mutableListOf<Int>()
        for (i in board.indices) {
            if (board[i].isEmpty()) {
                board[i] = ai
                val v = minimax(false, 1)
                board[i] = ""
                if (v > bestVal) {
                    bestVal = v
                    best.clear()
                    best.add(i)
                } else if (v == bestVal) {
                    best.add(i)
                }
            }
        }
        return best.random()
    }

    private fun minimax(isAiTurn: Boolean, depth: Int): Int {
        when (winner()) {
            ai -> return 10 - depth
            human -> return depth - 10
            "Draw" -> return 0
        }
        var best = if (isAiTurn) Int.MIN_VALUE else Int.MAX_VALUE
        for (i in board.indices) {
            if (board[i].isEmpty()) {
                board[i] = if (isAiTurn) ai else human
                val v = minimax(!isAiTurn, depth + 1)
                board[i] = ""
                best = if (isAiTurn) maxOf(best, v) else minOf(best, v)
            }
        }
        return best
    }

    private fun winner(): String? {
        for (l in lines) {
            val a = board[l[0]]
            if (a.isNotEmpty() && a == board[l[1]] && a == board[l[2]]) return a
        }
        return if (board.all { it.isNotEmpty() }) "Draw" else null
    }

    private fun finishIfOver(): Boolean {
        val w = winner() ?: return false
        gameOver = true
        when (w) {
            human -> { wins++; status.text = "You win! Tap New Game." }
            ai -> { losses++; status.text = "AI wins. Tap New Game." }
            else -> { draws++; status.text = "It's a draw. Tap New Game." }
        }
        updateScore()
        return true
    }

    private fun updateScore() {
        score.text = "You: $wins   AI: $losses   Draws: $draws"
    }

    private fun resetBoard() {
        handler.removeCallbacksAndMessages(null)
        aiThinking = false
        gameOver = false
        for (i in board.indices) {
            board[i] = ""
            cells[i]?.text = ""
        }
        status.text = "Your turn (X)"
    }
}
