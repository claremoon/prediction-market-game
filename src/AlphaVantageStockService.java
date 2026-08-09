import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class AlphaVantageStockService implements StockService {
    private final String apiKey;

    public AlphaVantageStockService(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public double fetchPrice(String symbol) {
        try {
            String apiUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + apiKey;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) response.append(scanner.nextLine());
            scanner.close();

            JSONObject json = new JSONObject(response.toString());
            JSONObject quote = json.getJSONObject("Global Quote");
            return Double.parseDouble(quote.getString("05. price"));
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public String fetchInfo(String symbol) {
        try {
            String apiUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + symbol + "&apikey=" + apiKey;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) response.append(scanner.nextLine());
            scanner.close();

            JSONObject json = new JSONObject(response.toString());
            JSONObject series = json.getJSONObject("Time Series (Daily)");
            List<String> dates = new ArrayList<>(series.keySet());
            Collections.sort(dates, Collections.reverseOrder());
            double latest = Double.parseDouble(series.getJSONObject(dates.get(0)).getString("4. close"));
            double prev = Double.parseDouble(series.getJSONObject(dates.get(1)).getString("4. close"));
            double change = (latest - prev) / prev * 100;

            return symbol + " 현재가: $" + latest + "\n전일대비: " + String.format("%.2f", change) + "%";
        } catch (Exception e) {
            return "정보 불러오기 실패";
        }
    }
}
