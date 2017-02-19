package ch.chrummibei.silvercoin.gui;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by brachiel on 19/02/2017.
 */
public class TableRow extends ArrayList<String> {
    private Optional<Color> color;
    private Optional<TableWidget.STYLE> style;
    private Optional<Float> rowHeight;

    public TableRow() {
        this(new String[]{},null,null,null);
    }

    public TableRow(String[] cols) {
        this(cols,null,null,null);
    }

    public TableRow(String[] cols, Color color, TableWidget.STYLE style, Float rowHeight) {
        addAll(Arrays.asList(cols));
        this.color = Optional.ofNullable(color);
        this.style = Optional.ofNullable(style); // null = no special style
        this.rowHeight = Optional.ofNullable(rowHeight);
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

    public Optional<Color> getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = Optional.of(color);
    }
}
