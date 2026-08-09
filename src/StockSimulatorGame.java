import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.time.*;
import java.util.*;

public class StockSimulatorGame extends Application {
    private double balance = 10000.0;
    private final StockService stockService = new AlphaVantageStockService("YOUR_API_KEY_HERE");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LocalTime now = LocalTime.now(ZoneId.of("America/New_York"));
        boolean bettingAllowed = now.isAfter(LocalTime.of(9, 30)) && now.isBefore(LocalTime.of(10, 30));

        showAlert("Welcome", "주식 시뮬레이터에 오신 것을 환영합니다!\n\n잔고: $" + balance);
        showStartingDashboard();

        TabPane tabPane = new TabPane();

        Tab tradeTab = new Tab("💰 매매 모드", createTradePane());
        Tab betTab = new Tab("🎲 베팅 모드", bettingAllowed ? createBetPane() : new Label("지금은 베팅이 불가능한 시간입니다."));
        tradeTab.setClosable(false);
        betTab.setClosable(false);

        tabPane.getTabs().addAll(tradeTab, betTab);

        VBox root = new VBox(tabPane);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Stock Trading & Betting Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createTradePane() {
        Label balanceLabel = new Label("잔고: $" + balance);
        TextField symbolField = new TextField();
        symbolField.setPromptText("회사 이름 또는 종목 코드 입력 (예: AAPL)");
        Label infoLabel = new Label();

        Button searchBtn = new Button("검색");
        searchBtn.setOnAction(e -> {
            String symbol = symbolField.getText().toUpperCase();
            String info = stockService.fetchInfo(symbol);
            infoLabel.setText(info);
        });

        Button buyBtn = new Button("매수");
        buyBtn.setOnAction(e -> {
            String symbol = symbolField.getText().toUpperCase();
            int qty = Integer.parseInt(askText("매수 수량 입력"));
            double price = stockService.fetchPrice(symbol);
            double cost = qty * price;
            if (cost <= balance) {
                balance -= cost;
                showAlert("매수 성공", symbol + " 주식 " + qty + "주 매수 완료. 총액: $" + String.format("%.2f", cost));
            } else {
                showAlert("잔고 부족", "잔고가 부족합니다.");
            }
        });

        Button sellBtn = new Button("매도");
        sellBtn.setOnAction(e -> {
            String symbol = symbolField.getText().toUpperCase();
            int qty = Integer.parseInt(askText("매도 수량 입력"));
            double price = stockService.fetchPrice(symbol);
            double revenue = qty * price;
            balance += revenue;
            showAlert("매도 성공", symbol + " 주식 " + qty + "주 매도 완료. 총액: $" + String.format("%.2f", revenue));
        });

        Button betBtn = new Button("베팅");
        betBtn.setOnAction(e -> {
            LocalTime now = LocalTime.now(ZoneId.of("America/New_York"));
            if (!(now.isAfter(LocalTime.of(9, 30)) && now.isBefore(LocalTime.of(10, 30)))) {
                showAlert("시간 제한", "지금은 베팅이 불가능한 시간입니다.");
                return;
            }
            String symbol = symbolField.getText().toUpperCase();
            String direction = askText("상승 또는 하락 입력").toLowerCase();
            double amount = Double.parseDouble(askText("베팅 금액 입력"));
            if (amount > balance) {
                showAlert("잔고 부족", "베팅 금액이 잔고를 초과합니다.");
                return;
            }
            boolean correct = direction.equals("상승");
            double result = correct ? amount * 0.05 : -amount;
            balance += result;
            showAlert("베팅 결과", correct ? "성공! 수익: $" + result : "실패! 손실: $" + amount);
        });

        Button exitBtn = new Button("종료");
        exitBtn.setOnAction(e -> System.exit(0));

        VBox box = new VBox(10, balanceLabel, symbolField, searchBtn, infoLabel, buyBtn, sellBtn, betBtn, exitBtn);
        box.setPadding(new Insets(20));
        return box;
    }

    private VBox createBetPane() {
        return new VBox(); // dummy placeholder
    }

    private void showStartingDashboard() {
        showAlert("포트폴리오", "현재 잔고: $" + balance + "\n\n(포트폴리오는 추후 확장 예정)");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String askText(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("입력 필요");
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        Optional<String> result = dialog.showAndWait();
        return result.orElse("");
    }
}
