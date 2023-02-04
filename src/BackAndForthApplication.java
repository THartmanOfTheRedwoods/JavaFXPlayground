import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class BackAndForthApplication extends Application {

    @Override
    public void start(Stage window) {
        Parameters p = getParameters();
        window.setHeight(Double.parseDouble(p.getNamed().get("height")));
        window.setWidth(Double.parseDouble(p.getNamed().get("width")));

        Button back = new Button("Back ..");
        Button forth = new Button(".. forth.");

        Scene first = new Scene(back);
        Scene second = new Scene(forth);

        back.setOnAction((event) -> {
            window.setScene(second);
        });

        forth.setOnAction((event) -> {
            window.setScene(first);
        });

        window.setScene(first);
        window.show();
    }
}