package ch.chrummibei.silvercoin.gui;

import java.util.ArrayList;

/**
 * Created by brachiel on 19/02/2017.
 */
public class TableRow<T> {
    private final ArrayList<T> data = new ArrayList<>();
    private TableWidget.STYLE style;
    private Integer rowHeight = null;

    public TableRow(ArrayList<T> data, TableWidget.STYLE style, Integer rowHeight) {
        this.style = style; // null = no special style
        this.data.addAll(data);
        this.rowHeight = rowHeight;
    }
}
