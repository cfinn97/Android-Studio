package it.miketech.tic_tac_toe;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Button> buttons_ = new ArrayList<Button>();

    private boolean player1Turn_ = true;
    /**
     * Turn counter (number of turns)
     */
    private int turn_;
    /**
     * Player 1 score
     */
    private int score1_;
    /**
     * Player 2 score
     */
    private int score2_;

    private TextView textViewPlayer1_;

    private TextView textViewPlayer2_;
    /**
     * Whether or not the game is being currently played
     */
    private boolean play_;

    private Toast toast_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPlayer1_ = (TextView) findViewById(R.id.text_view_p1);
        textViewPlayer2_ = (TextView) findViewById(R.id.text_view_p2);
        play_ = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                Button button = (Button) findViewById(resID);
                button.setOnClickListener(this);
                buttons_.add(button);
            }

            Button resetButton_ = (Button) findViewById(R.id.button_reset);
            resetButton_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initializeGame();

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (!play_) {
            toast_ = Toast.makeText(this, "Press New Game", Toast.LENGTH_SHORT);
            toast_.setGravity(Gravity.CENTER, 0, 0);
            toast_.show();

            return;
        }
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        if (player1Turn_) {
            ((Button) v).setTextColor(Color.RED);
            ((Button) v).setText("X");
        } else {
            ((Button) v).setTextColor(Color.BLUE);
            ((Button) v).setText("O");
        }
        turn_++;

        if (winCheck(v)) {
            playerWins();
            play_ = false;
        } else if (turn_ == 9) {
            draw();
            play_ = false;
        } else {
            player1Turn_ = !player1Turn_;
        }
    }

    private boolean winCheck(View v) {
        int n = buttons_.indexOf((Button) v);
        int r = n % 3;
        if (r == 2) {
            if (n == 8 && buttons_.get(8).getText().toString().equals(buttons_.get(4).getText().toString()) && buttons_.get(8).getText().toString().equals(buttons_.get(0).getText().toString())) {
                showWin(8, 4, 0);
                return true; // diagonal
            }
            if (n == 2 && buttons_.get(2).getText().toString().equals(buttons_.get(4).getText().toString()) && buttons_.get(2).getText().toString().equals(buttons_.get(6).getText().toString())) {
                showWin(2, 4, 6);
                return true; // diagonal
            }
            if (buttons_.get(n).getText().toString().equals(buttons_.get(n - 1).getText().toString()) && buttons_.get(n).getText().toString().equals(buttons_.get(n - 2).getText().toString())) {
                showWin(n, n - 1, n - 2);
                return true; // horizontal
            }
            if (buttons_.get(2).getText().toString().equals(buttons_.get(5).getText().toString()) && buttons_.get(2).getText().toString().equals(buttons_.get(8).getText().toString())) {
                showWin(2, 5, 8);
                return true; // vertical
            }
            return false;
        }

        if (r == 1) {
            if (n == 4 && ((buttons_.get(4).getText().toString().equals(buttons_.get(0).getText().toString()) && buttons_.get(4).getText().toString().equals(buttons_.get(8).getText().toString())) || (buttons_.get(4).getText().toString().equals(buttons_.get(2).getText().toString()) && buttons_.get(4).getText().toString().equals(buttons_.get(6).getText().toString())))) {
                showWin(4, 0, 8);
                return true; // diagonals
            }
            if (buttons_.get(n).getText().toString().equals(buttons_.get(n - 1).getText().toString()) && buttons_.get(n).getText().toString().equals(buttons_.get(n + 1).getText().toString())) {
                showWin(n, n - 1, n + 1);
                return true; // horizontal
            }
            if (buttons_.get(1).getText().toString().equals(buttons_.get(4).getText().toString()) && buttons_.get(1).getText().toString().equals(buttons_.get(7).getText().toString())) {
                showWin(1, 4, 7);
                return true; // vertical
            }
            return false;
        }
        if (n == 0 && buttons_.get(0).getText().toString().equals(buttons_.get(4).getText().toString()) && buttons_.get(0).getText().toString().equals(buttons_.get(8).getText().toString())) {
            showWin(0, 4, 8);
            return true; // diagonal
        }
        if (n == 6 && buttons_.get(6).getText().toString().equals(buttons_.get(4).getText().toString()) && buttons_.get(6).getText().toString().equals(buttons_.get(2).getText().toString())) {
            showWin(6, 4, 2);
            return true; // diagonal
        }
        if (buttons_.get(n).getText().toString().equals(buttons_.get(n + 1).getText().toString()) && buttons_.get(n).getText().toString().equals(buttons_.get(n + 2).getText().toString())) {
            showWin(n, n + 1, n + 2);
            return true; // horizontal
        }
        if (buttons_.get(0).getText().toString().equals(buttons_.get(3).getText().toString()) && buttons_.get(0).getText().toString().equals(buttons_.get(6).getText().toString())) {
            showWin(0, 3, 6);
            return true; // vertical
        }
        return false;
    }

    private void showWin(int mark1, int mark2, int mark3) {
        buttons_.get(mark1).setBackgroundColor(Color.YELLOW);
        buttons_.get(mark2).setBackgroundColor(Color.YELLOW);
        buttons_.get(mark3).setBackgroundColor(Color.YELLOW);
    }

    private void playerWins() {
        if (player1Turn_) {
            score1_++;
            toast_ = Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT);
            toast_.setGravity(Gravity.CENTER, 0, 0);
            toast_.show();
        } else {
            score2_++;
            toast_ = Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT);
            toast_.setGravity(Gravity.CENTER, 0, 0);
            toast_.show();
        }
        updatePointsText();
    }

    private void draw() {
        toast_ = Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT);
        toast_ = Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT);
        toast_.setGravity(Gravity.CENTER, 0, 0);
        toast_.show();
    }

    private void updatePointsText() {
        textViewPlayer1_.setText("Player 1: " + score1_);
        textViewPlayer2_.setText("Player 2: " + score2_);
    }

    private void initializeGame() {
        for (int i = 0; i < buttons_.size(); i++) {
            buttons_.get(i).setText("");
            buttons_.get(i).setBackgroundColor(Color.WHITE);
        }
        turn_ = 0;
        player1Turn_ = true;
        play_ = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("turn_count", turn_);
        outState.putInt("player1_score", score1_);
        outState.putInt("player2_score", score2_);
        outState.putBoolean("player1_Turn", player1Turn_);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        turn_ = savedInstanceState.getInt("turn_count");
        score1_ = savedInstanceState.getInt("player1_score");
        score2_ = savedInstanceState.getInt("player2_score");
        player1Turn_ = savedInstanceState.getBoolean("player1_turn");
    }
}