#!/usr/bin/env sh

source ./.github/scripts/nav.sh | sed 's| href="| href="../|g'
