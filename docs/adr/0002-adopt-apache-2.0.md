# ADR 0002: Adopt Apache License 2.0

## Status

Accepted.

## Date

2026-07-31

## Context

Chroniqor is intended to be an open-source project that permits personal,
educational and commercial use.

The project requires a license that:

- Permits use, modification and redistribution.
- Allows commercial adoption.
- Supports contributions from individuals and organizations.
- Includes an explicit patent license.
- Allows Chroniqor to offer managed services, commercial support and
  complementary products in the future.
- Does not require derivative products to disclose all of their own
  modifications.

The project also needs a contribution model that keeps the provenance and
licensing expectations clear without introducing unnecessary legal or
administrative friction.

## Decision

Chroniqor is distributed under the Apache License, Version 2.0.

The SPDX identifier is `Apache-2.0`.

The complete and unmodified license text is included in the root `LICENSE`
file.

Source files created specifically for Chroniqor should contain:

```text
Copyright 2026 Chroniqor contributors
SPDX-License-Identifier: Apache-2.0
```

Third-party code, datasets, documentation and other assets retain their
original licenses and attribution requirements.

## Contribution model

External contributions accepted into Chroniqor are licensed under Apache-2.0.

Chroniqor does not currently require a Developer Certificate of Origin or a
Contributor License Agreement. Contributors must have the right to submit
their work, and accepted contributions are licensed under Apache-2.0. This
lightweight model may be reviewed if external contributions become regular or
the project's legal structure changes.

Accepted contributions may be incorporated, reproduced, modified, distributed,
sublicensed and used commercially as part of Chroniqor in accordance with
Apache License 2.0.

Submitting a contribution does not grant the contributor ownership or
governance authority over the Chroniqor organization, repositories, name,
roadmap, releases or other official project assets.

## Project control

Chroniqor follows a founder-led governance model.

The Project Lead retains final authority over the official project roadmap,
architectural direction, acceptance or rejection of contributions, maintainers,
official releases, repositories, distribution channels and use of the
Chroniqor project identity.

Contributors may participate in discussions and propose changes, but a
contribution does not automatically grant decision-making authority over the
official project.

## Alternatives considered

### MIT License

MIT was not selected because Apache License 2.0 provides more explicit terms
concerning patent rights, contributions, redistribution and attribution
notices.

### GNU AGPL 3.0

AGPL 3.0 was not selected because its network copyleft requirements could reduce
adoption and integration by some individuals and organizations.

### Contributor License Agreement

A CLA was not selected for the initial project because Chroniqor does not
currently require copyright assignment, automatic relicensing rights or a
dual-licensing model. The additional legal and administrative requirements
would be disproportionate during the experimental stage.

## Consequences

Users may use, reproduce, modify and redistribute Chroniqor, including as part
of commercial or closed-source products, provided that they comply with Apache
License 2.0.

Third parties may create commercial products based on Chroniqor without
publishing all of their own modifications. This is an accepted consequence of
choosing a permissive license.

Chroniqor may provide commercial services, managed infrastructure,
professional support, private integrations or complementary products around
the open-source project.

Contributors retain the copyright applicable to their individual
contributions. Chroniqor receives the rights granted through Apache License
2.0 but does not automatically become the exclusive owner of third-party
contributions.

## Future licensing considerations

Chroniqor does not currently plan to adopt a dual-licensing model.

The licensing strategy may be reviewed if the project's governance, ownership
structure or commercial model changes substantially.

A future relicensing or dual-licensing proposal may require consent from
affected contributors, a separate contributor agreement, replacement or
independent reimplementation of affected contributions and legal review.

Versions already released under Apache License 2.0 will remain available under
that license and cannot be retroactively withdrawn from existing recipients.

## Review triggers

This decision should be reviewed before:

- Introducing a dual-licensing model.
- Requiring a Contributor License Agreement.
- Introducing a Developer Certificate of Origin or another contribution
  verification process.
- Forming a legal entity that will own official project assets.
- Transferring or selling significant project assets.
- Major commercialization.
- Version 1.0.
