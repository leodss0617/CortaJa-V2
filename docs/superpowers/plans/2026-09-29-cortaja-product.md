# CortaJá Product Experience Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the visible AutoSub experience with a functional Portuguese CortaJá clip-making product while preserving the existing local media engines.

**Architecture:** Keep `MainActivity` as the host, replace its primary navigation with CortaJá destinations, and add a thin CortaJá presentation/use-case layer over the existing Shorts, Whisper, Gemma, crop, persistence, and export components. Legacy subtitle screens remain internal compatibility surfaces.

**Tech Stack:** Android Java/Kotlin, XML layouts, ViewBinding, LiveData/ViewModel, Material Components, existing Media3/FFmpeg/Whisper/Gemma stack, JUnit and Espresso.

**Spec:** `docs/superpowers/specs/2026-09-29-cortaja-product-design.md`

## Global Constraints

- Preserve Whisper, renderização, model manager, Shorts engine, persistence, and media services.
- Visible product name is `CortaJá`.
- Visible strings must be Brazilian Portuguese resources.
- Primary navigation is `Início`, `Projetos`, `Meus Cortes`, `Configurações`.
- Preview/export must use exact candidate start/end timestamps.
- Do not expose legacy Models/Exports/Generate as primary navigation.

## Review Focus

- A local file picker cancellation must leave the project flow usable; test source selection state.
- Invalid YouTube input must show a human Portuguese error without breaking local-file flow; test validation.
- Empty analyzer results must show a recoverable Portuguese state; test result mapping.
- A candidate with non-zero timestamps must pass exact bounds to preview/export; test use-case mapping.
- Legacy English labels must not appear in primary navigation; test menu/resource scan.

---

### Task 1: CortaJá domain contract and navigation labels

**Files:**
- Create: `app/src/main/java/com/serhat/autosub/cortaja/CortaJaDomain.java`
- Modify: `app/src/main/res/values/strings.xml`, `app/src/main/res/menu/bottom_navigation_menu.xml`, `app/src/main/AndroidManifest.xml`
- Test: `app/src/test/java/com/serhat/autosub/cortaja/CortaJaDomainTest.java`

**Interfaces:**
- Produces immutable source/config/result value objects and Portuguese top-level labels used by UI.

- [ ] Write failing tests for source validation, candidate duration/score display, and four navigation labels.
- [ ] Run the focused unit test and confirm it fails because the domain contract is absent.
- [ ] Implement the minimal domain contract and resources.
- [ ] Run the focused test and then the full JVM test suite.
- [ ] Commit `feat: establish cortaja domain and navigation contract`.

### Task 2: CortaJá home and project setup flow

**Files:**
- Create: `app/src/main/java/com/serhat/autosub/cortaja/ui/HomeFragment.java`, `NewProjectFragment.java`
- Create: `app/src/main/res/layout/fragment_cortaja_home.xml`, `fragment_cortaja_new_project.xml`
- Modify: `MainActivity.java`, `activity_main.xml`
- Test: `app/src/androidTest/java/com/serhat/autosub/cortaja/CortaJaHomeInstrumentedTest.java`

**Interfaces:**
- Consumes domain source/config objects; produces a selected local `Uri` or validated YouTube URL and a configured project request.

- [ ] Add a launcher/home smoke test for `CortaJá`, Portuguese subtitle, and `NOVO PROJETO`.
- [ ] Run it and confirm the old navigation fails the new assertions.
- [ ] Implement home, source selection, project controls, and real local document picker callback.
- [ ] Run the focused instrumentation test and JVM suite.
- [ ] Commit `feat: add cortaja home and new project flow`.

### Task 3: Analysis/result façade over existing Shorts pipeline

**Files:**
- Create: `app/src/main/java/com/serhat/autosub/cortaja/CortaJaViewModel.java`, `CortaJaUseCases.java`, `CortaJaResultFragment.java`
- Create: `app/src/main/res/layout/fragment_cortaja_results.xml`, `item_cortaja_candidate.xml`
- Modify: existing Shorts integration only where required for callbacks.
- Test: `app/src/test/java/com/serhat/autosub/cortaja/CortaJaUseCasesTest.java`

**Interfaces:**
- `analyze(CortaJaDomain.ProjectRequest)` delegates to existing transcript/analyzer/store components and maps `ShortsCandidate` to semantic cards.
- `previewBounds(candidate)` and `exportRequest(candidate)` preserve exact start/end milliseconds.

- [ ] Write failing tests for semantic mapping, exact bounds, and empty-result handling.
- [ ] Run focused tests and observe failure.
- [ ] Implement the façade and result UI, reusing the existing analyzer and project store.
- [ ] Run focused and full tests.
- [ ] Commit `feat: connect cortaja analysis and semantic results`.

### Task 4: My Cuts, settings nesting, and Portuguese migration

**Files:**
- Create/modify CortaJá My Cuts and settings wrapper layouts/fragments.
- Modify all visible primary strings, notification titles, app theme label, model/export entry labels.
- Test: string/menu regression tests and instrumented navigation smoke test.

- [ ] Add failing regression assertions for forbidden primary labels.
- [ ] Implement Portuguese resources and move model entry under settings; label export library `Meus Cortes`.
- [ ] Run resource/string scan and tests.
- [ ] Commit `feat: localize cortaja product experience`.

### Task 5: Verification, report, CI, and artifact

**Files:**
- Modify: `docs/RELATORIO-CODEX.md`, GitHub workflow only if needed.

- [ ] Run complete JVM/instrumentation/build verification available locally.
- [ ] Commit report/checkpoint and push.
- [ ] Monitor GitHub Actions with `gh run list`, `gh run watch`, and failed logs.
- [ ] Correct remote failures, commit, push, and repeat until debug assemble passes.
- [ ] Download artifact, extract APK, copy to `/storage/emulated/0/Download/CortaJa-V2-PTBR.apk`, and calculate SHA256/size.
