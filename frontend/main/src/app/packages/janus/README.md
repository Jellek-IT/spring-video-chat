# Modifications in the Janus JS Library

## Overview

This document highlights the modifications made to the `janus` library to address a limitation with the `token` variable, which could not be changed once set during object construction. To make the library more flexible and accommodate dynamic token updates, the `token` variable has been replaced with a `callback` mechanism. Additionally, an enhancement has been introduced to the `onlocaltrack` event, where the `mid` (Media ID) is now provided to simplify client code.

## Issue with the Original Implementation

In the original implementation of the `janus` library:

- The `token` variable was defined as a fixed value that had to be passed during the initialization.
- Once the `token` was set, it could not be updated, making it unsuitable for scenarios where the token needs to be refreshed or replaced dynamically (e.g., in long-running applications that rely on expiring access tokens).
- The `onlocaltrack` event did not include the `mid` (Media ID), making it less convenient for developers to manage tracks and correlate them with their respective streams.

## Modifications Made

1. **`token` Variable Replaced with `callback`:**

   - The `token` attribute has been replaced with a `callback` function.
   - The `callback` function is expected to return the current token when invoked.
   - This change allows the token to be dynamically updated by the application without requiring a full reinitialization of objects.

2. **`mid` Added to `onlocaltrack`:**
   - The `onlocaltrack` event now provides the `mid` (Media ID) as part of its parameters.
   - This enhancement simplifies client code by allowing developers to easily identify and manage tracks associated with specific media streams.
