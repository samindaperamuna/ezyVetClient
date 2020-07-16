package org.fifthgen.evervet.ezyvet.client.ui.support;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class InfoNode extends GridPane {

    private final Label label;
    private final TextArea textArea;

    public InfoNode() {
        super();

        label = new Label();

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        this.maxWidth(Double.MAX_VALUE);
        this.add(label, 0, 0);
        this.add(textArea, 0, 1);
    }

    public void setLabelText(String text) {
        this.label.setText(text);
    }

    public void setTextAreaText(String text) {
        this.textArea.setText(text);
    }
}
