---
push_when_confident: true
auto_push: true
git:
  push_on_success: true
---

# Project Core Guidelines & Agent Directives

> **CRITICAL AUTOMATION NOTE:** `push_when_confident` is explicitly set to **TRUE**. 
> Before pushing changes directly to the remote repository, you must guarantee that all local compilation, linting, and automated test steps pass with an exit code of `0`.
