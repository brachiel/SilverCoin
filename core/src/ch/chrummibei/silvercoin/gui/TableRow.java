package ch.chrummibei.silvercoin.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by brachiel on 19/02/2017.
 */
public class TableRow extends Widget {
    private final ArrayList<String> cells = new ArrayList<>();
    private Optional<TableWidget.STYLE> style;
    private Optional<Float> rowHeight;
    public boolean hasColorSet = false;

    public TableRow() {
        this(new String[]{},null,null,null);
    }

    public TableRow(String[] cols) {
        this(cols,null,null,null);
    }

    public TableRow(String[] cols, Color color, TableWidget.STYLE style, Float rowHeight) {
        super();
        if (color != null) setColor(color);
        cells.addAll(Arrays.asList(cols));
        this.style = Optional.ofNullable(style); // null = no special style
        this.rowHeight = Optional.ofNullable(rowHeight);
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        hasColorSet = true;
    }

    public Optional<TableWidget.STYLE> getStyle() {
        return style;
    }

    public Optional<Float> getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(float rowHeight) {
        this.rowHeight = Optional.ofNullable(rowHeight);
    }

    public int size() {
        return cells.size();
    }

    public String get(int j) {
        return cells.get(j);
    }

    public void add(String cell) {
        cells.add(cell);
    }
}
