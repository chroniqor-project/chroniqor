# Contributing to Chroniqor

Thank you for your interest in contributing to Chroniqor.

Chroniqor is currently in an early experimental stage. Contributions should
preserve the project's direction, reproducibility, financial correctness,
security and modular architecture.

## Before contributing

Before starting significant work:

1. Check the existing issues and pull requests.
2. Open an issue describing the problem or proposal.
3. Wait for confirmation before implementing substantial architectural or
   behavioral changes.
4. Keep the proposed change limited to one clear responsibility.

Opening an issue does not guarantee that a proposal will be accepted.

## Project governance

Chroniqor follows a founder-led governance model while the project remains
experimental. The Project Lead retains final authority over the official
roadmap, architecture, releases and acceptance of contributions.

Technical discussion and contributions are welcome, but submitting a
contribution does not grant ownership, maintainer status or governance
authority over the official Chroniqor project. This model may evolve if the
project develops an active maintainer community.

## Licensing of contributions

Chroniqor is licensed under the Apache License, Version 2.0.

By submitting a contribution to Chroniqor, you agree that it is submitted and
licensed under Apache-2.0, unless a separate written agreement explicitly
states otherwise.

By submitting a contribution, you represent that you created the contribution
or otherwise have the right to submit it to Chroniqor under the project's
license.

Accepted contributions may be incorporated, reproduced, modified, distributed,
sublicensed and used commercially as part of Chroniqor in accordance with the
terms of Apache License 2.0.

Contributors retain the copyright applicable to their individual contributions.
At this stage, Chroniqor does not require a Developer Certificate of Origin or
a Contributor License Agreement. This keeps the initial contribution process
lightweight while the project remains experimental. The contribution model may
be reviewed if external contributions become regular or the project's legal
structure changes.

## Contribution requirements

A contribution should:

- Solve a documented problem.
- Preserve deterministic behavior.
- Avoid access to future market data.
- Pass through the risk engine when producing order intentions.
- Preserve financial and accounting invariants.
- Include appropriate tests.
- Update affected documentation.
- Avoid unrelated refactoring and unnecessary dependencies.
- Respect `docs/legal/data-policy.md`.
- Preserve third-party license notices.

## Deterministic behavior

A pull request must explicitly state whether it changes deterministic
backtesting results.

When a result changes, the pull request must explain why the previous result was
incorrect or incomplete, which execution behavior changed, which datasets or
configurations are affected, whether reference results or hashes changed and
which regression tests were added.

The same strategy, dataset, configuration, engine version and random seed
should produce the same result.

## Financial behavior

Changes affecting balance, equity, profit and loss, margin, exposure, position
sizing, currency conversion, spread, commission, slippage, orders, fills or
risk limits require dedicated tests.

Financial correctness takes priority over implementation convenience.

## Market data

Contributions must comply with `docs/legal/data-policy.md`. Do not submit broker
credentials, private account information, personal financial information,
proprietary market data without redistribution permission or data whose source
and license cannot be established. Synthetic test data is preferred.

## Commit messages

Chroniqor uses Conventional Commits. Examples:

```text
feat(replay): add deterministic market clock
fix(portfolio): correct unrealized pnl conversion
test(risk): cover maximum daily loss
docs(architecture): document module boundaries
refactor(simulation): isolate fill calculation
chore(legal): adopt contribution policy
```

## Pull requests

Pull requests should have a clear title, explain the problem and solution,
identify relevant deterministic or financial changes, include tests and pass
the required checks.

The Project Lead or maintainers may request changes, close a proposal or reject
a contribution that does not fit the project's scope or direction.

## Code of conduct and security

All participants must comply with the project's `CODE_OF_CONDUCT.md`. Do not
report security vulnerabilities through public issues; follow the private
reporting process documented in `SECURITY.md`.
