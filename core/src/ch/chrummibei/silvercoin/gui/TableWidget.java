package ch.chrummibei.silvercoin.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by brachiel on 19/02/2017.
 */
public class TableWidget extends WidgetGroup {
    public enum STYLE { LEFT_ALIGN, RIGHT_ALIGN, CENTER }
    private final ArrayList<ColumnConfiguration> columnConfigurations = new ArrayList<>();
    private final ArrayList<TableRow> rows = new ArrayList<>();

    private BitmapFont font;
    private float cellMargin = 0f;

    private class ColumnConfiguration {
        final float width;
        final Optional<STYLE> style;
        final Optional<Color> color;

        public ColumnConfiguration(float width, STYLE style, Color color) {
            this.width = width;
            this.style = Optional.ofNullable(style);
            this.color = Optional.ofNullable(color);
        }
    }

    public TableWidget(BitmapFont font) {
        super();
        this.font = font;
    }

    public void add(String ... cols) {
        rows.add(new TableRow(cols));
    }


    public void addColumn(String header, float columnWidth) {
        addColumn(header, columnWidth, null, null);
    }
    public void addColumn(String header, float columnWidth, Color color) {
        addColumn(header, columnWidth, null, color);
    }
    public void addColumn(String header, float columnWidth, STYLE style) {
        addColumn(header, columnWidth, style, null);
    }
    public void addColumn(String header, float columnWidth, STYLE style, Color color) {
        if (header != null) {
            if (rows.size() == 0) { rows.add(new TableRow()); }
            rows.get(0).add(header);
        }
        columnConfigurations.add(new ColumnConfiguration(columnWidth, style, color));
    }

    public void addRow(TableRow rowData) {
        rows.add(rowData);
    }

    public float defaultLineHeight() {
        return font.getLineHeight()+cellMargin;
    }

    public float getHeight() {
        return (float) rows.stream()
                .map(TableRow::getRowHeight)
                .mapToDouble(o -> o.orElse(defaultLineHeight()).doubleValue())
                .sum();
    }

    public float getWidth() {
        return (float) this.columnConfigurations.stream().mapToDouble(col -> (double) col.width).sum();
    }

    public void draw(Batch batch, int posX, int posY) {
        Color defaultColor = font.getColor().cpy();

        float rowPosY = posY;
        for (int i = 0; i < rows.size(); ++i) {
            TableRow row = rows.get(i);
            float cellPosX = posX;
            Optional<Color> rowColor = Optional.ofNullable(row.hasColorSet ? row.getColor() : null);

            for (int j = 0; j < row.size(); ++j) {
                String cell = row.get(j);
                ColumnConfiguration columnConfiguration = columnConfigurations.get(j);
                font.setColor(rowColor.orElse(columnConfiguration.color.orElse(defaultColor)));

                // TODO: Font style
                if (columnConfiguration.style.orElse(null) == STYLE.RIGHT_ALIGN) {
                    font.draw(batch, cell, cellPosX, rowPosY, columnConfiguration.width, Align.right, false);
                } else {
                    font.draw(batch, cell, cellPosX, rowPosY);
                }

                cellPosX += columnConfiguration.width;
            }
            rowPosY -= row.getRowHeight().orElse(defaultLineHeight());
        }

        font.setColor(defaultColor);
    }

    public TableRow getColumn(int i) {
        return rows.get(i);
    }
}
