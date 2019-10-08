package graphics;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Board {
    private Field[][] fields;
    private ArrayList<Ship> ships;
    private ArrayList<Field> markedFields;
    private VBox VBoard;
    private GridPane board;
    private boolean selected;
    private int selectedX;
    private int selectedY;
    private final int boardSize = 10;

    public Board(GridPane board) {
        fields = new Field[boardSize][boardSize];
        ships = new ArrayList<>(boardSize);
        markedFields = new ArrayList<>();
        VBoard = new VBox();
        selected = false;

        for(int i = 0; i < boardSize; i++){
            HBox HBoard = new HBox();
            for(int j = 0; j < boardSize; j++){
                Field field = new Field(i,j);
                fields[i][j] = field;
                HBoard.getChildren().add(field);
            }
            VBoard.getChildren().add(HBoard);
        }
        setBoard(board);
    }

    public void setBoard(GridPane board) {
        this.board = board;
        board.add(VBoard,1,1);
    }

    public boolean isCorrect(int shipSize){
        int counter = 0;
        Field[] ship = new Field[shipSize];

        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                if(isFieldSelected(i,j)){
                    if(++counter > shipSize)
                        return false;
                    ship[counter - 1] = fields[i][j];
                }
            }
        }

        if (counter < shipSize)
            return false;

        int x = 0, y = 0;
        for (int i = 1; i < shipSize; i++){
            x += ship[i].getPosX() - ship[i-1].getPosX();
            y += ship[i].getPosY() - ship[i-1].getPosY();
        }

        if (x == 0){
            for(int i = 1; i < shipSize; i++){
                if(ship[i].getPosY() - ship[i-1].getPosY() > 1)
                    return false;
            }
        }
        else if (y == 0) {
            for (int i = 1; i < shipSize; i++) {
                if (ship[i].getPosX() - ship[i - 1].getPosX() > 1)
                    return false;
            }
        }
        else
            return false;

        for(int i = 1; i < shipSize; i++){
            int diffX = ship[i].getPosX() - ship[i-1].getPosX();
            int diffY = ship[i].getPosY() - ship[i-1].getPosY();
            if(diffX > 1 || diffY > 1)
                return false;
        }
        for(Field field : ship) {
            for(int i = field.getPosX() - 1; i < field.getPosX() + 2; i++){
                if(i >= 0 && i < boardSize) {
                    for (int j = field.getPosY() - 1; j < field.getPosY() + 2; j++) {
                        if (j >= 0 && j < boardSize) {
                            if (isFieldEmpty(i,j)) {
                                fields[i][j].lock();
                            }
                        }
                    }
                }
            }
            field.lockAsShip();
        }
        ships.add(new Ship(ship));
        return true;
    }

    private void markNeighboringFields(Field[] ship) {
        for(Field field : ship) {
            for(int i = field.getPosX() - 1; i < field.getPosX() + 2; i++){
                if(i >= 0 && i < boardSize) {
                    for (int j = field.getPosY() - 1; j < field.getPosY() + 2; j++) {
                        if (j >= 0 && j < boardSize) {
                            if (isFieldEmpty(j,i)) {
                                fields[i][j].lockAsEnemyMiss();
                            }
                        }
                    }
                }
            }
        }
    }

    public void lockBoard() {
        for(Field[] fieldArr : fields)
            for(Field field : fieldArr)
                field.lock();
    }

    public void unlockBoard() {
        for(Field[] fieldArr : fields)
            for(Field field : fieldArr)
                field.unlock();
    }

    public void resetBoard() {
        for(Field[] fieldArr : fields)
            for(Field field : fieldArr)
                field.reset();
    }

    private boolean isFieldSelected(int x, int y) {
        return fields[x][y].getFill().equals(Color.GRAY);
    }

    public boolean isFieldEmpty(int x, int y) {
        return fields[y][x].getFill().equals(Color.WHITE);
    }

    private boolean isFieldShip(int x, int y) {
        return fields[y][x].isShip();
    }

    public boolean isSelected() {
        return selected;
    }

    public int getSelectedX() {
        return selectedX;
    }

    public int getSelectedY() {
        return selectedY;
    }

    private void selectField(int x, int y) {
        selectedX = x;
        selectedY = y;
        fields[y][x].select();
    }

    private boolean isFieldPermanentLocked(int x, int y) {
        return fields[y][x].isPermanentLocked();
    }

    public void selectingModeON() {
        board.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / 50) - 1;
            int y = (int) (event.getY() / 50) - 1;
            if(x < 0 || x >= fields.length || y < 0 || y >= fields.length || isFieldPermanentLocked(x,y))
                return;
            if(!selected) {
                selectField(x,y);
                selected = true;
            }
            else {
                if(!fields[selectedY][selectedX].isPermanentLocked())
                   fields[selectedY][selectedX].deselect();
                if(x == selectedX && y == selectedY)
                    selected = false;
                else {
                    selectField(x,y);
                }
            }
        });
    }

    public void selectingModeOFF() {
        board.setOnMouseClicked(null);
        selected = false;
    }

    public void lockFieldAsHit(int x, int y) {
        fields[y][x].lockAsHit();
    }

    public void lockFieldAsMiss(int x, int y) {
        fields[y][x].lockAsMiss();
    }

    public void lockFieldAsEnemyHit(int x, int y) {
        fields[y][x].lockAsEnemyHit();
    }

    public void lockFieldAsEnemyMiss(int x, int y) {
        fields[y][x].lockAsEnemyMiss();
    }

    public boolean isShipDestroyed(int x, int y) {
        Field field = fields[y][x];
        for(Ship ship : ships) {
            if(ship.containsField(field)) {
                ship.hit();
                if (ship.isDestroyed()) {
                    markNeighboringFields(ship.getFields());
                    ships.remove(ship);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public void markShipAsDestroyed(int x, int y) {
        markedFields.add(fields[x][y]);
        for(int i = x-1; i < x+2; i++){
            if(i >= 0 && i < boardSize) {
                for (int j = y-1; j < y+2; j++) {
                    if (j >= 0 && j < boardSize) {
                        if (isFieldEmpty(i,j))
                            fields[j][i].lockAsMiss();
                        else if (isFieldShip(i,j) && !markedFields.contains(fields[i][j]))
                            markShipAsDestroyed(i,j);
                    }
                }
            }
        }
    }

    public boolean isAllShipsDestroyed() {
        return ships.size() == 0;
    }
}
