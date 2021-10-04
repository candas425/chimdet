package ro.utcn.frontend.frontendServices.helper;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import ro.utcn.backend.model.Employee;

import java.time.LocalDate;

import static ro.utcn.helper.GeneralHelper.formatter;

/**
 * Used for date picker cell
 * <p>
 * Created by Lucian on 5/21/2017.
 */
public class DatePickerCellAngajat extends TableCell<Employee, LocalDate> {

    private final DatePicker datePicker;

    public DatePickerCellAngajat() {

        datePicker = new DatePicker();

        datePicker.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                datePicker.setValue(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
                commitEdit(datePicker.getValue());
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });

        datePicker.setDayCellFactory(picker -> {
            DateCell cell = new DateCell();
            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                datePicker.setValue(cell.getItem());
                if (event.getClickCount() == 2) {
                    datePicker.hide();
                    commitEdit(cell.getItem());
                }
                event.consume();
            });
            cell.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    commitEdit(datePicker.getValue());
                }
            });
            return cell;
        });

        contentDisplayProperty().bind(Bindings.when(editingProperty())
                .then(ContentDisplay.GRAPHIC_ONLY)
                .otherwise(ContentDisplay.TEXT_ONLY));
    }

    public void updateItem(LocalDate data, boolean empty) {
        super.updateItem(data, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(data.format(formatter));
            setGraphic(datePicker);
        }
    }

}
