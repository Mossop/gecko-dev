// Copyright 2024 Mathias Bynens. All rights reserved.
// This code is governed by the BSD license found in the LICENSE file.

/*---
author: Mathias Bynens
description: >
  Unicode property escapes for `Script_Extensions=Lao`
info: |
  Generated by https://github.com/mathiasbynens/unicode-property-escapes-tests
  Unicode v15.1.0
esid: sec-static-semantics-unicodematchproperty-p
features: [regexp-unicode-property-escapes]
includes: [regExpUtils.js]
---*/

const matchSymbols = buildString({
  loneCodePoints: [
    0x000E84,
    0x000EA5,
    0x000EC6
  ],
  ranges: [
    [0x000E81, 0x000E82],
    [0x000E86, 0x000E8A],
    [0x000E8C, 0x000EA3],
    [0x000EA7, 0x000EBD],
    [0x000EC0, 0x000EC4],
    [0x000EC8, 0x000ECE],
    [0x000ED0, 0x000ED9],
    [0x000EDC, 0x000EDF]
  ]
});
testPropertyEscapes(
  /^\p{Script_Extensions=Lao}+$/u,
  matchSymbols,
  "\\p{Script_Extensions=Lao}"
);
testPropertyEscapes(
  /^\p{Script_Extensions=Laoo}+$/u,
  matchSymbols,
  "\\p{Script_Extensions=Laoo}"
);
testPropertyEscapes(
  /^\p{scx=Lao}+$/u,
  matchSymbols,
  "\\p{scx=Lao}"
);
testPropertyEscapes(
  /^\p{scx=Laoo}+$/u,
  matchSymbols,
  "\\p{scx=Laoo}"
);

const nonMatchSymbols = buildString({
  loneCodePoints: [
    0x000E83,
    0x000E85,
    0x000E8B,
    0x000EA4,
    0x000EA6,
    0x000EC5,
    0x000EC7,
    0x000ECF
  ],
  ranges: [
    [0x00DC00, 0x00DFFF],
    [0x000000, 0x000E80],
    [0x000EBE, 0x000EBF],
    [0x000EDA, 0x000EDB],
    [0x000EE0, 0x00DBFF],
    [0x00E000, 0x10FFFF]
  ]
});
testPropertyEscapes(
  /^\P{Script_Extensions=Lao}+$/u,
  nonMatchSymbols,
  "\\P{Script_Extensions=Lao}"
);
testPropertyEscapes(
  /^\P{Script_Extensions=Laoo}+$/u,
  nonMatchSymbols,
  "\\P{Script_Extensions=Laoo}"
);
testPropertyEscapes(
  /^\P{scx=Lao}+$/u,
  nonMatchSymbols,
  "\\P{scx=Lao}"
);
testPropertyEscapes(
  /^\P{scx=Laoo}+$/u,
  nonMatchSymbols,
  "\\P{scx=Laoo}"
);

reportCompare(0, 0);
