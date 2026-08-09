# PredictionMarketGame

A Java-based desktop stock trading simulation game using U.S. stock market data from the Alpha Vantage API.

The application allows users to practice simulated equity trading and also includes a prediction-based betting feature where users can bet on whether a stock will rise or fall relative to the previous trading day.

## Features

- Search U.S. stocks and ETFs by ticker
- View latest price, daily change, 1-week return, and 1-month return
- Simulated stock buying and selling
- Start with a virtual $10,000 cash balance
- Track portfolio value, holdings, cash, and returns
- Save and reload portfolio and betting history
- Reset the game or restart after bankruptcy

### Stock Direction Betting

Users can bet on whether a stock will move UP or DOWN relative to the previous trading day's close.

- Betting window: 09:30–10:30 ET
- Evaluation: after market close
- Correct prediction: +5% of bet amount
- Incorrect prediction: -100% of bet amount

## Tech Stack

- Java
- Java Swing
- Alpha Vantage API
- Java HttpClient
- File I/O
- Object-Oriented Programming

## Project Structure

```text
StockSimulatorSwing
│
├── StockService
│   └── AlphaVantageStockService
│       └── AlphaVantageApiException
│
└── BetEntry
