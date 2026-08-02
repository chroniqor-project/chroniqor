# Market data policy

Chroniqor prioritizes reproducibility and lawful redistribution of market
data.

## Permitted data

The repository may contain:

- Synthetic datasets created specifically for tests and examples.
- Data whose license explicitly permits redistribution under the intended
  repository terms.
- Small fixtures required to reproduce a documented test, when their origin
  and license are recorded.

## Required metadata

Every published dataset should identify, when applicable:

- Provider or creator.
- Instrument and market.
- Time range and timezone.
- Resolution and price representation, including bid and ask when available.
- Original license and redistribution conditions.
- Dataset version.
- Integrity hash.

## Prohibited data

Do not commit broker credentials, private account data, personal financial
information or commercial market data that cannot legally be redistributed.

Do not assume that data accessible through an API, trial account or personal
subscription may be published in the repository.

## Corrections and reproducibility

Published datasets are immutable. A correction or transformation that changes
content creates a new dataset version and a new integrity hash. Processing
configuration and assumptions must be recorded so that a backtest can be
reproduced.

This policy does not change the license terms of third-party data. Where a
dataset has separate terms, those terms remain applicable and must be included
in its documentation.
