package com.cloud.cloudclient.fxcontrollers;

import com.cloud.cloudclient.entity.Board;
import com.cloud.cloudclient.entity.DashBoard;
import com.cloud.cloudclient.view.utils.DashBoardCalc;
import com.cloud.cloudclient.view.utils.SizeUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dashboard {
    public Pane boardsWrapper;
    private DashBoard dashBoard;

    public Dashboard(Pane boardsWrapper) {
        this.boardsWrapper = boardsWrapper;
        boardsWrapper.getChildren().clear();
        dashBoard = DashBoardCalc.getDashBoard();
        initBoards();
    }

    private void initBoards() {
        GridPane boardPane = creatGridBoardsWrapper();
        initDashBoards();
        initBoardPane(boardPane);
        boardsWrapper.getChildren().add(boardPane);
    }

    private void initBoardPane(GridPane boardPane) {
        List<Board> boards = new ArrayList<>(dashBoard.getAll().stream().sorted().toList());

        var row = 0;
        var col = 0;
        for (int i = 0; i < boards.size(); i++) {
            Board board = boards.get(i);
            if (i % 2 == 0 && i != 0) {
                row++;
                col = 0;
            }
            if (i == boards.size() - 1) {
                boardPane.add(board.getBoard(), col, row, 2, 1);
            } else {
                boardPane.add(board.getBoard(), col, row);
            }
            col++;
        }
    }

    private GridPane creatGridBoardsWrapper() {
        GridPane boardPane = new GridPane();
        boardPane.setHgap(10);
        boardPane.setVgap(10);
        boardPane.setAlignment(Pos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints(150, 150, Double.MAX_VALUE);
        col1.setHgrow(Priority.ALWAYS);
        boardPane.getColumnConstraints().add(col1);

        ColumnConstraints col2 = new ColumnConstraints(120, 120, Double.MAX_VALUE);
        col2.setHgrow(Priority.ALWAYS);
        boardPane.getColumnConstraints().add(col2);

        RowConstraints row1 = new RowConstraints(130, 130, Double.MAX_VALUE);
        row1.setVgrow(Priority.ALWAYS);
        boardPane.getRowConstraints().add(row1);

        RowConstraints row2 = new RowConstraints(120, 120, Double.MAX_VALUE);
        row2.setVgrow(Priority.ALWAYS);
        boardPane.getRowConstraints().add(row2);

        RowConstraints row3 = new RowConstraints(110, 110, Double.MAX_VALUE);
        row3.setVgrow(Priority.ALWAYS);
        boardPane.getRowConstraints().add(row3);

        return boardPane;
    }

    private void initDashBoards() {
        dashBoard.getDocument().setBoard(
                createBoard(dashBoard.getDocument().getName(), dashBoard.getDocument().getBytes(), "/images/icon/file-text2.png"));
        dashBoard.getVideo().setBoard(
                createBoard(dashBoard.getVideo().getName(), dashBoard.getVideo().getBytes(), "/images/icon/file-play.png"));
        dashBoard.getImage().setBoard(
                createBoard(dashBoard.getImage().getName(), dashBoard.getImage().getBytes(), "/images/icon/file-picture.png"));
        dashBoard.getMusic().setBoard(
                createBoard(dashBoard.getMusic().getName(), dashBoard.getMusic().getBytes(), "/images/icon/file-music.png"));
        dashBoard.getOther().setBoard(
                createBoard(dashBoard.getOther().getName(), dashBoard.getOther().getBytes(), "/images/icon/file-empty.png"));
    }

    private VBox createBoard(String name, long size, String iconPath) {
        VBox button = new VBox();
        button.setAlignment(Pos.CENTER);
        button.getStyleClass().add("board");

        VBox boardWrapper = new VBox();
        boardWrapper.getStyleClass().add("boardWrapper");
        boardWrapper.setAlignment(Pos.CENTER);
        ImageView view = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)), 50,
                50, false, true));
        boardWrapper.getChildren().add(view);

        Label labelName = new Label(name);
        Label labelSize = new Label(SizeUtil.calculateSize(size));
        boardWrapper.getChildren().addAll(labelName, labelSize);
        button.getChildren().add(boardWrapper);
        return button;
    }
}
