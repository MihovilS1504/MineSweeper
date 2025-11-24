
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class MineSweeper extends JFrame {

    private final int GridSize = 10;
    private final int Mines = 10;
    private final int CellSize = 40;
    @SuppressWarnings("FieldMayBeFinal")
    private Cell[][] grid;
    private boolean gameOver;

    public class Cell extends JButton {

        int row, col;
        boolean isMine;
        boolean isRevealed;
        boolean isFlagged;
        int adjacentMines;

        Cell(int row, int col) {
            this.row = row;
            this.col = col;
            setFont(new Font("Arial", Font.BOLD, 16));
            setMargin(new Insets(0, 0, 0, 0));
            addMouseListener(new CellMouseListener());
        }
    }

    public MineSweeper() {
        setTitle("MineSweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(GridSize * CellSize, GridSize * CellSize);
        setLayout(new GridLayout(GridSize, GridSize));
        grid = new Cell[GridSize][GridSize];
        initializeGrid();
        placeMines();
        calculateAdjacentMines();
        gameOver = false;
        setResizable(false);
        setVisible(true);
    }

    private void initializeGrid() {
        for (int i = 0; i < GridSize; i++) {
            for (int j = 0; j < GridSize; j++) {
                grid[i][j] = new Cell(i, j);
                add(grid[i][j]);
            }
        }
    }

    private void placeMines() {
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < Mines) {
            int row = rand.nextInt(GridSize);
            int col = rand.nextInt(GridSize);
            if (!grid[row][col].isMine) {
                grid[row][col].isMine = true;
                minesPlaced++;
            }
        }
    }

    private void calculateAdjacentMines() {
        for (int i = 0; i < GridSize; i++) {
            for (int j = 0; j < GridSize; j++) {
                if (!grid[i][j].isMine) {
                    grid[i][j].adjacentMines = countAdjacentMines(i, j);
                }
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < GridSize && newCol >= 0 && newCol < GridSize) {
                    if (grid[newRow][newCol].isMine) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void revealCell(int row, int col) {
        if (row < 0 || row >= GridSize || col < 0 || col >= GridSize || grid[row][col].isRevealed || grid[row][col].isFlagged) {
            return;
        }

        grid[row][col].isRevealed = true;
        grid[row][col].setEnabled(false);

        if (grid[row][col].isMine) {
            grid[row][col].setText("X");
            gameOver = true;
            JOptionPane.showMessageDialog(this, "Game Over! You hit a mine.");
            revealAllMines();
            return;
        }

        if (grid[row][col].adjacentMines > 0) {
            grid[row][col].setText(String.valueOf(grid[row][col].adjacentMines));
        } else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    revealCell(row + i, col + j);
                }
            }
        }

        checkWinCondition();
    }

    private void revealAllMines() {
        for (int i = 0; i < GridSize; i++) {
            for (int j = 0; j < GridSize; j++) {
                if (grid[i][j].isMine) {
                    grid[i][j].setText("X");
                    grid[i][j].setEnabled(false);
                }
            }
        }
    }

    private void checkWinCondition() {
        int revealedCount = 0;
        for (int i = 0; i < GridSize; i++) {
            for (int j = 0; j < GridSize; j++) {
                if (grid[i][j].isRevealed) {
                    revealedCount++;
                }
            }
        }
        if (revealedCount == GridSize * GridSize - Mines) {
            gameOver = true;
            JOptionPane.showMessageDialog(this, "Congratulations! You won!");
        }
    }

    private class CellMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (gameOver) {
                return;
            }

            Cell cell = (Cell) e.getSource();
            if (SwingUtilities.isLeftMouseButton(e)) {
                revealCell(cell.row, cell.col);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (!cell.isRevealed) {
                    cell.isFlagged = !cell.isFlagged;
                    cell.setText(cell.isFlagged ? "F" : "");
                }
            }
        }
    }
}
