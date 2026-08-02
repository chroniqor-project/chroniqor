# Chroniqor

Chroniqor is an open-source, deterministic and auditable engine for
algorithmic trading research and historical market simulation.

The project is currently in an early experimental stage. Version 0.1 focuses
on building a local and reproducible Forex backtesting engine using historical
bid and ask market data.

## Project status

Chroniqor is under initial development. The current repository contains the
base project configuration; the core trading functionality has not yet been
implemented.

Chroniqor is not ready for production use, demo trading or real-money trading.

## Initial goals

Chroniqor 0.1 aims to provide:

- Historical Forex market data import.
- Versioned and reproducible datasets.
- Bid and ask price processing.
- Deterministic historical replay.
- Strategy execution.
- Mandatory risk evaluation.
- Market, limit and stop order simulation.
- Portfolio, balance and equity calculations.
- Configurable spread, commission and slippage.
- Metrics and final reports.
- Complete execution auditing.
- Reproducible backtest results.

## Out of scope for version 0.1

The first version will not include:

- Broker integrations.
- Real-time market data.
- Demo trading.
- Real-money trading.
- Microservices.
- Mobile applications.
- Machine learning.
- Copy trading.
- Trading signals.
- Strategy marketplaces.
- Management of third-party funds.

## Development principles

1. Strategies propose operations but never send orders directly.
2. Every order intention must pass through the risk engine.
3. Historical executions must not access future market data.
4. The same inputs must produce the same results.
5. Published datasets must be immutable and versioned.
6. Financial calculations must be explicit and testable.
7. Every relevant decision must be auditable.
8. Real-money trading is outside the initial project scope.
9. Security and risk management take priority over development speed.
10. New infrastructure must be justified by a current requirement.

## Building the project

Requirements:

- Java 21 or a compatible configured toolchain.
- Git.
- Docker or a compatible container runtime for future integration tests.

On Windows:

```powershell
.\gradlew.bat clean check
```

On Linux and macOS:

```bash
./gradlew clean check
```

### Optional local formatting hook

Chroniqor includes an optional Git hook that checks Spotless before each
commit. If formatting problems are found, it applies the formatter and stops
the commit so the changes can be reviewed and staged explicitly.

Enable it locally with:

```bash
git config core.hooksPath .githooks
```

The hook is a local developer convenience; CI remains the authoritative
formatting check.
