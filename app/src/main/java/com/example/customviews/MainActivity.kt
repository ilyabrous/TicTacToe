package com.example.customviews

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.customviews.TicTacToeField.Memento
import com.example.customviews.databinding.ActivityMainBinding
import kotlin.properties.Delegates

//all possible direction of Tic tac toe
enum class StepDirection(val columnStep: Int, val rowStep: Int) {
    UP_DOWN(0, 1),
    LEFT_RIGHT(1, 0),
    LOWER_LEFT_TO_UPPER_RIGHT(1, 1),
    LOWER_RIGHT_TO_UPPER_LEFT(-1, 1),
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isFirstPlayer by Delegates.notNull<Boolean>()
    private lateinit var field: TicTacToeField
    private var isClickable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup UI
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore or init field and variables
        field =
            savedInstanceState?.getParcelable<Memento>(KEY_FIELD)?.restoreField() ?: TicTacToeField(
                3,
                3
            )
        binding.ticTacToeField.field = field
        isFirstPlayer = savedInstanceState?.getBoolean(KEY_IS_FIRST_PLAYER, true) ?: true

        // listening user actions
        binding.ticTacToeField.actionListener = actionListener@{ row, column, currentField ->
            // user has pressed/chosen cell[row, column]
            if (!isClickable) return@actionListener
            // get current cell value
            val cell = currentField.getCell(row, column)
            if (cell == Cell.EMPTY) {
                // cell is empty, changing it to X or O
                if (isFirstPlayer) currentField.setCell(row, column, Cell.PLAYER_1)
                else currentField.setCell(row, column, Cell.PLAYER_2)

                isFirstPlayer = if (checkWin(row, column)) {
                    showWinnerDialog()
                    true
                } else
                    !isFirstPlayer

            }
        }

        binding.randomFieldButton.setOnClickListener {
            // generate random empty field
            setNewField()
            isClickable = true
        }
    }

    private fun showWinnerDialog() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setIcon(R.drawable.ic_baseline_videogame_asset_24)
            .setTitle("Game over!")
            .setMessage("Would you like to start a new game or look at the field?")
            .setPositiveButton("New game") { _, which ->
                setNewField()
            }
            .setNeutralButton("Look at") { ui, which ->
                ui.cancel()
                isClickable = false
            }
            .create().show()
    }

    private fun setNewField() {
        field = TicTacToeField(3, 3)
        binding.ticTacToeField.field = field
    }

    private fun checkWin(row: Int, column: Int): Boolean {
        val stepDirections = StepDirection.values()

        //for each direction we try to find the right amount of winnings
        for (i in stepDirections.indices) {

            if (checkDirection(stepDirections[i], row, column)) {

                val winner = if (isFirstPlayer) "First Player" else "Second Player"
                Toast.makeText(applicationContext, "Winner is $winner", Toast.LENGTH_SHORT).show()
                return true

            }
        }

        return false
    }

    //it goes in one side of direction, catch empty or another player's cell and start going in a reverse direction
    private fun checkDirection(step: StepDirection, row: Int, column: Int): Boolean {
        val playerCell = if (isFirstPlayer) Cell.PLAYER_1 else Cell.PLAYER_2
        var numberOfCorrectCell = 0

        var columnStep = step.columnStep
        var rowStep = step.rowStep

        var startColumn = column
        var startRow = row

        for (i in 0..1) {

            if (i == 1) {
                //change direction in reverse(from down-up to up-down)
                startColumn = column
                startRow = row
                columnStep = 0 - step.columnStep
                rowStep = 0 - step.rowStep
            }

            while (true) {
                try {
                    startColumn += columnStep
                    startRow += rowStep

                    if (field.getCell(row = startRow, column = startColumn) == playerCell) numberOfCorrectCell++
                    else break
                    if (numberOfCorrectCell == 2) return true

                } catch (e: CellNotFoundException) {
                    break
                }
            }
        }
        return numberOfCorrectCell >= 3
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val field = binding.ticTacToeField.field
        outState.putParcelable(KEY_FIELD, field!!.saveState())
        outState.putBoolean(KEY_IS_FIRST_PLAYER, isFirstPlayer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    companion object {
        private const val KEY_FIELD = "KEY_FIELD"
        private const val KEY_IS_FIRST_PLAYER = "KEY_IS_FIRST_PLAYER"
    }

}

