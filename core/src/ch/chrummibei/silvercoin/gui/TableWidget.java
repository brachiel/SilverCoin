package ch.chrummibei.silvercoin.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.ArrayList;

/**
 * Created by brachiel on 19/02/2017.
 */
public class TableWidget {
    public enum STYLE { HEADER }

    private class ColumnConfiguration {
        private final int width;
        private final STYLE style;

        public ColumnConfiguration(int width, STYLE style) {
            this.width = width;
            this.style = style;
        }
    }

    private ArrayList<ColumnConfiguration> columnConfigurations = new ArrayList<>();
    private ArrayList<TableRow<String>> data = new ArrayList<>();

    public void addColumn(int columnWidth, STYLE style) {
        columnConfigurations.add(new ColumnConfiguration(columnWidth, style));
    }

    public void addRow(TableRow<String> rowData) {
        this.data.add(rowData);
    }

    public void draw(BitmapFont font, int posX, int posY) {
        for ()
    }
}
