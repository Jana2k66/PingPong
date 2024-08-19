package com.game.pp;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private View playerPaddle, ball;
    private TextView scoreView, gameOverText;
    private Button restartButton;
    private int screenHeight, screenWidth;
    private float ballXVelocity = 12f, ballYVelocity = 12f;  // Increased ball speed
    private int score = 0;
    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getColor(R.color.bg_black));
        setContentView(R.layout.activity_main);

        // Initialize views
        playerPaddle = findViewById(R.id.player_paddle);
        ball = findViewById(R.id.ball);
        scoreView = findViewById(R.id.score);
        gameOverText = findViewById(R.id.game_over_text);
        restartButton = findViewById(R.id.restart_button);

        // Hide game over text and restart button initially
        gameOverText.setVisibility(View.INVISIBLE);
        restartButton.setVisibility(View.INVISIBLE);

        // Get the layout and screen dimensions
        final RelativeLayout layout = findViewById(R.id.layout);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            screenHeight = layout.getHeight();
            screenWidth = layout.getWidth();
        });

        // Set up paddle movement
        playerPaddle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!gameOver) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            float newX = event.getRawX() - playerPaddle.getWidth() / 2;
                            // Ensure the paddle stays within screen bounds
                            if (newX >= 0 && newX <= screenWidth - playerPaddle.getWidth()) {
                                playerPaddle.setX(newX);
                            }
                            break;
                    }
                }
                return true;
            }
        });

        // Set up restart button
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        // Start the game loop
        startGame();
    }

    // Game loop to move the ball
    private void startGame() {
        ball.post(new Runnable() {
            @Override
            public void run() {
                if (!gameOver) {
                    moveBall();
                    ball.postDelayed(this, 25); // Adjusted the speed slightly
                }
            }
        });
    }

    // Handle ball movement and collision detection
    private void moveBall() {
        float ballX = ball.getX() + ballXVelocity;
        float ballY = ball.getY() + ballYVelocity;

        // Bounce off left and right walls
        if (ballX <= 0 || ballX >= screenWidth - ball.getWidth()) {
            ballXVelocity = -ballXVelocity;
        }

        // Bounce off the top wall
        if (ballY <= 0) {
            ballYVelocity = -ballYVelocity;
        }

        // Check for paddle collision (ball's bottom edge with paddle's top edge)
        if (ballY + ball.getHeight() >= playerPaddle.getY() &&
                ballY + ball.getHeight() <= playerPaddle.getY() + ballYVelocity &&  // Check within the next frame's movement
                ballX + ball.getWidth() >= playerPaddle.getX() &&
                ballX <= playerPaddle.getX() + playerPaddle.getWidth()) {

            // Reverse ball's Y velocity to simulate bounce
            ballYVelocity = -ballYVelocity;

            // Ensure ball doesn't get inside the paddle
            ballY = playerPaddle.getY() - ball.getHeight();

            // Increment the score
            score++;
            scoreView.setText("Score: " + score);
        }

        // Check if the ball touches the bottom of the screen (game over)
        if (ballY + ball.getHeight() >= screenHeight) {
            gameOver = true;
            ballYVelocity = 0;
            ballXVelocity = 0;
            gameOverText.setText("Game Over! Score: " + score);
            gameOverText.setVisibility(View.VISIBLE);
            restartButton.setVisibility(View.VISIBLE);
        }

        // Update ball position
        ball.setX(ballX);
        ball.setY(ballY);
    }

    // Restart the game
    private void restartGame() {
        gameOver = false;
        score = 0;
        ballXVelocity = 12f;
        ballYVelocity = 12f;
        ball.setX(screenWidth / 2 - ball.getWidth() / 2);
        ball.setY(screenHeight / 2 - ball.getHeight() / 2);
        scoreView.setText("Score: 0");
        gameOverText.setVisibility(View.INVISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
        startGame();
    }
}
