#!/usr/bin/env bash

PROMPT="Analyze the Bytehot project. It follows DDD and Hexagonal architecture, plus EDA. First, make sure all Github Actions pipelines are correct. They are failing now. Next, make sure the repositories are refactored to follow the latest changes in javaeda-domain, javaeda-infrastructure and javaeda-application"
npx claude-flow@alpha hive-mind spawn "${PROMPT}" --agents 10 --strategy development --claude --memory-namespace "metaphysical"
